package com.richard.library.context.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * Description : 正则验证工具类
 * Author : admin-richard
 * Date : 2020-02-17 11:09
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020-02-17 11:09     admin-richard         new file.
 * </pre>
 */
public final class RegexUtil {

    /**
     * 正则表达式
     */
    public interface Exp {

        /**
         * 简易手机号正则
         */
        String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
        /**
         * 精准手机号正则
         * <p>中国移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、165、172、178、182、183、184、187、188、195、197、198</p>
         * <p>中国联通：130、131、132、145、155、156、166、167、175、176、185、186、196</p>
         * <p>中国电信：133、149、153、162、173、177、180、181、189、190、191、199</p>
         * <p>中国广电：192</p>
         * <p>全球星：1349</p>
         * <p>虚拟运营商：170、171</p>
         */
        String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[579])|(15[0-35-9])|(16[2567])|(17[0-35-8])|(18[0-9])|(19[0-35-9]))\\d{8}$";
        /**
         * 固定电话正则
         */
        String REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}$";
        /**
         * 15位身份证号码正则
         */
        String REGEX_ID_CARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        /**
         * 18位身份证号码正则
         */
        String REGEX_ID_CARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
        /**
         * 邮箱正则
         */
        String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        /**
         * URL正则
         */
        String REGEX_URL = "[a-zA-z]+://[^\\s]*";
        /**
         * 中文字符正则
         */
        String REGEX_ZH = "^[\\u4e00-\\u9fa5]+$";
        /**
         * 用户名正则
         * <p>取值范围：a-z、A-Z、0-9、_、中文字符</p>
         * <p>不能以下划线结尾</p>
         * <p>长度为6-20位</p>
         */
        String REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
        /**
         * yyyy-MM-dd 格式日期正则
         */
        String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
        /**
         * IP地址正则
         */
        String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

        ///////////////////////////////////////////////////////////////////////////
        // 以下来源：http://tool.oschina.net/regex
        ///////////////////////////////////////////////////////////////////////////

        /**
         * 双字节字符正则（包含中文）
         */
        String REGEX_DOUBLE_BYTE_CHAR = "[^\\x00-\\xff]";
        /**
         * 空白行正则
         */
        String REGEX_BLANK_LINE = "\\n\\s*\\r";
        /**
         * QQ号正则
         */
        String REGEX_QQ_NUM = "[1-9][0-9]{4,}";
        /**
         * 中国邮政编码正则
         */
        String REGEX_CHINA_POSTAL_CODE = "[1-9]\\d{5}(?!\\d)";
        /**
         * 整数正则
         */
        String REGEX_INTEGER = "^(-?[1-9]\\d*)|0$";
        /**
         * 正整数正则
         */
        String REGEX_POSITIVE_INTEGER = "^[1-9]\\d*$";
        /**
         * 负整数正则
         */
        String REGEX_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
        /**
         * 非负整数正则
         */
        String REGEX_NOT_NEGATIVE_INTEGER = "^[1-9]\\d*|0$";
        /**
         * 非正整数正则
         */
        String REGEX_NOT_POSITIVE_INTEGER = "^-[1-9]\\d*|0$";
        /**
         * 浮点数正则
         */
        String REGEX_FLOAT = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
        /**
         * 正浮点数正则
         */
        String REGEX_POSITIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
        /**
         * 负浮点数正则
         */
        String REGEX_NEGATIVE_FLOAT = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$";
        /**
         * 非负浮点数正则
         */
        String REGEX_NOT_NEGATIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$";
        /**
         * 非正浮点数正则
         */
        String REGEX_NOT_POSITIVE_FLOAT = "^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$";
    }

    /**
     * 提取字符串中的数字
     *
     * @param input
     * @return
     */
    public static String getNumStr(CharSequence input) {
        return Pattern.compile("[^0-9]").matcher(input).replaceAll("");
    }

    /**
     * 提取指定字符串中字符
     *
     * @param content 提取内容
     * @param regex   指定提取字符正则表达式
     * @return 提取出的内容
     */
    public static String getStr(String content, String regex) {
        return Pattern.compile(regex).matcher(content).replaceAll("");
    }

    /**
     * 验证URL
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isURL(final CharSequence input) {
        return isMatch("[a-zA-z]+://[^\\s]*", input);
    }

    /**
     * 验证是否为汉字
     */
    public static boolean isChineseChar(String str) {
        return isMatch("[\u4e00-\u9fa5]", str);
    }

    /**
     * 验证是否全为字母
     */
    public static boolean isSpell(String str) {
        return isMatch("^[a-zA-Z]+$", str);
    }


    /**
     * 验证邮箱
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isEmail(CharSequence input) {
        return isMatch("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", input);
    }

    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(String regex, CharSequence input) {
//        return input != null && input.length() > 0 && Pattern.matches(regex, input);
        return Pattern.compile(regex).matcher(input).find();
    }


    /**
     * 去掉字符串中的空格
     *
     * @param targetStr 原字符串
     */
    public static String replaceSpace(String targetStr) {
        if (TextUtils.isEmpty(targetStr)) {
            return "";
        }
        return targetStr.replaceAll(" ", "");
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str 字符
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*|-[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isDecimals(String decimals) {
        if (!TextUtils.isEmpty(decimals)) {
            Pattern decimalRegex = Pattern.compile("[0-9]\\d*|[0-9]\\d*.[0-9]\\d*|-[1-9]\\d*|-([0-9]\\d*.[0-9]\\d*)");
            return decimalRegex.matcher(decimals).matches();
        }
        return false;
    }

    /**
     * 手机号码验证
     *
     * @param mobiles 手机号
     */
    public static boolean isMobileNo(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        Pattern p = Pattern.compile("[1]\\d{10}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 验证是否属于IP V4 地址
     *
     * @param ipv4 IP地址
     */
    public static boolean isIPV4(String ipv4) {
        if (!TextUtils.isEmpty(ipv4)) {
            return Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$")
                    .matcher(ipv4)
                    .matches();
        }
        return false;
    }

    /**
     * 验证是否属于IP V6 地址
     *
     * @param ipv6 IPV6地址
     */
    public static boolean isIPV6(String ipv6) {
        if (!TextUtils.isEmpty(ipv6)) {
            return Pattern.compile("^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^::([\\da-fA-F]{1,4}:){0,4}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){2}:([\\da-fA-F]{1,4}:){0,2}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){4}:((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$|^:((:[\\da-fA-F]{1,4}){1,6}|:)$|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)$|^([\\da-fA-F]{1,4}:){2}((:[\\da-fA-F]{1,4}){1,4}|:)$|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)$|^([\\da-fA-F]{1,4}:){4}((:[\\da-fA-F]{1,4}){1,2}|:)$|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?$|^([\\da-fA-F]{1,4}:){6}:$")
                    .matcher(ipv6)
                    .matches();
        }
        return false;
    }

    /**
     * 检测是否为中文
     */
    public static boolean checkChina(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]{1,24}");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 检查验证码是否合法
     *
     * @param verificationCode
     */
    public static boolean checkVerificationCode(String verificationCode) {
        if (TextUtils.isEmpty(verificationCode)) {
            return false;
        }
        Pattern p = Pattern.compile("[0-9]{6}$");
        Matcher m = p.matcher(verificationCode);
        return m.matches();
    }

    /**
     * 根据字符不同长度来“*”替换,达到保密
     *
     * @param str 需要“*”替换的字符串
     * @return “*”替换后的字符串
     */
    public static String replaceWithStar(String str) {
        if (str == null) {
            str = "";
        }

        String regular = "";
        int strLength = str.length();

        if (strLength <= 1) {
            regular = "\\*";
        } else if (strLength == 2) {
            regular = "(?<=\\w{0})\\w(?=\\w{1})";
        } else if (strLength <= 6) {
            regular = "(?<=\\d{1})\\d(?=\\d{1})";
        } else if (strLength == 7) {
            regular = "(?<=\\d{1})\\d(?=\\d{2})";
        } else if (strLength == 8) {
            regular = "(?<=\\d{2})\\d(?=\\d{2})";
        } else if (strLength == 9) {
            regular = "(?<=\\d{2})\\d(?=\\d{3})";
        } else if (strLength == 10) {
            regular = "(?<=\\d{3})\\d(?=\\d{3})";
        } else {
            regular = "(?<=\\d{3})\\d(?=\\d{4})";
        }

        return str.replaceAll(regular, "*");

    }
}
