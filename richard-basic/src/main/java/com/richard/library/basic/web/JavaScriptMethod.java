package com.richard.library.basic.web;

import java.io.Serializable;

/**
 * <pre>
 * Description : js方法实现
 * Author : admin-richard
 * Date : 2022/3/31 15:06
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/3/31 15:06     admin-richard         new file.
 * </pre>
 *
 * 1.使用“@JavascriptInterface”注解实现方法，并且为public修饰
 */
public interface JavaScriptMethod extends Serializable {

    /**
     * 网页端调用java方法的实例名称
     */
    String getInstanceName();

}
