package com.spongzi.train.business.controller.admin;

import com.spongzi.train.business.req.StationQueryReq;
import com.spongzi.train.business.req.StationSaveReq;
import com.spongzi.train.business.resp.StationQueryResp;
import com.spongzi.train.business.service.StationService;
import com.spongzi.train.business.service.TrainSeatService;
import com.spongzi.train.common.resp.CommonResp;
import com.spongzi.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/station")
public class StationAdminController {

    @Resource
    private StationService stationService;

    @Resource
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req) {
        stationService.save(req);
        return CommonResp.builder().build();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req) {
        PageResp<StationQueryResp> list = stationService.queryList(req);
        return CommonResp.<PageResp<StationQueryResp>>builder().content(list).build();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        stationService.delete(id);
        return CommonResp.builder().build();
    }

    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryAll() {
        return CommonResp.<List<StationQueryResp>>builder()
                .content(stationService.queryAll())
                .build();
    }

    @GetMapping("/gen-seat/{trainCode}")
    public CommonResp<Object> genSeat(@PathVariable String trainCode) {
        trainSeatService.genTrainSeat(trainCode);
        return CommonResp.builder()
                .message("生成座位成功！")
                .build();
    }

}
