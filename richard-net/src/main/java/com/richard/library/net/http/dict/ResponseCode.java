package com.richard.library.net.http.dict;

/**
 * <pre>
 * Description : 本地响应状态码
 * Author : admin-richard
 * Date : 2019-05-27 09:23
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-27 09:23     admin-richard         new file.
 * </pre>
 */
public enum ResponseCode {

    FAIL("-1", "请求数据失败"),

    BODY_NULL("-2", "未获取到数据"),

    NET_ERROR("-3", "网络连接已断开,请检查网络连接是否正常"),

    DATA_ERROR("-4","响应数据格式错误"),

    FILE_DOWNLOAD_FAIL("-5","文件下载失败"),

    REQUEST_TIME_OUT("-6", "请求网络超时,请检查网络是否正常");


    private final String code;

    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
