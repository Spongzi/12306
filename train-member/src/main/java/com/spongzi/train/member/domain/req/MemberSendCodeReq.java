package com.spongzi.train.member.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 成员发送代码申请
 *
 * @author spong
 * @date 2023/08/29
 */
@Data
public class MemberSendCodeReq {

    @NotBlank(message = "【手机号】不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String mobile;

}
