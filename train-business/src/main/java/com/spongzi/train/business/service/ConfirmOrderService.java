package com.spongzi.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spongzi.train.business.domain.*;
import com.spongzi.train.business.enums.SeatColEnum;
import com.spongzi.train.business.enums.SeatTypeEnum;
import com.spongzi.train.business.mapper.ConfirmOrderMapper;
import com.spongzi.train.business.req.ConfirmOrderDoReq;
import com.spongzi.train.business.req.ConfirmOrderQueryReq;
import com.spongzi.train.business.req.ConfirmOrderSaveReq;
import com.spongzi.train.business.req.ConfirmOrderTicketReq;
import com.spongzi.train.business.resp.ConfirmOrderQueryResp;
import com.spongzi.train.common.context.LoginMemberContext;
import com.spongzi.train.common.exception.BusinessException;
import com.spongzi.train.common.exception.BusinessExceptionEnum;
import com.spongzi.train.common.resp.PageResp;
import com.spongzi.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.spongzi.train.business.enums.ConfirmOrderStatusEnum.INIT;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    public void save(ConfirmOrderSaveReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void doConfirm(ConfirmOrderDoReq req) {
        // 省略业务校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客是否已经买过

        Date now = DateTime.now();
        // 保存确认订单，状态初始
        ConfirmOrder confirmOrder = new ConfirmOrder();
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        List<ConfirmOrderTicketReq> tickets = req.getTickets();
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);

        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);

        //  扣减余票数量，并判断余票数是否充足
        reduceTickets(req, dailyTrainTicket);

        // 计算相对第一个座位的偏移值
        // 判断是否进行选座
        ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
        if (StrUtil.isBlank(ticketReq0.getSeat())) {
            LOG.info("本次购票没有选座");
            for (ConfirmOrderTicketReq ticketReq : tickets) {
                getSeat(date, trainCode, ticketReq0.getSeatTypeCode(), null, null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            }
        } else {
            LOG.info("本次购票有选座");
            // 查出本次选座的类型有哪些，用于计算所选座位与第一个座位的偏离值
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
            LOG.info("本次选座的座位类型包含的列：{}", colEnumList);
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i < 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode() + i);
                }
            }
            LOG.info("用于做参照的两排座位：{}", referSeatList);

            // 绝对偏移值
            List<Integer> absloteOffsetList = new ArrayList<>();
            for (ConfirmOrderTicketReq ticketReq : tickets) {
                int index = referSeatList.indexOf(ticketReq.getSeat());
                absloteOffsetList.add(index);
            }
            LOG.info("计算得到所有座位的的绝对偏移值：{}", absloteOffsetList);
            // 计算相对偏移值
            List<Integer> offsetList = new ArrayList<>();
            for (Integer index : absloteOffsetList) {
                int offset = index - absloteOffsetList.get(0);
                offsetList.add(offset);
            }
            LOG.info("计算得到所有座位的的相对偏移值：{}", absloteOffsetList);

            getSeat(date, trainCode, ticketReq0.getSeatTypeCode(), ticketReq0.getSeat().split(" ")[0], offsetList, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
        }

        // 选座

        // 一个车厢一个车厢的获取

        // 挑选符合条件的座位，如果这个车厢不满足，则进入下一个车厢（多个选座应该在同一个车厢内）

        // 对选中的数据进行事务处理：

        // 座位表修改售卖情况
        // 余票详情表修改余票
        // 为会员增加购票记录
        // 更新确认订单表位成功
    }

    /**
     * 挑选座位，如果有座位的话一次性调完，如果无座位，则一个一个挑
     *
     * @param date
     * @param trainCode
     * @param seatType
     * @param col
     * @param offsetList
     */
    private void getSeat(Date date, String trainCode, String seatType, String col, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查处{}个符合条件的车厢", carriageList.size());

        // 一个车厢一个车厢的获取
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("开始从车厢{}选座", dailyTrainCarriage);
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位号:{}", dailyTrainCarriage.getIndex(), seatList.size());
            for (DailyTrainSeat dailyTrainSeat : seatList) {
                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    LOG.info("选中座位");
                    return;
                } else {
                    LOG.info("未选中座位");
                    continue;
                }
            }
        }
    }

    /**
     * 计算某座位区间是否可卖
     *
     * @param dailyTrainSeat 每日列车座位
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        String sell = dailyTrainSeat.getSell();
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageIndex(), startIndex, endIndex);
            return false;
        } else {
            LOG.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位", dailyTrainSeat.getCarriageIndex(), startIndex, endIndex);
            String curSell = sellPart.replace('0', '1');
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区域的信息curCell与库里的已售信息sell按位或，即可得到改座位卖出此票后的售票情况
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息: {}，车站区间:{} ~ {}，即: {}，最终售票信息: {}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;
        }
    }

    /**
     * 扣减余票数量
     *
     * @param req
     * @param dailyTrainTicket
     */
    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
        }
    }
}
