package com.richard.library.net.websocket;

/**
 * @author: Richard
 * @createDate: 2026/6/1 16:45
 * @version: 1.0
 * @description: WebSocket连接状态
 */
public enum ConnectionState {

    /// 未连接
    DISCONNECTED,

    /// 连接中
    CONNECTING,

    /// 已连接
    CONNECTED,

    /// 重连中
    RECONNECTING

}
