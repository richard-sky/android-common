package com.richard.library.basic.widget;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * <pre>
 * Description : editText限制整数位和小数位位数InputFilter
 * Author : admin-richard
 * Date : 2020-01-10 20:53
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020-01-10 20:53     admin-richard         new file.
 * </pre>
 */
public class DecimalInputFilter implements InputFilter {

    //整数位位数(-1不限制)
    private int integerLength = -1;

    //小数点位位数(-1不限制)
    private int dotLength = -1;

    public DecimalInputFilter(int dotLength) {
        this.dotLength = dotLength;
    }

    public DecimalInputFilter(int integerLength, int dotLength) {
        this.integerLength = integerLength;
        this.dotLength = dotLength;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // source:当前输入的字符
        // start:输入字符的开始位置
        // end:输入字符的结束位置
        // dest：当前已显示的内容
        // dstart:当前光标开始位置
        // dent:当前光标结束位置
        String dValue = dest.toString();
        if (integerLength > -1 && !dValue.contains(".") && dValue.length() == integerLength) {
            return "";
        }

        if (dotLength > -1) {
            if (dest.length() == 0 && source.equals(".")) {
                return "0.";
            }

            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                if (dotValue.length() == dotLength) {//输入框小数的位数
                    return "";
                }
            }
        }

        return null;
    }

}
