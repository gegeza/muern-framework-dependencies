package com.muern.framework.boot.storage;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author gegeza
 * @date 2022/06/29
 */
@Component
@ConditionalOnProperty(prefix = "muern.storage.oss", name = "enable", havingValue = "true")
public class OssClient implements StorageClient{

    /** oss的EndPoint  例如:oss-cn-hangzhou.aliyuncs.com */
    @Value("${muern.storage.oss.endpoint}") private String endpoint;
    /** 分配的accessKey 例如： */
    @Value("${muern.storage.oss.access-key}") private String accessKey;
    /** 分配的accessSecret 例如： */
    @Value("${muern.storage.oss.access-secret}") private String accessSecret;
    /** 分配的bucketName */
    @Value("${muern.storage.oss.bucket-name}") private String bucketName;

    private OSS oss;

    @PostConstruct
    void createOssClient() {
        // 创建ClientConfiguration。 具体配置项见https://help.aliyun.com/document_detail/32010.html
        ClientBuilderConfiguration conf= new ClientBuilderConfiguration();
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
    public boolean existsObject(String key) {
        return oss.doesObjectExist(bucketName, key);
    }

    @Override
    public String getUploadUrl(String key) {
        URL url = oss.generatePresignedUrl(bucketName, key, getExpiration(), HttpMethod.PUT);
        return URLDecoder.decode(url.toString());
    }

    @Override
    public String getDownloadUrl(String key) {
        URL url = oss.generatePresignedUrl(bucketName, key, getExpiration(), HttpMethod.GET);
        return URLDecoder.decode(url.toString());
    }

    @Override
    public Map<String, Object> getMultiUploadUrl(String key, int parts) {
        return null;
    }

    @Override
    public void mergeMultiUploadUrl(String key, String uploadId) {

    }
}
