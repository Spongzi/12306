package com.spongzi.train.member.controller;

import com.spongzi.train.common.context.LoginMemberContext;
import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.common.resp.PageResp;
import com.spongzi.train.member.req.TicketQueryReq;
import com.spongzi.train.member.resp.TicketQueryResp;
import com.spongzi.train.member.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;


    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> query(@Valid TicketQueryReq req) {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<TicketQueryResp> pageResp = ticketService.queryList(req);
        return CommonResp.<PageResp<TicketQueryResp>>builder().content(pageResp).build();
    }

}
