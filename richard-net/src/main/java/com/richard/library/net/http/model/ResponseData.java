package com.richard.library.net.http.model;

import java.io.Serializable;


/**
 * <pre>
 * Description : 接口响应数据实现interface
 * Author : admin-richard
 * Date : 2019-05-27 08:28
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-27 08:28     admin-richard         new file.
 * </pre>
 */
public interface ResponseData<T> extends Serializable {

    /**
     * 是否请求数据成功
     */
    boolean isSuccess();

    /**
     * 获取响应提示消息
     */
    String getMessage();

    /**
     * 获取响应数据状态码
     */
    String getCode();

    /**
     * 获取响应数据
     */
    T getData();

}
