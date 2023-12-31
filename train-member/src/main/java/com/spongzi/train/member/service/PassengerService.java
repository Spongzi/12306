package com.spongzi.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spongzi.train.common.context.LoginMemberContext;
import com.spongzi.train.common.resp.PageResp;
import com.spongzi.train.common.utils.SnowUtil;
import com.spongzi.train.member.domain.Passenger;
import com.spongzi.train.member.domain.PassengerExample;
import com.spongzi.train.member.req.PassengerQueryReq;
import com.spongzi.train.member.req.PassengerSaveReq;
import com.spongzi.train.member.resp.PassengerQueryResp;
import com.spongzi.train.member.mapper.PassengerMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    public void save(PassengerSaveReq req) {
        DateTime now = DateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        // 如果是空新增保存，如果不是空就是修改保存
        if (ObjectUtil.isNull(passenger.getId())) {
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
            return;
        }
        passenger.setUpdateTime(now);
        passengerMapper.updateByPrimaryKeySelective(passenger);
    }

    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if (ObjectUtil.isNotNull(req.getMemberId())) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Passenger> passengers = passengerMapper.selectByExample(passengerExample);
        PageInfo<Passenger> pageInfo = new PageInfo<>(passengers);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

        List<PassengerQueryResp> passengerQueryResps = BeanUtil.copyToList(passengers, PassengerQueryResp.class);

        // 封装分页参数
        PageResp<PassengerQueryResp> passengerQueryRespPageResp = new PageResp<>();
        passengerQueryRespPageResp.setList(passengerQueryResps);
        passengerQueryRespPageResp.setTotal(pageInfo.getTotal());
        return passengerQueryRespPageResp;
    }

    public void delete(Long id) {
        passengerMapper.deleteByPrimaryKey(id);
    }

    public List<PassengerQueryResp> queryMine() {
        PassengerExample example = new PassengerExample();
        example.setOrderByClause("name asc");
        example.createCriteria().andMemberIdEqualTo(LoginMemberContext.getId());
        List<Passenger> passengers = passengerMapper.selectByExample(example);
        return BeanUtil.copyToList(passengers, PassengerQueryResp.class);
    }
}