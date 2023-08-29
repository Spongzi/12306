package com.spongzi.train.common.exception;

import com.spongzi.train.common.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理、数据预处理等
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 所有异常统一处理
     *
     * @param e 拦截到的错误信息
     * @return 返回抛出的异常信息
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp<Object> exceptionHandler(Exception e) {
        return CommonResp.builder().success(false).message("系统出现异常，请联系管理员").build();
    }

    /**
     * 业务异常统一处理
     *
     * @param e 拦截到的错误信息
     * @return 返回抛出的一信息
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp<Object> exceptionHandler(BusinessException e) {
        return CommonResp.builder().success(false).message(e.getE().getDesc()).build();
    }

    /**
     * 校验异常统一处理
     *
     * @param e 拦截到的错误信息
     * @return 返回抛出的一信息
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp<Object> exceptionHandler(BindException e) {
        LOG.error("校验异常：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return CommonResp.builder()
                .success(false)
                .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build();
    }

}
