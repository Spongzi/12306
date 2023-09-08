package com.spongzi.train.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.spongzi")
@EnableFeignClients("com.spongzi.train.batch.feign")
public class TrainBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainBatchApplication.class, args);
    }
}
