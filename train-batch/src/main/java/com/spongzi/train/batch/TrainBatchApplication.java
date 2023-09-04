package com.spongzi.train.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.spongzi.train.batch.feign")
public class TrainBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainBatchApplication.class, args);
    }
}
