package com.muern.framework.boot.storage;

import com.github.f4b6a3.ulid.UlidCreator;
import com.muern.framework.core.common.Constant;
import com.muern.framework.core.utils.DateUtils;
import org.springframework.util.StringUtils;

import java.io.File;
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
     * @param fileKey 文件标识
     * @return boolean
     */
    boolean existsObject(String fileKey);

    /**
     * 获取oss文件元数据
     * @param fileKey
     * @return
     */
    Map<String, Object> getMetaData(String fileKey);

    /**
     * 获取文件上传地址
     * @param directory 上传目录
     * @param fileName 文件名称
     * @return java.lang.String
     */
    String getUploadUrl(String directory, String fileName);

    /**
     * 获取文件下载地址
     * @param fileKey 文件标识
     * @param fileName 文件名称
     * @return java.lang.String
     */
    String getDownloadUrl(String fileKey, String fileName);

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

    default String getFileKey(String directory, String fileName) {
        String fileKey = UlidCreator.getUlid().toString();
        //获取文件目录
        if (StringUtils.hasText(directory)) {
            fileKey = directory.concat(File.separator).concat(fileKey);
        }
        //获取文件类型
        if (fileName.contains(Constant.DOT)) {
            fileKey = fileKey.concat(fileName.substring(fileName.lastIndexOf(Constant.DOT)));
        }
        return fileKey;
    }

    default Date getExpiration() {
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(10 * 60L);
        return DateUtils.localDateTime2Date(expireTime);
    }
}
