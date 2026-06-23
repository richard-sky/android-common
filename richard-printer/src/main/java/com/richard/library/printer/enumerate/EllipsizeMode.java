package com.richard.library.printer.enumerate;

/**
 * author Richard
 * date 2020/12/30 14:23
 * version V1.0
 * description: 列文本显示模式
 */
public enum EllipsizeMode {

    /**
     * 文本在该列显示不完时，则结尾处以省略号代替
     */
    ELLIPSIS,

    /**
     * 文本在该列显示不完时，则在当前位置开始显示，剩余显示不完的内容则下一行并跨列显示
     */
    LINE,

    /**
     * 文本在该列显示不完时，则在该列多行显示
     */
    COLUMN_LINE,

    /**
     * 文本在该列显示不完时，则换行到下一行开始显示
     */
    NEXT_LINE,

    /**单行显示，多余部分会被截断*/
    SINGLE_LINE;

}
