package com.richard.dev.common.mq.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * @author: Richard
 * @createDate: 2023/4/14 12:59
 * @version: 1.0
 * @description: mqtt连接监听回调
 */
public interface ConnectListener {

    /**
     * 连接成功时回调
     */
    void onSuccess(IMqttToken asyncActionToken);

    /**
     * 当需要重新连接mqtt的时候回调
     */
    void onNeedReconnect(Throwable exception);

}
