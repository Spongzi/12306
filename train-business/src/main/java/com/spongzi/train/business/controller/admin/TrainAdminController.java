package com.spongzi.train.business.controller.admin;

import com.spongzi.train.common.context.LoginMemberContext;
import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.common.resp.PageResp;
import com.spongzi.train.business.req.TrainQueryReq;
import com.spongzi.train.business.req.TrainSaveReq;
import com.spongzi.train.business.resp.TrainQueryResp;
import com.spongzi.train.business.service.TrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

    @Resource
    private TrainService trainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req) {
        trainService.save(req);
        return CommonResp.builder().build();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req) {
        PageResp<TrainQueryResp> list = trainService.queryList(req);
        return CommonResp.<PageResp<TrainQueryResp>>builder().content(list).build();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        trainService.delete(id);
        return CommonResp.builder().build();
    }

}
