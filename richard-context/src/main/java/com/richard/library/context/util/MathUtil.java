package com.richard.library.context.util;


import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Description : 数学计算(加、减、乘、除、模运算、分转元、元转分、数值格式化)
 * Author : admin-richard
 * Date : 2017/4/18
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/4/18     admin-richard         new file.
 * </pre>
 */
public final class MathUtil {

    /**
     * 计算类型
     */
    public enum CalType {
        ADD, //加
        SUBTRACT,//减
        MULTIPLY, //乘
        DIVIDE,//除
        REMAINDER//取余数（模运算）

    }

    //不进行四舍五入，多余位数截断（向下取整）
//    public static RoundingMode ROUNDING_MODE = RoundingMode.DOWN;

    //不进行四舍五入，向上取整
    public static RoundingMode ROUNDING_MODE = RoundingMode.UP;

    //向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。(即四舍五入)
//    public static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    //银行家舍入法,舍去和上去的概率分别是50%,四舍六入五取偶（又称四舍六入五留双）法。
//    public static RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    private static final ThreadLocal<Map<String, DecimalFormat>> DECIMAL_FORMAT_THREAD_LOCAL =
            new ThreadLocal<Map<String, DecimalFormat>>() {
                @Override
                protected Map<String, DecimalFormat> initialValue() {
                    return new HashMap<>();
                }
            };

