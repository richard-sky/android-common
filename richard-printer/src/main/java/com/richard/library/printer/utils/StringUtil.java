package com.richard.library.printer.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * author Richard
 * date 2020/12/23 10:54
 * version V1.0
 * description: 字符串工具类
 */
final class StringUtil {

    /**
     * 按字节长度截取字符串
     * 注意：长度是以byte为单位的，一个汉字是2个byte
     *
     * @param text    要截取的字符串
     * @param charset 字符编码
     * @param limit   每次截取字节数
     * @return 截取结果
     */
    public static List<String> substring(String text, Charset charset, int limit) {
        return substring(text,charset,limit,0);
    }


    /**
     * 按字节长度截取字符串
     * 注意：长度是以byte为单位的，一个汉字是2个byte
     *
     * @param text       要截取的字符串
     * @param charset    字符编码
     * @param limit      每次截取字节数
     * @param firstLimit 第一次截取字节数
     * @return 截取结果
     */
    public static List<String> substring(String text, Charset charset, int limit, int firstLimit) {
        if (text == null) {
            return new ArrayList<>();
        }

        charset = charset == null ? Charset.forName("GBK") : charset;

        List<String> result = new ArrayList<>();
        int textLength;
        char[] tempChar;
        int reInt;
        int index;
        int tempLimit;
        String reStr;

        String[] splitArray = text.split("\\n");
        for(String splitItem : splitArray){

            textLength = splitItem.getBytes(charset).length;
            tempChar = splitItem.toCharArray();
            reInt = 0;
            index = 0;
            tempLimit = 0;
            reStr = "";

            while (reInt < textLength) {

                if(firstLimit > 0){
                    tempLimit = firstLimit;
                    firstLimit = 0;
                }else{
                    tempLimit = limit;
                }

                for (int i = 0; i < tempLimit && index < tempChar.length; i++) {
                    String s1 = String.valueOf(tempChar[index]);

                    //若字节长度超过了当前指定的分割字节数量，则忽略该次切割，放到下次切割
                    if (reStr.concat(s1).getBytes(charset).length > tempLimit) {
                        break;
                    }

                    reInt += s1.getBytes(charset).length;
                    reStr = reStr.concat(s1);
                    index++;
                }

                result.add(reStr);
                reStr = "";
            }
        }

        return result;
    }

}
