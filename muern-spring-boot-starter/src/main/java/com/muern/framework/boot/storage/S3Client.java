package com.muern.framework.boot.storage;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.muern.framework.core.common.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gegeza
 * @date 2022/06/29
 */
@Component
@ConditionalOnProperty(prefix = "muern.storage.s3", name = "enable", havingValue = "true")
public class S3Client implements StorageClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(S3Client.class);

    /** 云存储endpoint地址  例如:https://s3.muern.com:9021 */
    @Value("${muern.storage.s3.endpoint}") private String endpoint;
    /**
     * 代理地址 主要解决前端put时跨域问题  自己在后端nginx做好代理  例如https://yrmalltest.muern.com
     * 则https://yrmalltest.muern.com/bucketName 代理到 https://s3.muern.com:9021/bucketName
     */
    @Value("${muern.storage.s3.proxy-endpoint:null}") private String proxyEndpoint;
    /** 分配的accessKey */
    @Value("${muern.storage.s3.access-key}") private String accessKey;
    /** 分配的accessSecret */
    @Value("${muern.storage.s3.access-secret}") private String accessSecret;
    /** 分配的bucketName */
    @Value("${muern.storage.s3.bucket-name}") private String bucketName;

    private AmazonS3 s3Client;

    @PostConstruct
    void createS3Client() {
        s3Client = AmazonS3Client.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, Regions.US_EAST_1.getName()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, accessSecret)))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    @Override
    public boolean existsObject(String fileKey) {
        return s3Client.doesObjectExist(bucketName, fileKey);
    }

    @Override
    public Map<String, Object> getMetaData(String fileKey) {
        ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, fileKey);
        return Maps.of("size", metadata.getContentLength());
    }

    @Override
    public String getUploadUrl(String directory, String fileName) {
        //创建请求对象
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, getFileKey(directory, fileName), HttpMethod.PUT);
        //设置过期时间
        request.setExpiration(getExpiration());
        //设置ContentType
        request.setContentType("application/octet-stream");
        //生成PUT上传地址
        return s3Client.generatePresignedUrl(request).toString();
    }

    @Override
    public String getDownloadUrl(String fileKey, String fileName) {
        try {
            //创建请求对象
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileKey, HttpMethod.GET);
            //设置过期时间
            request.setExpiration(getExpiration());
            //设置下载文件名
            ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
            overrides.setContentDisposition(
                    "attachment;filename=\"".concat(URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())).concat("\""));
            request.setResponseHeaders(overrides);
            //生成GET下载地址
            return s3Client.generatePresignedUrl(request).toString();
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getMultiUploadUrl(String key, int parts) {
        //获取uploadId
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult result = s3Client.initiateMultipartUpload(request);
        String uploadId = result.getUploadId();
        //循环生成上传URL
        List<String> partUrlList = new ArrayList<>();
        for (int i = 1; i <= parts; i++) {
            GeneratePresignedUrlRequest generateRequest = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.PUT);
            generateRequest.withExpiration(getExpiration());
            generateRequest.addRequestParameter("uploadId", uploadId);
            generateRequest.addRequestParameter("partNumber", String.valueOf(i));
            URL url = s3Client.generatePresignedUrl(generateRequest);
            partUrlList.add(URLDecoder.decode(url.toString().replace(endpoint, proxyEndpoint)));
        }
        return Maps.of("uploadId", uploadId, "uploadUrls", partUrlList);
    }

    @Override
    public void mergeMultiUploadUrl(String key, String uploadId) {
        //查询uploadId下面的分片
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, key, uploadId);
        PartListing partListing = s3Client.listParts(listPartsRequest);
        List<PartSummary> partSummaries = partListing.getParts();
        //获取分片下所有的ETag
        List<PartETag> eTags = new ArrayList<>();
        for (PartSummary partSummary : partSummaries) {
            eTags.add(new PartETag(partSummary.getPartNumber(), partSummary.getETag()));
        }
        //合并分片
        CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(bucketName, key, uploadId, eTags);
        CompleteMultipartUploadResult completeResult = s3Client.completeMultipartUpload(completeRequest);
        if (completeResult == null) {
            throw new RuntimeException("CompleteMultipartUploadRequest error");
        }
    }
}
