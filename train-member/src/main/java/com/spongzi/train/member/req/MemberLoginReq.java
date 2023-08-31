package com.spongzi.train.member.req;

import lombok.Data;

/**
 * 会员登录请求
 *
 * @author spong
 * @date 2023/08/29
 */
@Data
public class MemberLoginReq {

    private String mobile;

    private String code;

}
