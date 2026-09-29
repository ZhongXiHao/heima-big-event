package com.cshlands;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.OSSClientBuilder;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.*;
import com.aliyun.sdk.service.oss2.paginator.ListBucketsIterable;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

public class PutObjectTest {

    @Test
    public void testPutObject() {
        execute(
                "oss-cn-hangzhou.aliyuncs.com",
                "cn-hangzhou",
                "big-event-cshlands",
                "test.jpg");
    }

    @Test
    public void testShowExample() {
        showExample();
    }

    private static void execute(
            String endpoint,
            String region,
            String bucket,
            String key) {

        CredentialsProvider provider = new EnvironmentVariableCredentialsProvider();
        OSSClientBuilder clientBuilder = OSSClient.newBuilder()
                .credentialsProvider(provider)
                .region(region);

        if (endpoint != null) {
            clientBuilder.endpoint(endpoint);
        }

        try (OSSClient client = clientBuilder.build()) {

            String data = "hello world";
            File file = new File("C:\\Users\\Xixi\\Desktop\\files\\mtFUJIjpg.jpg");

            PutObjectResult result = client.putObject(PutObjectRequest.newBuilder()
                    .bucket(bucket)
                    .key(key)
                    .body(BinaryData.fromStream(new FileInputStream(file), file.length()))
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
        }
    }

//    public Options getOptions() {
//        Options opts = new Options();
//        opts.addOption(Option.builder().longOpt("endpoint").desc("The domain names that other services can use to access OSS.").hasArg().get());
//        opts.addOption(Option.builder().longOpt("region").desc("The region in which the bucket is located.").hasArg().required().get());
//        opts.addOption(Option.builder().longOpt("bucket").desc("The name of the bucket.").hasArg().required().get());
//        opts.addOption(Option.builder().longOpt("key").desc("The name of the object.").hasArg().required().get());
//        return opts;
//    }
//
//
//    public void runCmd(CommandLine cmd) throws ParseException {
//        String endpoint = cmd.getParsedOptionValue("endpoint");
//        String region = cmd.getParsedOptionValue("region");
//        String bucket = cmd.getParsedOptionValue("bucket");
//        String key = cmd.getParsedOptionValue("key");
//        execute(endpoint, region, bucket, key);
//    }

    public void showExample() {
        String region = "cn-hangzhou";

        CredentialsProvider provider = new EnvironmentVariableCredentialsProvider();
        OSSClientBuilder clientBuilder = OSSClient.newBuilder()
                .credentialsProvider(provider)
                .region(region);

        try (OSSClient client = clientBuilder.build()) {

            ListBucketsIterable paginator = client.listBucketsPaginator(
                    ListBucketsRequest.newBuilder()
                            .build());

            for (ListBucketsResult result : paginator) {
                for (BucketSummary info : result.buckets()) {
                    System.out.printf("bucket: name:%s, region:%s, storageClass:%s\n", info.name(), info.region(), info.storageClass());
                }
            }

        } catch (Exception e) {
//            ServiceException se = ServiceException.asCause(e);
//            if (se != null) {
//                System.out.printf("ServiceException: requestId:%s, errorCode:%s\n", se.requestId(), se.errorCode());
//            }
            System.out.printf("error:\n%s", e);
        }
    }
}
