package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.config.MinioProperties;
import io.minio.*;
import io.minio.errors.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @description:
 * @title: FileUploadController
 * @Author coderZGH
 * @Date: 2022/10/24 15:36
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    @Autowired
    private MinioProperties minioProperties;

    @ApiOperation("实现文件上传")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        String url = null;
        try {
            // 创建客户端
            /* play.min.io for test and development. */
            String endpointUrl = minioProperties.getEndpointUrl();
            String accessKey = minioProperties.getAccessKey();
            String secreKey = minioProperties.getSecreKey();
            String bucketName = minioProperties.getBucketName();

            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(endpointUrl)
                            .credentials(accessKey, secreKey)
                            .build();

            // 判断bukect是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            if (!found) {
                // 创建bucket
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            String fileName = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();

            String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
            fileName = fileName + extName;

            InputStream inputStream = file.getInputStream();
            
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                            inputStream, inputStream.available(), -1)
                            .build());

             url = endpointUrl + "/" + bucketName + "/" + fileName;

            System.out.println("url:\t"+url);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.ok(url);
    }

}