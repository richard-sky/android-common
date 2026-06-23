package com.richard.library.basic.util;

import com.richard.library.context.util.RegexUtil;

/**
 * <pre>
 * Description : 付款码相关工具类
 * Author : admin-richard
 * Date : 2019-12-13 21:36
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-12-13 21:36     admin-richard         new file.
 * </pre>
 */
public final class PayCodeUtil {

    /**
     * 微信付款码标记位（微信官方可能会更新该标记位）
     * 用户付款码条形码规则：18位纯数字，以10、11、12、13、14、15开头
     * 详见https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=5_1
     */
    private final static String weCharCodeRule = "^1[0-5]\\d{16}$";


    /**
     * 支付宝付款码标记（支付宝官方可能会更新该标记位）
     * 用户付款码，25-30 开头的长度为 16-24 位的数字，实际字符串长度以开发者获取的付款码长度为准；付款码使用一次即失效
     * 详见https://opendocs.alipay.com/open/194/106039/ 的条码支付快速接入章节的 入参中auth_code参数描述
     */
    private final static String aliPayCodeRule = "^(?:2[5-9]|30)\\d{14,22}$";

    /**
     * 是否为微信支付付款码
     *
     * @param payCode 付款码
     */
    public static boolean isWechatPayCode(String payCode) {
        return RegexUtil.isMatch(weCharCodeRule, payCode);
    }

    /**
     * 是否为支付宝付款码
     *
     * @param payCode 付款码
     */
    public static boolean isAliPayCode(String payCode) {
        return RegexUtil.isMatch(aliPayCodeRule, payCode);
    }

}
