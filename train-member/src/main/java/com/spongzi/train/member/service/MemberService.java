package com.spongzi.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.spongzi.train.common.exception.BusinessException;
import com.spongzi.train.member.domain.Member;
import com.spongzi.train.member.domain.MemberExample;
import com.spongzi.train.member.domain.req.MemberRegisterReq;
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
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
