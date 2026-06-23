package com.richard.library.context.simple;


/**
 * <pre>
 * Description : 通用回调
 * Author : admin-richard
 * Date : 2021-09-11 11:11
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021-09-11 11:11     admin-richard         new file.
 * </pre>
 */
@FunctionalInterface
public interface SimpleCallback<T> {
    /**
     * 某业务完成时
     *
     * @param data 携带数据
     */
    void onCompleted(T data);
}
