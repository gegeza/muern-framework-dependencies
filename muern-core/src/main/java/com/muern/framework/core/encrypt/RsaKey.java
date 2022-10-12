package com.muern.framework.core.encrypt;

import com.muern.framework.core.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Enumeration;

/**
 * @author gegeza
 * @date 2020-09-07
 */
public final class RsaKey {

    private static final Logger log = LoggerFactory.getLogger(RsaKey.class);
    private static final String KEY_ALGORITHM_RSA = "RSA";

    /**
     * 根据私钥文件的路径创建公钥实例
     * @param path 私钥文件的路径
     * @return 私钥实例
     */
    public static PrivateKey getPriKeyFromPath(String path) {
        return getPriKeyFromByte(readKeyFromPath(path));
    }

    /**
     * 根据私钥Base64字符串创建公钥实例
     * @param content 私钥的内容(Base64加密)
     * @return 私钥实例
     */
    public static PrivateKey getPriKeyFromContent(String content) {
        return getPriKeyFromByte(Base64.getDecoder().decode(content));
    }

    /**
     * 根据私钥byte[]创建公钥实例
     * @param bytes 私钥内容的byte数组
     * @return 私钥实例
     */
    public static PrivateKey getPriKeyFromByte(byte[] bytes) {
        try {
            KeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /***
     * 从pfx证书中读取私钥
     * @param path pfx证书的路径
     * @return 私钥实例
     */
    public static PrivateKey getPriKeyFromPfx(String path, String strPassword) {
        try (
            InputStream inputStream = RsaKey.class.getResourceAsStream(path)
        ) {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] nPassword = StringUtils.hasText(strPassword) ? strPassword.toCharArray() : null;
            ks.load(inputStream, nPassword);
            Enumeration<String> enumas = ks.aliases();
            String keyAlias = null;
            if (enumas.hasMoreElements()) {
                keyAlias = enumas.nextElement();
            }
            return (PrivateKey) ks.getKey(keyAlias, nPassword);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据公钥文件的路径创建公钥实例
     * @param path 公钥文件的路径
     * @return 公钥实例
     */
    public static PublicKey getPubKeyFromPath(String path) {
        return getPubKeyFromByte(readKeyFromPath(path));
    }

    /**
     * 根据公钥Base64字符串创建公钥实例
     * @param content 公钥的内容(Base64加密)
     * @return 公钥实例
     */
    public static PublicKey getPubKeyFromContent(String content) {
        return getPubKeyFromByte(Base64.getDecoder().decode(content));
    }

    /**
     * 根据公钥byte[]创建公钥实例
     * @param bytes 公钥内容的byte数组
     * @return 公钥实例
     */
    public static PublicKey getPubKeyFromByte(byte[] bytes) {
        try {
            KeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /***
     * 从cer证书中读取公钥
     * @param path cer证书的路径
     * @return 公钥实例
     */
    public static PublicKey getPubKeyFromCerPath(String path) {
        try (
            InputStream inputStream = RsaKey.class.getResourceAsStream(path)
        ) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate)cf.generateCertificate(inputStream);
            return certificate.getPublicKey();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /** 从path中读取密钥的回byte数组 */
    private static byte[] readKeyFromPath(String path) {
        try (InputStream inputStream = RsaKey.class.getResourceAsStream(path);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            byte[] b = new byte[1024];
            int length;
            while (-1 != (length = inputStream.read(b))) {
                baos.write(b, 0, length);
            }
            String key = ByteUtils.byteToStr(baos.toByteArray());
            if (key.contains("-----BEGIN PRIVATE KEY-----")) {
                return Base64.getDecoder().decode(key.replaceAll("-----\\w+ PRIVATE KEY-----", ""));
            } else if (key.contains("-----BEGIN PUBLIC KEY-----")) {
                return Base64.getDecoder().decode(key.replaceAll("-----\\w+ PUBLIC KEY-----", ""));
            } else if (key.contains("-----BEGIN RSA PRIVATE KEY-----")) {
                final byte[] innerKey = Base64.getDecoder().decode(key.replaceAll("-----\\w+ RSA PRIVATE KEY-----", ""));
                byte[] bytes = new byte[innerKey.length + 26];
                System.arraycopy(Base64.getDecoder().decode("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKY="), 0, bytes, 0, 26);
                System.arraycopy(BigInteger.valueOf(bytes.length - 4L).toByteArray(), 0, bytes, 2, 2);
                System.arraycopy(BigInteger.valueOf(innerKey.length).toByteArray(), 0, bytes, 24, 2);
                System.arraycopy(innerKey, 0, bytes, 26, innerKey.length);
                return bytes;
            } else {
                log.error("ERROR PEM FILES");
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
