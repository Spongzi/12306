package com.spongzi.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.spongzi.train.common.exception.BusinessException;
import com.spongzi.train.common.utils.JwtUtil;
import com.spongzi.train.common.utils.SnowUtil;
import com.spongzi.train.member.domain.Member;
import com.spongzi.train.member.domain.MemberExample;
import com.spongzi.train.member.req.MemberLoginReq;
import com.spongzi.train.member.req.MemberRegisterReq;
import com.spongzi.train.member.req.MemberSendCodeReq;
import com.spongzi.train.member.resp.MemberLoginResp;
import com.spongzi.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.spongzi.train.common.exception.BusinessExceptionEnum.*;

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
        Member memberByDb = selectMembersByMobile(mobile);
        if (ObjectUtil.isNotNull(memberByDb)) {
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
        Member memberByDb = selectMembersByMobile(mobile);
        if (ObjectUtil.isNull(memberByDb)) {
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

    public MemberLoginResp login(MemberLoginReq req) {
        // 获取当前请求体中的数据
        String mobile = req.getMobile();
        String code = req.getCode();

        // 构造查询信息
        Member memberByDb = selectMembersByMobile(mobile);

        // 如果手机号不存在, 抛出异常
        if (ObjectUtil.isNull(memberByDb)) {
            throw new BusinessException(MEMBER_MOBILE_NOT_EXIST);
        }

        // 校验短信验证码，正常通过数据库
        if (!"8888".equals(code)) {
            throw new BusinessException(MEMBER_MOBILE_CODE_ERROR);
        }

        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(memberByDb, MemberLoginResp.class);
        String token = JwtUtil.createToken(memberLoginResp.getId(), memberLoginResp.getMobile());
        memberLoginResp.setToken(token);
        return memberLoginResp;
    }

    private Member selectMembersByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isEmpty(members)) {
            return null;
        }
        return members.get(0);
    }
}
