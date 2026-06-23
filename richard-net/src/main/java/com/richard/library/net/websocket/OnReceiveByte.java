package com.richard.library.net.websocket;

import okio.ByteString;

/**
 * @author: Richard
 * @createDate: 2026/6/2 16:21
 * @version: 1.0
 * @description: 二进制消息接收监听
 */
@FunctionalInterface
public interface OnReceiveByte {

    /**
     * 当前接收到二进制消息时回调
     */
    void onReceiveByte(ByteString message);

}
