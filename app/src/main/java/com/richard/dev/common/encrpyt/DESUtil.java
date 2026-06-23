package com.richard.dev.common.encrpyt;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * <pre>
 * <b>DES加、解密辅助工具.</b>
 * <b>Description:</b> DES算法的入口参数有三个:Key、Data、Mode.
 *    其中Key为8个字节共64位,是DES算法的工作密钥;Data也为8个字节64位,
 *    是要被加密或被解密的数据;Mode为DES的工作方式,有两种:加密或解密.
 *    DES算法把64位的明文输入块变为64位的密文输出块,它所使用的密钥也是64位.
 *    支持 DES、DESede(TripleDES,就是3DES)、AES、Blowfish、RC2、RC4(ARCFOUR)
 *    DES                  key size must be equal to 56
 *    DESede(TripleDES)    key size must be equal to 112 or 168
 *    AES                  key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
 *    Blowfish             key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
 *    RC2                  key size must be between 40 and 1024 bits
 *    RC4(ARCFOUR)         key size must be between 40 and 1024 bits
 *    具体内容 需要关注 JDK Document http://.../docs/technotes/guides/security/SunProviders.html
 *
 * <b>Author:</b> zhouguangyong@huanbotech.com
 * <b>Date:</b> 2014-01-01 上午10:00:01
 * <b>Copyright:</b> Copyright ©2006-2015 huanbotech.com Technology Co., Ltd. All rights reserved.
 * <b>Changelog:</b>
 *   Ver   Date                  Author              Detail
 *   ----------------------------------------------------------------------
 *   1.0   2014-01-01 10:00:01   zhouguangyong@huanbotech.com
 *         new file.
 * </pre>
 */
public abstract class DESUtil {
    /**
     * ALGORITHM 算法 <br>
     * 可替换为以下任意一种算法，同时key值的size相应改变。
     * <p>
     * <pre>
     * DES          		key size must be equal to 56
     * DESede(TripleDES) 	key size must be equal to 112 or 168
     * AES          		key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
     * Blowfish     		key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
     * RC2          		key size must be between 40 and 1024 bits
     * RC4(ARCFOUR) 		key size must be between 40 and 1024 bits
     * </pre>
     * <p>
     * 在Key toKey(byte[] key)方法中使用下述代码
     * <code>SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);</code> 替换
     * <code>
     * DESKeySpec dks = new DESKeySpec(key);
     * SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
     * SecretKey secretKey = keyFactory.generateSecret(dks);
     * </code>
     */
    public static final String ALGORITHM = "DES";

    /**
     * 生成密钥
     *
     * @return String
     */
    public static String initKey() {
        return initKey(null);
    }

    /**
     * 生成密钥
     *
     * @param seed
     * @return String
     */
    public static String initKey(String seed) {
        SecureRandom secureRandom = null;
        if (seed != null) {
            secureRandom = new SecureRandom(Base64Util.decrypt2Byte(seed));
        } else {
            secureRandom = new SecureRandom();
        }
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(ALGORITHM);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        kg.init(secureRandom);
        SecretKey secretKey = kg.generateKey();
        return Base64Util.encrypt(secretKey.getEncoded());
    }

    /**
     * 转换密钥<br>
     *
     * @param key
     * @return
     */
    protected static Key toKey(byte[] key) {
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey secretKey = keyFactory.generateSecret(dks);
            // 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
            // SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
            return secretKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt2byte(byte[] data, String key) {
        try {
            Key k = toKey(Base64Util.decrypt2Byte(key));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, k);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param data
     * @param key
     * @return
     */
    public static String decrypt(byte[] data, String key) {
        try {
            Key k = toKey(Base64Util.decrypt2Byte(key));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, k);
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(String data, String key) {
        try {
            byte[] byt = data.getBytes();
            Key k = toKey(Base64Util.decrypt2Byte(key));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            return cipher.doFinal(byt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, String key) {
        try {
            Key k = toKey(Base64Util.decrypt2Byte(key));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public static void main(String[] args) throws Exception {
        String input = "测试加密明文";
        String key = Base64Util.encrypt(MD5Util.encrypt(UUID.randomUUID().toString()));
        System.out.println("加密前：" + input);
        System.out.println("加密key：" + key);
        // 加密
		input = Base64Util.encrypt(DESUtil.encrypt(Base64Util.encrypt(input), key));
		System.out.println("加密后：" + input);

        // 解密
        input = DESUtil.decrypt(Base64Util.decrypt2Byte(input), key);
        System.out.println("解密后：" + Base64Util.decrypt(input));
    }*/
}
