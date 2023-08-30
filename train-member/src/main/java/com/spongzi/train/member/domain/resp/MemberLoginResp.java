package com.spongzi.train.member.domain.resp;

import lombok.Data;

/**
 * 会员登录
 *
 * @author spong
 * @date 2023/08/29
 */
@Data
public class MemberLoginResp {
    private Long id;

    private String mobile;

    private String token;
}
