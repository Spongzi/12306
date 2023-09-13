package com.spongzi.train.business.controller.admin;

import com.spongzi.train.business.req.SkTokenQueryReq;
import com.spongzi.train.business.req.SkTokenSaveReq;
import com.spongzi.train.business.resp.SkTokenQueryResp;
import com.spongzi.train.business.service.SkTokenService;
import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sk-token")
public class SkTokenAdminController {

    @Resource
    private SkTokenService skTokenService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody SkTokenSaveReq req) {
        skTokenService.save(req);
        return CommonResp.builder().build();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<SkTokenQueryResp>> queryList(@Valid SkTokenQueryReq req) {
        PageResp<SkTokenQueryResp> list = skTokenService.queryList(req);
        return CommonResp.<PageResp<SkTokenQueryResp>>builder().content(list).build();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        skTokenService.delete(id);
        return CommonResp.builder().build();
    }

}
