package com.richard.library.basic.basic;

/**
 * <pre>
 * Description : UI层初始化
 * Author : admin-richard
 * Date : 2022/3/23 9:25
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/3/23 9:25      admin-richard         new file.
 * </pre>
 */
public interface UIInitializer {

    /**
     * 初始化布局
     */
    void initLayoutView();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 事件监听绑定
     */
    void bindListener();

}
