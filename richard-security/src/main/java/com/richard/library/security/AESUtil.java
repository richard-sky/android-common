package com.richard.library.security;

import java.nio.charset.StandardCharsets;

/**
 * @version V1.0
 * @desc AES 加密工具类
 */
public class AESUtil {

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";//默认的加密算法

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param aesKey  加密key
     * @param aesIv   16位向量
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String aesKey, String aesIv) {
        byte[] decryptByte = EncryptUtil.encryptAES2Base64(
                content.getBytes()
                , aesKey.getBytes()
                , DEFAULT_CIPHER_ALGORITHM
                , aesIv.getBytes()
        );
        return new String(decryptByte, StandardCharsets.UTF_8);
    }

    /**
     * AES 解密操作
     *
     * @param content 解密内容
     * @param aesKey  解密key
     * @param aesIv   16位向量
     */
    public static String decrypt(String content, String aesKey, String aesIv) {
        byte[] decryptByte = EncryptUtil.decryptBase64AES(
                content.getBytes()
                , aesKey.getBytes()
                , DEFAULT_CIPHER_ALGORITHM
                , aesIv.getBytes()
        );
        return new String(decryptByte, StandardCharsets.UTF_8);
    }
}