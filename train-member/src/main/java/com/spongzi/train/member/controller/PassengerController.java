package com.spongzi.train.member.controller;

import com.spongzi.train.common.context.LoginMemberContext;
import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.member.domain.req.PassengerQueryReq;
import com.spongzi.train.member.domain.req.PassengerSaveReq;
import com.spongzi.train.member.domain.resp.PassengerQueryResp;
import com.spongzi.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> save(@RequestBody PassengerSaveReq req) {
        passengerService.save(req);
        return CommonResp.builder().build();
    }

    @GetMapping("/query-list")
    public CommonResp<List<PassengerQueryResp>> query(PassengerQueryReq req) {
        // 设置会员参数，可以减少前端的传递
        req.setMemberId(LoginMemberContext.getId());
        return CommonResp.<List<PassengerQueryResp>>builder()
                .content(passengerService.queryList(req))
                .message("查询成功！")
                .build();
    }
}
