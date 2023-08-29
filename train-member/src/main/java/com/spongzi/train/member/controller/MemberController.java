package com.spongzi.train.member.controller;

import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.member.domain.req.MemberLoginReq;
import com.spongzi.train.member.domain.req.MemberRegisterReq;
import com.spongzi.train.member.domain.req.MemberSendCodeReq;
import com.spongzi.train.member.domain.resp.MemberLoginResp;
import com.spongzi.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 成员管理 api
 *
 * @author spong
 * @date 2023/08/29
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/count")
    public CommonResp<Long> count() {
        return CommonResp.<Long>builder().content(memberService.count()).build();
    }

    @PostMapping("/registry")
    public CommonResp<Long> registry(@RequestBody @Valid MemberRegisterReq req) {
        return CommonResp.<Long>builder().content(memberService.registry(req)).message("注册成功！").build();
    }

    @PostMapping("/send-code")
    public CommonResp<Object> sendCode(@RequestBody @Valid MemberSendCodeReq req) {
        memberService.sendCode(req);
        return CommonResp.builder().message("发送验证码成功！").build();
    }

    @PostMapping("/login")
    public CommonResp<MemberLoginResp> Login(@RequestBody @Valid MemberLoginReq req) {
        MemberLoginResp loginResp = memberService.login(req);
        return CommonResp.<MemberLoginResp>builder().content(loginResp).message("用户登录成功").build();
    }
}
