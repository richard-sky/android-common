package com.richard.library.net.http.util;

import com.richard.library.net.http.exception.HttpException;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;


/**
 * <pre>
 * Description : 网络请求工具类
 * Author : admin-richard
 * Date : 2022/8/26 16:04
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/8/26 16:04     admin-richard         new file.
 * </pre>
 */
public final class Utils {

    /**
     * 是否属于网络请求超时异常(包含建立连接超时异常的验证)
     */
    public static boolean isTimeout(Throwable error) {
        return validateTimeout(error, true);
    }

    /**
     * 是否属于网络请求超时异常(不包含建立连接超时异常的验证)
     */
    public static boolean isSocketIOTimeout(Throwable error) {
        return validateTimeout(error, false);
    }

    /**
     * 验证异常是否属于网络请求超时
     */
    private static boolean validateTimeout(Throwable e, boolean isValidateConnectException) {
        if (e == null) {
            return false;
        }

        Class<?> clazz = e.getClass();
        boolean isTimeout = (isValidateConnectException && (clazz.isAssignableFrom(ConnectException.class) || clazz.isAssignableFrom(ConnectTimeoutException.class)))
                || clazz.isAssignableFrom(InterruptedIOException.class)
                || clazz.isAssignableFrom(SocketException.class)
                || clazz.isAssignableFrom(SocketTimeoutException.class)
                || clazz.isAssignableFrom(IOException.class);

        if (isTimeout) {
            return true;
        }

        if (e instanceof HttpException th) {
            return validateTimeout(th.getOriginException(), isValidateConnectException);
        }

        return false;
    }
}
