package com.richard.library.net.websocket;

import okhttp3.Response;
import okio.ByteString;

/**
 * @author: Richard
 * @createDate: 2026/6/1 16:46
 * @version: 1.0
 * @description: websocket回调接口(以下方法均是子线程回调)
 */
public interface WebSocketCallback {

    /**
     * 连接成功
     */
    void onConnected(Response response);

    /**
     * 收到文本消息
     *
     * @param message 消息
     */
    void onMessage(String message);

    /**
     * 收到二进制消息
     *
     * @param bytes 二进制数据
     */
    void onMessage(ByteString bytes);

    /**
     * 连接断开
     *
     * @param code   code码
     * @param reason 断开原因
     */
    void onDisconnected(int code, String reason);

    /**
     * 发生错误
     *
     * @param error 异常
     */
    void onError(Throwable error);

}
