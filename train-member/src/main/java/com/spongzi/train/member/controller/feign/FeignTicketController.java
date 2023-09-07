package com.spongzi.train.member.controller.feign;

import com.spongzi.train.common.req.MemberTicketReq;
import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {

    @Resource
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req) throws Exception {
        ticketService.save(req);
        return CommonResp.builder().build();
    }

}
