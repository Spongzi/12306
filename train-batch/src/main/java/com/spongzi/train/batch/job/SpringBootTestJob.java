package com.spongzi.train.batch.job;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 只适合单体项目，不适合集群
 * 没法事实更改定时任务和策略
 */
@Component
@EnableScheduling
public class SpringBootTestJob {

    @Scheduled(cron = "0/5 * * * * ?")
    private void test() {
        // 增加分布式锁，解决集群问题
        System.out.println("SpringBootTestJob");
    }
}
