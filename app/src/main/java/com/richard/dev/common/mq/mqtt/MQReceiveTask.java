package com.richard.dev.common.mq.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * author Richard
 * date 2019-11-19 20:24
 * version V1.0
 * description: MQ消息接收任务
 */
public class MQReceiveTask implements Runnable {

    private final MessageHandler messageHandler;

    public MQReceiveTask(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        ConnectListener connectListener = new ConnectListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                //EventBusUtil.post(EventBusType.MQ_STATE,"已连接");
                MQTTClient.get().setMessageHandler(messageHandler);
                MQTTClient.get().subscribe(TopicProvider.stallPrint());
            }

            @Override
            public void onNeedReconnect(Throwable exception) {
                //EventBusUtil.post(EventBusType.MQ_STATE,"重连中");
                lazyRun();
            }
        };
        MQTTClient.get().setConnectListener(connectListener);
    }

    @Override
    public void run() {
        //EventBusUtil.post(EventBusType.MQ_STATE,"连接中");
        MQTTClient.get().connect();
    }

    /**
     * 延迟执行
     */
    private void lazyRun() {
        try {
            Thread.sleep(5000);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.run();
    }

    public void release() {
        MQTTClient.get().unSubscribe(TopicProvider.stallPrint());
    }
}
