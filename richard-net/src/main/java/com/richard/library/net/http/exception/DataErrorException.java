package com.richard.library.net.http.exception;

import androidx.annotation.NonNull;

/**
 * <pre>
 * Description : 响应数据错误异常
 * Author : admin-richard
 * Date : 2019-09-20 09:04
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-09-20 09:04     admin-richard         new file.
 * </pre>
 */
public class DataErrorException extends RuntimeException {

    private static final long serialVersionUID = 2920245708999590056L;

    /**
     * @param message 异常的描述信息，也就是在打印栈追踪信息时异常类名后面紧跟着的描述字符串,性能最好
     */
    public DataErrorException(String message) {
        super(message);
    }

    @NonNull
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
