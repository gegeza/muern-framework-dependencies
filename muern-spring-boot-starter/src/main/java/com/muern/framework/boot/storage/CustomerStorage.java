package com.muern.framework.boot.storage;

import com.muern.framework.core.common.CodeImpl;
import com.muern.framework.core.common.Constant;
import com.muern.framework.core.common.R;
import com.muern.framework.core.encrypt.Hash;
import com.muern.framework.core.utils.CodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @author gegeza
 * @date 2020-09-06 4:21 PM
 */
@RestController
@RequestMapping("storage")
@ConditionalOnProperty(prefix = "muern.storage.custom", name = "enable", havingValue = "true")
public class CustomerStorage {

    @Value("${muern.storage.custom.dir}") private String fileDir;
    @Value("${muern.storage.custom.uri}") private String fileUri;

    private static final Logger log = LoggerFactory.getLogger(CustomerStorage.class);
    private static final String SIGN_KEY = "muernupload";


    @PostMapping(value = "upload")
    public R<String> upload(@RequestBody UploadDto dto) {
        //验证签名
        if (!dto.getSign().equalsIgnoreCase(Hash.sha256(dto.getTimestamp() + SIGN_KEY))){
            return R.ins(CodeImpl.ERR_SIGN);
        }
        //定义文件名称
        String randomName = CodeUtils.getUlid(), filename = dto.getFilename();
        if (filename.contains(Constant.DOT)) {
            randomName = randomName.concat(filename.substring(filename.lastIndexOf(Constant.DOT)));
        }
        //创建本地文件
        File localFile = new File(fileDir.concat(randomName));
        try {
            if (!localFile.exists() && !localFile.createNewFile()) {
                log.error("createNewFile Error:" + localFile.getPath());
                return R.fail();
            }
        } catch (Exception e) {
            log.error("createNewFile Error:" + localFile.getPath());
            log.error(e.getMessage(), e);
            return R.fail();
        }
        //写入文件并返回
        try (
            FileOutputStream fos = new FileOutputStream(localFile, false);
            BufferedOutputStream bos = new BufferedOutputStream(fos)
        ) {
            //获取文件对象
            byte[] bytes = Base64.getDecoder().decode(dto.getFile());
            bos.write(bytes, 0, bytes.length);
            bos.flush();
            bos.close();
            fos.close();
            return R.ok(fileUri.concat(randomName));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    public static class UploadDto {
        private String file;
        private String filename;
        private Long timestamp;
        private String sign;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}
