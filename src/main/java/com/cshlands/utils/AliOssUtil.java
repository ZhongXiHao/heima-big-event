package com.cshlands.utils;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.OSSClientBuilder;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectResult;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.cshlands.exception.BusinessException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AliOssUtil {

    public static String uploadUserPictures(String fileOssName, InputStream inputStream) {
        return execute(fileOssName, inputStream);
    }

    private static String execute(
            String fileName, InputStream inputStream) {

        final String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
        final String REGION = "cn-hangzhou";
        final String BUCKET = "big-event-cshlands";

        CredentialsProvider provider = new EnvironmentVariableCredentialsProvider();
        OSSClientBuilder clientBuilder = OSSClient.newBuilder()
                .credentialsProvider(provider)
                .region(REGION);

//        if (ENDPOINT != null) {
//            clientBuilder.endpoint(ENDPOINT);
//        }

        String url = "https://" + BUCKET + "." + ENDPOINT + "/" + fileName;

        try (OSSClient client = clientBuilder.build()) {

            String data = "hello world";
//            File file = new File("C:\\Users\\Xixi\\Desktop\\files\\mtFUJIjpg.jpg");

            PutObjectResult result = client.putObject(PutObjectRequest.newBuilder()
                    .bucket(BUCKET)
                    .key(fileName)
                    .body(BinaryData.fromStream(inputStream))
                    .build());

            System.out.printf("status code:%d, request id:%s, eTag:%s\n",
                    result.statusCode(), result.requestId(), result.eTag());

        } catch (Exception e) {
            //If the exception is caused by ServiceException, detailed information can be obtained in this way.
            // ServiceException se = ServiceException.asCause(e);
            // if (se != null) {
            //    System.out.printf("ServiceException: requestId:%s, errorCode:%s\n", se.requestId(), se.errorCode());
            //}
            System.out.printf("error:\n%s", e);
            throw BusinessException.internalServerError("Failed to upload file to Aliyun OSS");
        }

        return url;
    }
}
