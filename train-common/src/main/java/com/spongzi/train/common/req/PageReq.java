package com.spongzi.train.common.req;

import jakarta.validation.constraints.Max;
import lombok.Data;

/**
 * 页面请求
 *
 * @author spong
 * @date 2023/08/31
 */
@Data
public class PageReq {
    private Integer page;

    @Max(value = 100, message = "【每条页数】不超过100")
    private Integer size;
}
