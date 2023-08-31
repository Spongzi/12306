package com.spongzi.train.member.controller;

import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.member.domain.req.PassengerSaveReq;
import com.spongzi.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> save(PassengerSaveReq req) {
        passengerService.save(req);
        return CommonResp.builder().build();
    }
}
