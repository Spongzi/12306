package com.spongzi.train.batch.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.spongzi.train.batch.feign.BusinessFeign;
import com.spongzi.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

@Slf4j
@DisallowConcurrentExecution // 禁用并发执行
public class DailayTrainJob implements Job {

    @Resource
    private BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("生成15天后的车次数据开始");
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, 15);
        Date offsetDate = dateTime.toJdkDate();
        CommonResp<Object> commonResp = businessFeign.genDaily(offsetDate);
        log.info("生成15天后的车次数据结束， 结果：{}", commonResp);
    }
}
