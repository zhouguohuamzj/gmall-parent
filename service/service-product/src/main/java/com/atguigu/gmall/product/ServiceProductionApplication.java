package com.atguigu.gmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description:
 * @title: ServiceProductionApplication
 * @Author coderZGH
 * @Date: 2022/10/23 11:42
 * @Version 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan({"com.atguigu.gmall"})
public class ServiceProductionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProductionApplication.class, args);
    }
}