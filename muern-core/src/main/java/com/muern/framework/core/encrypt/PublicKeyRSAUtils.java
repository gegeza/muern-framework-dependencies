package com.muern.framework.core.encrypt;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author 112932
 * @date 2020/3/25
 */
public class PublicKeyRSAUtils {

    private static final String ALGORITHM = "RSA";

    /**
     * 解密算法
     */
    private static final String CIPHER_EN = "RSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;


    /**
     * 公钥加密
     *
     * @param data      原文 转为byte[] 数组
     * @param publicKey 公钥
     * @return byte[]
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        // 得到公钥
        byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        Key key = keyFactory.generatePublic(x509EncodedKeySpec);
        // 加密数据，分段加密
        Cipher cipher = Cipher.getInstance(CIPHER_EN);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        int inputLength = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        while (inputLength - offset > 0) {
            if (inputLength - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offset, inputLength - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 公钥加密
     *
     * @param sourceStr sourceStr
     * @param publicKey publicKey
     * @return String
     * @throws Exception e
     */
    public static String getStringEncryptByPublicKey(String sourceStr, String publicKey) throws Exception {
        String deEncryptString = sourceStr.trim();
        byte[] encryptStrByte = PublicKeyRSAUtils.encryptByPublicKey(deEncryptString.getBytes(), publicKey);
        byte[] btt = Base64.getEncoder().encode(encryptStrByte);
        return new String(btt);
    }

}