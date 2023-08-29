package com.spongzi.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.spongzi.train.member.domain.Member;
import com.spongzi.train.member.domain.MemberExample;
import com.spongzi.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * @param mobile 移动
     * @return {@link Long} 返回注册用户的id
     */
    public Long registry(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);

        List<Member> members = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(members)) {
            throw new RuntimeException("mobile is registry");
        }

        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
