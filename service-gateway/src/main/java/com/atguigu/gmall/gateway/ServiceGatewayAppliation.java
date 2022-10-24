package com.atguigu.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @description:
 * @title: ServiceGatewayAppliation
 * @Author coderZGH
 * @Date: 2022/10/24 5:20
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceGatewayAppliation {

    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayAppliation.class, args);
    }
}