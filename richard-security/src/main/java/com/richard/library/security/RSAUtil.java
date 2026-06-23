package com.richard.library.security;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * @author Mr.Zheng
 * @date 2014年8月22日 下午1:44:23
 */
public final class RSAUtil {

    /*android客户端用这个*/
    public static final String KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * 用公钥进行Rsa加密
     *
     * @param publicKey 公钥
     * @param content   要加密的字符串
     */
    public static String encrypt(String publicKey, String content) {
        String outStr = "";
        try {
            // base64编码的公钥
            byte[] decoded = Base64.decode(publicKey, Base64.NO_WRAP);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));

            // RSA加密
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            outStr = Base64.encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param privateKey 私钥
     * @param content    加密字符串
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String privateKey, String content) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decode(content.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);

        //base64编码的私钥
        byte[] decoded = Base64.decode(privateKey.getBytes(), Base64.NO_WRAP);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));

        //RSA解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }
}