    /**
     * 获取指定格式的DecimalFormat
     *
     * @param pattern 格式
     */
    public static DecimalFormat getSafeDecimalFormat(String pattern) {
        Map<String, DecimalFormat> decimalFormatMap = DECIMAL_FORMAT_THREAD_LOCAL.get();
        //noinspection ConstantConditions
        DecimalFormat decimalFormat = decimalFormatMap.get(pattern);
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat(pattern);
            decimalFormatMap.put(pattern, decimalFormat);
        }
        return decimalFormat;
    }

    /**
     * 分转元
     *
     * @param fen   以分为单位的金额
     * @param scale 保留小数点后几位(小于0时不限制小数位数)
     */
    public static BigDecimal toYuan(Object fen, int scale) {
        return toYuan(fen, scale, ROUNDING_MODE);
    }

    /**
     * 分转元
     *
     * @param fen          以分为单位的金额
     * @param scale        保留小数点后几位(小于0时不限制小数位数)
     * @param roundingMode 舍入模式
     */
    public static BigDecimal toYuan(Object fen, int scale, RoundingMode roundingMode) {
        BigDecimal bigDecimal = converterToBigDecimal(fen);
        if (scale > 0) {
            bigDecimal = bigDecimal.divide(new BigDecimal("100"), scale, roundingMode);
        } else {
            bigDecimal = bigDecimal.divide(new BigDecimal("100"), roundingMode);
        }
        return bigDecimal;
    }

    /**
     * 元转分
     *
     * @param yuan 以元为单位的金额
     */
    public static long toFen(Object yuan) {
        return converterToBigDecimal(yuan)
                .multiply(new BigDecimal("100"))
                .longValue();
    }

    /**
     * 多个数相除并且保留指定位数(scale)
     *
     * @param scale 表示表示需要精确到小数点以后几位。(小于0时不限制小数位数)
     * @param vars  参与相除的数值
     */
    public static BigDecimal divide(int scale, Object... vars) {
        return calculate(CalType.DIVIDE, ROUNDING_MODE, scale, vars);
    }

    /**
     * 多个数相除并且保留指定位数(scale)
     *
     * @param scale        表示表示需要精确到小数点以后几位。(小于0时不限制小数位数)
     * @param roundingMode 舍入模式
     * @param vars         参与相除的数值
     */
    public static BigDecimal divide(int scale, RoundingMode roundingMode, Object... vars) {
        return calculate(CalType.DIVIDE, roundingMode, scale, vars);
    }

    /**
     * 多个数相乘并且保留指定位数(scale)
     *
     * @param scale 表示表示需要精确到小数点以后几位。(小于0时不限制小数位数)
     * @param vars  参与相除的数值
     */
    public static BigDecimal multiply(int scale, Object... vars) {
        return calculate(CalType.MULTIPLY, ROUNDING_MODE, scale, vars);
    }

    /**
     * 多个数相乘并且保留指定位数(scale)
     *
     * @param scale        表示表示需要精确到小数点以后几位。(小于0时不限制小数位数)
     * @param vars         参与相除的数值
     * @param roundingMode 舍入模式
     */
    public static BigDecimal multiply(int scale, RoundingMode roundingMode, Object... vars) {
        return calculate(CalType.MULTIPLY, roundingMode, scale, vars);
    }

    /**
     * 多个数的加法运算
     *
     * @param vars 参与相加的数值参数
     * @return 多个参数的和
     */
    public static BigDecimal addNoScale(Object... vars) {
        return calculate(CalType.ADD, ROUNDING_MODE, -1, vars);
    }

    /**
     * 多个数的加法运算
     *
     * @param vars         参与相加的数值参数
     * @param roundingMode 舍入模式
     * @return 多个参数的和
     */
    public static BigDecimal addNoScale(RoundingMode roundingMode, Object... vars) {
        return calculate(CalType.ADD, roundingMode, -1, vars);
    }


    /**
     * 多个数的加法运算
     *
     * @param scale 小数位保留位数(小于0时不限制小数位数)
     * @param vars  数字数组参数
     */
    public static BigDecimal add(int scale, Object... vars) {
        return calculate(CalType.ADD, ROUNDING_MODE, scale, vars);
    }

    /**
     * 多个数的加法运算
     *
     * @param scale        小数位保留位数(小于0时不限制小数位数)
     * @param roundingMode 舍入模式
     * @param vars         数字数组参数
     */
    public static BigDecimal add(int scale, RoundingMode roundingMode, Object... vars) {
        return calculate(CalType.ADD, roundingMode, scale, vars);
    }

    /**
     * 多个数的减法运算
     *
     * @param scale 小数位保留位数(小于0时不限制小数位数)
     * @param vars  数字数组参数
     */
    public static BigDecimal subtract(int scale, Object... vars) {
        return calculate(CalType.SUBTRACT, ROUNDING_MODE, scale, vars);
    }

    /**
     * 多个数的减法运算
     *
     * @param scale        小数位保留位数(小于0时不限制小数位数)
     * @param roundingMode 舍入模式
     * @param vars         数字数组参数
     */
    public static BigDecimal subtract(int scale, RoundingMode roundingMode, Object... vars) {
        return calculate(CalType.SUBTRACT, roundingMode, scale, vars);
    }

    /**
     * 多个数取余（模运算）
     *
     * @param scale 小数位保留位数(小于0时不限制小数位数)
     * @param vars  数字数组参数
     */
    public static BigDecimal remainder(int scale, Object... vars) {
        return calculate(CalType.REMAINDER, ROUNDING_MODE, scale, vars);
    }

    /**
     * 多个数取余（模运算）
     *
     * @param scale        小数位保留位数(小于0时不限制小数位数)
     * @param roundingMode 舍入模式
     * @param vars         数字数组参数
     */
    public static BigDecimal remainder(int scale, RoundingMode roundingMode, Object... vars) {
        return calculate(CalType.REMAINDER, roundingMode, scale, vars);
    }

    /**
     * 格式化数字格式
     *
     * @param num 数字
     */
    public static String formatNum(Object num) {
        return formatNum("0.00", num);
    }

    /**
     * 格式化数字格式
     *
     * @param format 格式：比如"0.00"
     * @param num    数字
     */
    public static String formatNum(String format, Object num) {
        return getSafeDecimalFormat(format).format(converterToBigDecimal(num));
    }


    /**
     * 指定数值的小数位的位数
     *
     * @param scale 保留小数点位数(小于0时不限制小数位数)
     * @param num   具体数值
     */
    public static BigDecimal scaleNum(int scale, Object num) {
        return scaleNum(scale, ROUNDING_MODE, num);
    }

    /**
     * 指定数值的小数位的位数
     *
     * @param scale        保留小数点位数(小于0时不限制小数位数)
     * @param roundingMode 舍入模式
     * @param num          具体数值
     */
    public static BigDecimal scaleNum(int scale, RoundingMode roundingMode, Object num) {
        if (scale < 0) {
            return converterToBigDecimal(num);
        }
        return converterToBigDecimal(num).setScale(scale, roundingMode);
    }

    /**
     * 当小数点位全为0的时候抹掉0
     *
     * @param num 数字
     */
    public static String replaceEndZero(Object num) {
        return replaceEndZero(num, -1);
    }

    /**
     * 当小数点位全为0的时候抹掉0
     *
     * @param num   数字
     * @param scale 保留小数点位数 (小于0时不限制小数位数)
     */
    public static String replaceEndZero(Object num, int scale) {
        return replaceEndZero(num, scale, ROUNDING_MODE);
    }

    /**
     * 当小数点位全为0的时候抹掉0
     *
     * @param num   数字
     * @param scale 保留小数点位数 (小于0时不限制小数位数)
     */
    public static String replaceEndZero(Object num, int scale, RoundingMode roundingMode) {
        if (num == null || (num instanceof String && TextUtils.isEmpty((CharSequence) num))) {
            return "";
        }

        if (scale <= 0 && toDouble(String.valueOf(num)) == 0) {
            return "0";
        }

        BigDecimal bigDecimal = converterToBigDecimal(num);
        if (scale >= 0) {
            bigDecimal = bigDecimal.setScale(scale, roundingMode);
        }
        return bigDecimal
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 加减乘除计算
     *
     * @param calType      计算类型(加、减、乘、除、模)
     * @param roundingMode 小数位进位方式，比如BigDecimal.ROUND_DOWN
     * @param scale        保留小数点位数(小于0时不限制小数位数)
     * @param vars         参与计算的数值可变参数
     */
    public static BigDecimal calculate(CalType calType, int roundingMode, int scale, Object... vars) {
        return calculate(calType, RoundingMode.valueOf(roundingMode), scale, vars);
    }

    /**
     * 加减乘除计算
     *
     * @param calType      计算类型(加、减、乘、除、模)
     * @param roundingMode 小数位进位方式，比如RoundingMode.ROUND_DOWN
     * @param scale        保留小数点位数(小于0时不限制小数位数)
     * @param vars         参与计算的数值可变参数
     */
    public static BigDecimal calculate(CalType calType, RoundingMode roundingMode, int scale, Object... vars) {
        if (vars == null || vars.length <= 0) {
            BigDecimal result = new BigDecimal("0");
            if (scale >= 0) {
                result = result.setScale(scale, roundingMode);
            }
            return result;
        }

        BigDecimal bigDecimal = converterToBigDecimal(vars[0]);
        BigDecimal tempBigDecimal;
        for (int i = 1; i < vars.length; i++) {
            tempBigDecimal = converterToBigDecimal(vars[i]);
            switch (calType) {
                case ADD:
                    bigDecimal = bigDecimal.add(tempBigDecimal);
                    break;
                case SUBTRACT:
                    bigDecimal = bigDecimal.subtract(tempBigDecimal);
                    break;
                case MULTIPLY:
                    bigDecimal = bigDecimal.multiply(tempBigDecimal);
                    break;
                case DIVIDE:
                    if (toDouble(tempBigDecimal.toPlainString()) == 0D) {
                        tempBigDecimal = new BigDecimal("1");
                    }
                    bigDecimal = bigDecimal.divide(tempBigDecimal, 16, roundingMode);
                    break;
                case REMAINDER:
                    bigDecimal = bigDecimal.divideAndRemainder(tempBigDecimal)[1];
                    break;
            }
        }

        if (scale >= 0) {
            bigDecimal = bigDecimal.setScale(scale, roundingMode);
        }

        return bigDecimal;
    }

    /**
     * 将数字值对象转换为有效数字字符串
     *
     * @param numObject 数字对象
     */
    public static BigDecimal converterToBigDecimal(Object numObject) {
        if (numObject == null) {
            return new BigDecimal("0");
        }

        if (numObject instanceof BigDecimal) {
            return (BigDecimal) numObject;
        }

        String numStr = numObject.toString();
        if (numStr.length() <= 0) {
            return new BigDecimal("0");
        }

        return new BigDecimal(numStr);
    }

    /**
     * double 字符串转double 类型
     */
    private static double toDouble(String doubleStr) {
        try {
            return TextUtils.isEmpty(doubleStr) ? 0D : Double.parseDouble(doubleStr);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0D;
        }
    }
}
