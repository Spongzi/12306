package com.spongzi.train.common.context;

import com.spongzi.train.common.resp.MemberLoginResp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginMemberContext {
    private static final ThreadLocal<MemberLoginResp> member = new ThreadLocal<>();

    public static MemberLoginResp getMember() {
        return member.get();
    }

    public static void setMember(MemberLoginResp resp) {
        LoginMemberContext.member.set(resp);
    }

    public static Long getId() {
        try {
            return getMember().getId();
        } catch (Exception e) {
            log.error("获取登录信息异常", e);
            throw e;
        }
    }
}
