package com.richard.library.context.util;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * ID生成工具类
 */
public class IdGenerator {
    // 基础字符集
    private static final String NUMBER = "0123456789";
    private static final String LOWER_LETTER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // 安全字符集（移除易混淆字符：0 O 1 I l）
    private static final String NUMBER_SAFE = "23456789";
    private static final String LOWER_SAFE = "abcdefghjkmnpqrstuvwxyz";
    private static final String UPPER_SAFE = "ABCDEFGHJKMNPQRSTUVWXYZ";

    // 安全随机实例
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 字符组合类型枚举
     */
    public enum CharType {
        ONLY_NUM,        // 仅数字
        ONLY_LOWER,      // 仅小写字母
        ONLY_UPPER,      // 仅大写字母
        NUM_LOWER,       // 数字+小写
        NUM_UPPER,       // 数字+大写
        ALL_MIX          // 数字+大小写混合
    }

    /**
     * ID生成配置参数实体，Builder链式赋值
     */
    public static class IdConfig {
        /// 随机串长度
        private int length = 8;
        /// 字符类型
        private CharType charType = CharType.ALL_MIX;
        /// 自定义前缀
        private String prefix = "";
        /// 自定义后缀
        private String suffix = "";
        /// 是否使用安全字符（去除易混淆字符）
        private boolean useSafeChar = true;
        /// 是否拼接毫秒时间戳前缀
        private boolean appendTimestamp = false;

        public IdConfig() {}

        // 链式设置方法
        public IdConfig length(int length) {
            this.length = length;
            return this;
        }

        public IdConfig charType(CharType charType) {
            this.charType = charType;
            return this;
        }

        public IdConfig prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public IdConfig suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public IdConfig useSafeChar(boolean useSafeChar) {
            this.useSafeChar = useSafeChar;
            return this;
        }

        public IdConfig appendTimestamp(boolean appendTimestamp) {
            this.appendTimestamp = appendTimestamp;
            return this;
        }

        // Getter
        public int getLength() {
            return length;
        }

        public CharType getCharType() {
            return charType;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public boolean isUseSafeChar() {
            return useSafeChar;
        }

        public boolean isAppendTimestamp() {
            return appendTimestamp;
        }
    }

    /**
     * 根据配置获取字符池
     */
    private static String getCharPool(CharType type, boolean safe) {
        switch (type) {
            case ONLY_NUM:
                return safe ? NUMBER_SAFE : NUMBER;
            case ONLY_LOWER:
                return safe ? LOWER_SAFE : LOWER_LETTER;
            case ONLY_UPPER:
                return safe ? UPPER_SAFE : UPPER_LETTER;
            case NUM_LOWER:
                return safe ? NUMBER_SAFE + LOWER_SAFE : NUMBER + LOWER_LETTER;
            case NUM_UPPER:
                return safe ? NUMBER_SAFE + UPPER_SAFE : NUMBER + UPPER_LETTER;
            case ALL_MIX:
            default:
                return safe ? NUMBER_SAFE + LOWER_SAFE + UPPER_SAFE : NUMBER + LOWER_LETTER + UPPER_LETTER;
        }
    }

    /**
     * 根据配置生成单个ID
     */
    public static String generateId(IdConfig config) {
        int len = config.getLength();
        if (len <= 0) {
            throw new IllegalArgumentException("ID长度必须大于0");
        }
        String pool = getCharPool(config.getCharType(), config.isUseSafeChar());
        StringBuilder sb = new StringBuilder();

        // 拼接时间戳
        if (config.isAppendTimestamp()) {
            sb.append(System.currentTimeMillis());
        }

        // 生成随机字符
        for (int i = 0; i < len; i++) {
            int index = SECURE_RANDOM.nextInt(pool.length());
            sb.append(pool.charAt(index));
        }

        // 拼接前后缀返回完整ID
        return config.getPrefix() + sb + config.getSuffix();
    }

    /**
     * 自定义字符池生成ID
     * @param length 随机串长度
     * @param customChars 自定义字符集，如"abc123XYZ"
     * @param prefix 前缀
     * @param suffix 后缀
     * @param appendTs 是否追加时间戳
     */
    public static String generateCustomId(int length, String customChars, String prefix, String suffix, boolean appendTs) {
        if (length <= 0 || customChars == null || customChars.isEmpty()) {
            throw new IllegalArgumentException("长度不能小于等于0，自定义字符不能为空");
        }
        StringBuilder sb = new StringBuilder();
        if (appendTs) {
            sb.append(System.currentTimeMillis());
        }
        for (int i = 0; i < length; i++) {
            int idx = SECURE_RANDOM.nextInt(customChars.length());
            sb.append(customChars.charAt(idx));
        }
        return prefix + sb + suffix;
    }

    /**
     * 批量生成指定数量不重复ID
     * @param count 生成数量
     * @param config ID配置
     * @return 无重复ID列表
     */
    public static List<String> batchGenerate(int count, IdConfig config) {
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }
        Set<String> idSet = new HashSet<>();
        while (idSet.size() < count) {
            idSet.add(generateId(config));
        }
        return new ArrayList<>(idSet);
    }

    // ===================== 新增 UUID 相关方法 =====================
    /**
     * 生成不带横线的标准32位UUID（小写）
     * 示例：f47ac10b58cc4372a57d2b9349966666
     */
    public static String generateUUID() {
        String uuidStr = UUID.randomUUID().toString();
        // 移除所有 "-"
        return uuidStr.replace("-", "");
    }

    /**
     * 生成大写无横线UUID
     */
    public static String generateUpperUUID() {
        return generateUUID().toUpperCase();
    }

    // ===================== 快捷工具方法 =====================
    /**
     * 纯数字ID（验证码专用）
     * @param length 位数，如6位验证码
     */
    public static String getNumId(int length) {
        return generateId(new IdConfig()
                .length(length)
                .charType(CharType.ONLY_NUM)
                .useSafeChar(false));
    }

    /**
     * 普通字母数字混合ID
     */
    public static String getMixId(int length) {
        return generateId(new IdConfig().length(length));
    }

    /**
     * 带时间戳全局唯一ID（订单/设备ID）
     * @param randomLen 随机串长度
     */
    public static String getUniqueId(int randomLen) {
        return generateId(new IdConfig()
                .length(randomLen)
                .appendTimestamp(true));
    }
}