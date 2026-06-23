package com.richard.library.basic.web;

import java.io.Serializable;

/**
 * <pre>
 * Description : html富文本内容加载器
 * Author : admin-richard
 * Date : 2022/7/14 9:44
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/7/14 9:44     admin-richard         new file.
 * </pre>
 */
public interface HtmlLoader extends Serializable {

    /**
     * 获取网页内容标题
     *
     * @return 返回为null时默认自动获取网页内容标题
     */
    default String getTitle() {
        return null;
    }

    /**
     * 是否使用TextView控件显示
     */
    default boolean isTextViewShow() {
        return false;
    }

    /**
     * 获取网页内容
     *
     * @return String
     */
    String getHtml();


}
