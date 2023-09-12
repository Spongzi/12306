package com.spongzi.train.business;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.spongzi.*"})
@MapperScan("com.spongzi.train.*.mapper")
@EnableFeignClients("com.spongzi.train.business.feign")
@EnableCaching
public class TrainBusinessApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TrainBusinessApplication.class);
        Environment env = app.run(args).getEnvironment();
        log.info("启动成功!");
        log.info("会员模块地址：\thttp://localhost:{}", env.getProperty("server.port"));
        // 启动成功后，运行限流规则
        initFlowRules();
        log.info("已定义限流规则");
    }

    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("doConfirm");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // set limit QPS to 20
        rule.setCount(20);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
