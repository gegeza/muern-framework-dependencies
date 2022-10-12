package com.muern.framework.core.encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.muern.framework.core.common.Constant.N16;

/**
 * @author gegeza
 * @date 2020-01-08
 */
public final class Aes {

    private static final Logger log = LoggerFactory.getLogger(Aes.class);
    private static final String KEY_ALGORITHM_AES = "AES";
    private static final String CIPHER_ALGORITHM_AES = "AES/ECB/PKCS5Padding";
    private static final String AES_CBC_PKCS7 = "AES/CBC/PKCS7Padding";

    /**
     * AES加密方法
     **/
    public static byte[] encode(String content, String encryptKey) {
        int len = encryptKey.length();
        if (len > N16) {
            encryptKey = encryptKey.substring(0, N16);
        } else if (len < N16) {
            encryptKey = encryptKey + getNumOfZero(N16 - len);
        }
        try {
            vailKeyLength(encryptKey);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_AES);
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey.getBytes(), KEY_ALGORITHM_AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * 加密后转换成Base64字符串
     **/
    public static String encodeToBase64(String content, String encryptKey) {
        int len = encryptKey.length();
        if (len > N16) {
            encryptKey = encryptKey.substring(0, N16);
        } else if (len < N16) {
            encryptKey = encryptKey + getNumOfZero(N16 - len);
        }
        byte[] bytes = encode(content, encryptKey);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * AES解密方法
     **/
    public static String decode(byte[] encryptBytes, byte[] decryptKey, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS7);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decryptKey, KEY_ALGORITHM_AES);
            if (iv != null) {
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            }
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * AES解密方法
     **/
    public static String decode(byte[] encryptBytes, String decryptKey) {
        int len = decryptKey.length();
        if (len > N16) {
            decryptKey = decryptKey.substring(0, N16);
        } else if (len < N16) {
            decryptKey = decryptKey + getNumOfZero(N16 - len);
        }
        try {
            vailKeyLength(decryptKey);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_AES);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decryptKey.getBytes(), KEY_ALGORITHM_AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * AES解密方法
     **/
    public static String decode(String encryptString, String decryptKey) {
        return decode(Base64.getDecoder().decode(encryptString), decryptKey);
    }

    /**
     * AES解密方法
     **/
    public static String decode(String encryptString, String decryptKey, String iv) {
        byte[] encryptBytes = Base64.getDecoder().decode(encryptString);
        byte[] keyBytes = Base64.getDecoder().decode(decryptKey);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        return decode(encryptBytes, keyBytes, ivBytes);
    }

    /**
     * 校验AES的密钥必须为16位长度，否则抛出RuntimeException
     **/
    private static void vailKeyLength(String key) {
        if (key.length() != N16) {
            throw new RuntimeException("key length must be 16");
        }
    }

    /**
     * 补零
     */
    private static String getNumOfZero(int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append("0");
        }
        return sb.toString();
    }
}
