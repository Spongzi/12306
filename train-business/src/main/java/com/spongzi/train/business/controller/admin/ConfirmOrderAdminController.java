package com.spongzi.train.business.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import com.spongzi.train.business.req.ConfirmOrderDoReq;
import com.spongzi.train.business.service.ConfirmOrderService;
import com.spongzi.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderAdminController {

    @Resource
    private ConfirmOrderService confirmOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {

        // 图形验证码校验
        String imageCodeToken = req.getImageCodeToken();
        String imageCode = req.getImageCode();
        String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
        log.info("从redis中获取到的验证码：{}", imageCodeRedis);
        if (ObjectUtil.isEmpty(imageCodeRedis)) {
            return CommonResp.builder().success(false).message("验证码不正确").build();
        }
        // 验证校验码，大小写忽略
        if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
            return CommonResp.builder().success(false).message("验证码不正确").build();
        }
        // 验证码通过，移除验证码
        redisTemplate.delete(imageCodeToken);

        confirmOrderService.doConfirm(req);
        return CommonResp.builder().build();
    }

}
