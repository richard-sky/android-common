package com.richard.dev.common.mq.rabitmq;

/**
 * @author: PHILIPS
 * @createDate: 2023/4/14 12:03
 * @version: 1.0
 * @description: 消息处理
 */
public interface MessageHandler {

    /**
     * 处理消息
     *
     * @param message 消息
     * @return 是否已经处理了消息
     */
    boolean handleMessage(String message);

}
