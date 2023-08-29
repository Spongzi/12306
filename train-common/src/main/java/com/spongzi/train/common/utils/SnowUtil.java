package com.spongzi.train.common.utils;

import cn.hutool.core.util.IdUtil;

/**
 * 封装hutool雪花算法
 *
 * @author spong
 * @date 2023/08/29
 */
public class SnowUtil {

    private static final long dataCenterId = 1;  //数据中心
    private static final long workerId = 1;     //机器标识

    public static long getSnowflakeNextId() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextId();
    }

    public static String getSnowflakeNextIdStr() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextIdStr();
    }
}
