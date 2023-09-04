package com.spongzi.train.business.controller.admin;

import com.spongzi.train.business.req.DailyTrainQueryReq;
import com.spongzi.train.business.req.DailyTrainSaveReq;
import com.spongzi.train.business.resp.DailyTrainQueryResp;
import com.spongzi.train.business.service.DailyTrainService;
import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

    @Resource
    private DailyTrainService dailyTrainService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req) {
        dailyTrainService.save(req);
        return CommonResp.builder().build();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryReq req) {
        PageResp<DailyTrainQueryResp> list = dailyTrainService.queryList(req);
        return CommonResp.<PageResp<DailyTrainQueryResp>>builder().content(list).build();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        dailyTrainService.delete(id);
        return CommonResp.builder().build();
    }

    @GetMapping("/gen-daily/{date}")
    public CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        dailyTrainService.genDaily(date);
        return CommonResp.builder().build();
    }

}
