package com.richard.library.net.http.exception;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 * Description : http包下通用异常
 * Author : admin-richard
 * Date : 2022/11/4 16:32
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/11/4 16:32      admin-richard         new file.
 * </pre>
 */
public class HttpException extends RuntimeException {

    private static final long serialVersionUID = -3864091066462401664L;

    /**
     * 原始异常对象
     */
    private Throwable originException;

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable originException) {
        super(message);
        this.originException = originException;
    }

    public Throwable getOriginException() {
        return originException;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + "HttpException{" +
                "originException=" + originException +
                '}';
    }

    @Nullable
    @Override
    public String getMessage() {
        return super.getMessage() + " : " + (originException != null ? originException.getMessage() : "");
    }

    @NonNull
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
