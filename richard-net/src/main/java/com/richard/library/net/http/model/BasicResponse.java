package com.richard.library.net.http.model;

import android.text.TextUtils;

/**
 * <pre>
 * Description : 基本响应数据（根据实际业务可调整）
 * Author : admin-richard
 * Date : 2019-06-06 15:07
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-06 15:07     admin-richard         new file.
 * </pre>
 */
public class BasicResponse<T> implements ResponseData<T> {

    private static final long serialVersionUID = 2147573255985566723L;

    /**
     * 成功状态码
     */
    public static final String SUCCESS_CODE_200 = "200";

    /**
     * 数据
     */
    private T data;

    /**
     * 状态码（接口兼容）
     */
    private String code;

    /**
     * 消息
     */
    private String message;

    /**
     * 是否成功
     */
    private boolean success;


    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        if (TextUtils.isEmpty(message) && !this.isSuccess()) {
            return String.format("操作失败[code = %s]", this.code);
        }
        return this.message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public T getData() {
        return this.data;
    }

    //----------------------------------------set method---------------------------------------


    public void setData(T data) {
        this.data = data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
