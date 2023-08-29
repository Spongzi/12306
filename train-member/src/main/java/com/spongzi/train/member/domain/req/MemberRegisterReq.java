package com.spongzi.train.member.domain.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
    @NotBlank(message = "【手机号】不能为空")
    @Length(min = 11, max = 11, message = "【手机号】错误")
    private String mobile;
}
