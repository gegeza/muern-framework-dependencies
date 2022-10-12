package com.muern.framework.core.encrypt;

import com.muern.framework.core.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author gegeza
 * @date 2020-09-07
 */
public final class Rsa {

    private static final Logger log = LoggerFactory.getLogger(Rsa.class);
    private static final String MD5_WITH_RSA    = "MD5WithRSA";
    private static final String SHA1_WITH_RSA    = "SHA1WithRSA";
    private static final String SHA256_WITH_RSA      = "SHA256withRSA";
    private static final String RSA = "RSA";
    private static final String RSA_ECB_PKCS1 = "RSA/ECB/PKCS1Padding";

    public static String signWithMd5(String data, PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(signWithSha1(data.getBytes(StandardCharsets.UTF_8), privateKey));
    }

    public static byte[] signWithMd5(byte[] data, PrivateKey privateKey) {
        return sign(data, privateKey, MD5_WITH_RSA);
    }

    public static byte[] signWithSha1(String data, PrivateKey privateKey) {
        return signWithSha1(data.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    public static byte[] signWithSha1(byte[] data, PrivateKey privateKey) {
        return sign(data, privateKey, SHA1_WITH_RSA);
    }

    public static String signWithSha1ToBase64(byte[] data, PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(signWithSha1(data, privateKey));
    }

    public static String signWithSha1ToBase64(String data, PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(signWithSha1(data, privateKey));
    }

    public static byte[] signWithSha256(String data, PrivateKey privateKey) {
        return signWithSha256(data.getBytes(StandardCharsets.UTF_8), privateKey);
    }

    public static byte[] signWithSha256(byte[] data, PrivateKey privateKey) {
        return sign(data, privateKey, SHA256_WITH_RSA);
    }

    public static String signWithSha256ToBase64(String data, PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(signWithSha256(data, privateKey));
    }

    public static byte[] sign(byte[] data, PrivateKey privateKey, String algorithm) {
        try {
            Signature st = Signature.getInstance(algorithm);
            st.initSign(privateKey);
            st.update(data);
            return st.sign();
        } catch (NoSuchAlgorithmException e) {
            log.error("Rsa sign algorithm[" + algorithm + "] error!", e);
        } catch (InvalidKeyException e) {
            log.error("Rsa sign key invalid!", e);
        } catch (SignatureException e) {
            log.error("Rsa sign error!", e);
        }
        return null;
    }

    public static boolean designWithSha1(String data, String signature,PublicKey publicKey) {
        return designWithSha1(data.getBytes(StandardCharsets.UTF_8), Base64.getDecoder().decode(signature), publicKey);
    }

    public static boolean designWithSha1(byte[] data, byte[] signature,PublicKey publicKey) {
        return design(data, signature, publicKey, SHA1_WITH_RSA);
    }

    public static boolean designWithSha256(String data, String signature,PublicKey publicKey) {
        return designWithSha256(data.getBytes(StandardCharsets.UTF_8), Base64.getDecoder().decode(signature), publicKey);
    }

    public static boolean designWithSha256(byte[] data, byte[] signature,PublicKey publicKey) {
        return design(data, signature, publicKey, SHA256_WITH_RSA);
    }

    public static boolean design(byte[] data, byte[] signature, PublicKey publicKey, String algorithm) {
        try {
            Signature sign = Signature.getInstance(algorithm);
            sign.initVerify(publicKey);
            sign.update(data);
            return sign.verify(signature);
        } catch (NoSuchAlgorithmException e) {
            log.error("Rsa design algorithm[" + algorithm + "] error!", e);
        } catch (SignatureException e) {
            log.error("Rsa design error!", e);
        } catch (InvalidKeyException e) {
            log.error("Rsa design key invalid!", e);
        }
        return false;
    }

    public static String encrypt(String data, PublicKey publicKey, int keyLength) {
        return Base64.getEncoder().encodeToString(encrypt(data.getBytes(StandardCharsets.UTF_8), publicKey, RSA, keyLength));
    }

    public static String encryptWithPkcs1(String data, PublicKey publicKey, int keyLength) {
        return Base64.getEncoder().encodeToString(encrypt(data.getBytes(StandardCharsets.UTF_8), publicKey, RSA_ECB_PKCS1, keyLength));
    }

    public static byte[] encryptWithPkcs1(byte[] data, PublicKey publicKey, int keyLength) {
        return encrypt(data, publicKey, RSA_ECB_PKCS1, keyLength);
    }

    public static byte[] encrypt(byte[] data, PublicKey publicKey, String transformation, int keyLength) {
        try {
            byte[] encrypt = null;
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int length = keyLength / 8;
            for (int i = 0; i < data.length; i += length - 11) {
                byte[] temp = cipher.doFinal(Arrays.copyOfRange(data, i, i + length - 11));
                encrypt = ByteUtils.byteAdd(encrypt, temp);
            }
            return encrypt;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String decrypt(String data, PrivateKey privateKey, int length) {
        return ByteUtils.byteToStr(decrypt(Base64.getDecoder().decode(data), privateKey, RSA, length));
    }

    public static String decryptWithPkcs1(String data, PrivateKey privateKey, int length) {
        return ByteUtils.byteToStr(decrypt(Base64.getDecoder().decode(data), privateKey, RSA_ECB_PKCS1, length));
    }

    public static byte[] decryptWithPkcs1(byte[] data, PrivateKey privateKey, int length) {
        return decrypt(data, privateKey, RSA_ECB_PKCS1, length);
    }

    public static byte[] decrypt(byte[] data, PrivateKey privateKey, String transformation, int keyLength) {
        try {
            byte[] decrypt = null;
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int length = keyLength / 8;
            for (int i = 0; i < data.length; i += length) {
                byte[] temp = cipher.doFinal(Arrays.copyOfRange(data, i, i + length));
                decrypt = ByteUtils.byteAdd(decrypt, temp);
            }
            return decrypt;
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
