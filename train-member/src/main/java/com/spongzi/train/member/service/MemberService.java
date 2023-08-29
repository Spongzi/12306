package com.spongzi.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.spongzi.train.common.exception.BusinessException;
import com.spongzi.train.common.utils.SnowUtil;
import com.spongzi.train.member.domain.Member;
import com.spongzi.train.member.domain.MemberExample;
import com.spongzi.train.member.domain.req.MemberRegisterReq;
import com.spongzi.train.member.domain.req.MemberSendCodeReq;
import com.spongzi.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.spongzi.train.common.exception.BusinessExceptionEnum.MEMBER_MOBILE_EXIST;

@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    /**
     * 会员总量
     *
     * @return int
     */
    public Long count() {
        return memberMapper.countByExample(null);
    }

    /**
     * 注册表
     *
     * @param req 会员注册申请数据
     * @return {@link Long}
     */
    public Long registry(MemberRegisterReq req) {
        // 获取当前请求体中的数据
        String mobile = req.getMobile();

        // 构造查询信息
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);

        // 如果当前手机号已经被注册，抛出异常
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(members)) {
            throw new BusinessException(MEMBER_MOBILE_EXIST);
        }

        // 创建用户并且添加到数据库中
        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    /**
     * 发送代码
     *
     * @param req 要求事情
     * @return {@link Long}
     */
    public void sendCode(MemberSendCodeReq req) {
        // 获取当前请求体中的数据
        String mobile = req.getMobile();

        // 构造查询信息
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);

        // 如果手机号不存在，则插入一条记录
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isEmpty(members)) {
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        }

        // 生成验证码
        // String code = RandomUtil.randomString(4);
        String code = "8888";

        // 保存短信记录表：手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间

        // 对接短信通道，发送短信
    }
}
