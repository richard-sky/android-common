package com.richard.library.net.ftp.common;

/**
 * @author: Richard
 * @createDate: 2026/6/8 11:31
 * @version: 1.0
 * @description: 取消传输检查
 */
public interface CancelChecker {

    /**
     * 是否取消
     */
    boolean isCanceled();

}
