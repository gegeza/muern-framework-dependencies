package com.muern.framework.boot.storage;

import com.muern.framework.core.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 定义对象存储操作接口
 * @author gegeza
 * @date 2022/06/29
 */
public interface StorageClient {

    /**
     * 判断文件是否存在
     * @param key 文件名称
     * @return boolean
     */
    boolean existsObject(String key);

    /**
     * 获取文件上传地址
     * @param key 文件名称
     * @return java.lang.String
     */
    String getUploadUrl(String key);

    /**
     * 获取文件下载地址
     * @param key 文件名称
     * @return java.lang.String
     */
    String getDownloadUrl(String key);

    /**
     * 获取分片上传URL
     * @param key 文件名称
     * @param parts 分片数量
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> getMultiUploadUrl(String key, int parts);

    /**
     * 合并分片上传数据
     * @param key 文件名称
     * @param uploadId 分片上传ID
     */
    void mergeMultiUploadUrl(String key, String uploadId);

    default Date getExpiration() {
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(10 * 60L);
        return DateUtils.localDateTime2Date(expireTime);
    }
}
