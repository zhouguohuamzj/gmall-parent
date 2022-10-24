package com.atguigu.gmall.product.config;

import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @title: MinioProperties
 * @Author coderZGH
 * @Date: 2022/10/24 15:32
 * @Version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String endpointUrl;
    private String accessKey;
    private String secreKey;
    private String bucketName;



}