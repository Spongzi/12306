package com.spongzi.train.member;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.spongzi.*"})
@MapperScan("com.spongzi.train.member.mapper")
public class TrainMemberApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TrainMemberApplication.class);
        Environment env = app.run(args).getEnvironment();
        log.info("启动成功!");
        log.info("会员模块地址：\thttp://localhost:{}", env.getProperty("server.port"));
    }
}
