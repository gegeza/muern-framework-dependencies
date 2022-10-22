package com.muern.framework.boot.storage;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.ResponseHeaderOverrides;
import com.github.f4b6a3.ulid.UlidCreator;
import com.muern.framework.boot.exception.BizException;
import com.muern.framework.core.common.Constant;
import com.muern.framework.core.common.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author gegeza
 * @date 2022/06/29
 */
@Component
@ConditionalOnProperty(prefix = "muern.storage.oss", name = "enable", havingValue = "true")
public class OssClient implements StorageClient{

    public static final Logger LOGGER = LoggerFactory.getLogger(OssClient.class);

    /** oss的EndPoint  例如:oss-cn-hangzhou.aliyuncs.com */
    @Value("${muern.storage.oss.endpoint}") private String endpoint;
    /** 分配的accessKey */
    @Value("${muern.storage.oss.access-key}") private String accessKey;
    /** 分配的accessSecret */
    @Value("${muern.storage.oss.access-secret}") private String accessSecret;
    /** 分配的bucketName */
    @Value("${muern.storage.oss.bucket-name}") private String bucketName;

    private OSS oss;

    @PostConstruct
    void createOssClient() {
        // 创建ClientConfiguration。 具体配置项见https://help.aliyun.com/document_detail/32010.html
        ClientBuilderConfiguration conf= new ClientBuilderConfiguration();
        //设置传输协议为HTTPS
        conf.setProtocol(Protocol.HTTPS);
        // 设置Socket层传输数据的超时时间，默认为50000毫秒。
        conf.setSocketTimeout(10000);
        // 设置建立连接的超时时间，默认为50000毫秒。
        conf.setConnectionTimeout(10000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        conf.setConnectionRequestTimeout(1000);
        // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
        conf.setIdleConnectionTime(10000);
        // 设置失败请求重试次数，默认为3次。
        conf.setMaxErrorRetry(5);
        oss = new OSSClientBuilder().build(endpoint, accessKey, accessSecret, conf);
    }

    @Override
    public boolean existsObject(String fileKey) {
        return oss.doesObjectExist(bucketName, fileKey);
    }

    @Override
    public Map<String, Object> getMetaData(String fileKey) {
        ObjectMetadata metadata = oss.getObjectMetadata(bucketName, fileKey);
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
        return oss.generatePresignedUrl(request).toString();
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
            return oss.generatePresignedUrl(request).toString();
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getMultiUploadUrl(String fileKey, int parts) {
        throw new BizException("暂不支持");
    }

    @Override
    public void mergeMultiUploadUrl(String fileKey, String uploadId) {
        throw new BizException("暂不支持");
    }
}
