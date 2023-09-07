package com.spongzi.train.business.service;

import com.spongzi.train.business.domain.DailyTrainSeat;
import com.spongzi.train.business.mapper.DailyTrainSeatMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;

    /* 对选中的数据进行事务处理：

         座位表修改售卖情况
         余票详情表修改余票
         为会员增加购票记录
         更新确认订单表位成功
    */
    @Transactional
    public void afterDoConfirm(List<DailyTrainSeat> finalSeatList) {
        for (DailyTrainSeat dailyTrainSeat: finalSeatList) {
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);
        }
    }
}
