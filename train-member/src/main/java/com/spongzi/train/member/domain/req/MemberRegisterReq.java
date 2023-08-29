package com.spongzi.train.member.domain.req;

import lombok.Data;

/**
 * 会员注册申请
 *
 * @author spong
 * @date 2023/08/29
 */
@Data
public class MemberRegisterReq {

    /**
     * 手机号
     */
    private String mobile;
}
