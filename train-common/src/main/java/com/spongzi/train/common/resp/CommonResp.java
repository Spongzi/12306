package com.spongzi.train.common.resp;

import lombok.Builder;
import lombok.Data;

/**
 * 同意返回类
 *
 * @author spong
 * @date 2023/08/29
 */
@Data
@Builder
public class CommonResp<T> {

    /**
     * 业务上的成功或失败
     */
    @Builder.Default
    private boolean success = true;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回泛型数据，自定义类型
     */
    private T content;
}
