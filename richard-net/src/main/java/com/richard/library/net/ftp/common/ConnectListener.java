package com.richard.library.net.ftp.common;

/**
 * @author: Richard
 * @createDate: 2026/6/8 10:39
 * @version: 1.0
 * @description: FTP连接监听
 */
public interface ConnectListener {

    /**
     * 连接成功
     */
    /**
     * 连接成功(主线程回调)
     */
    void onConnected();

    /**
     * 断开连接(主线程回调)
     */
    void onDisconnected();

    /**
     * 连接错误(主线程回调)
     *
     * @param error 错误信息
     */
    void onConnectError(String error);

}
