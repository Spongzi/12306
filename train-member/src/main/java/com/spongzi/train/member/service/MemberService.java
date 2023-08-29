package com.spongzi.train.member.service;

import com.spongzi.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    /**
     * 会员总量
     *
     * @return int
     */
    public int count() {
        return memberMapper.count();
    }
}
