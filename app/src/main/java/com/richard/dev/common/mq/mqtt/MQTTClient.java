package com.richard.dev.common.mq.mqtt;

import com.richard.library.context.util.DeviceUtil;
import com.richard.library.context.util.LogUtil;
import com.richard.library.context.util.ThreadUtil;
import com.richard.library.context.AppContext;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author: Richard
 * @createDate: 2023/4/14 9:25
 * @version: 1.0
 * @description: mqtt客户端
 */
public class MQTTClient {

    private final String TAG = "MQTTClient";

    private MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    private MessageHandler messageHandler;
    private ConnectListener connectListener;


    private MQTTClient() {
        this.init();
    }

    private static final class InstanceHolder {
        private static final MQTTClient instance = new MQTTClient();
    }

    public static MQTTClient get() {
        return InstanceHolder.instance;
    }


    /**
     * 初始化
     */
    private void init() {
        String host = "http://********:9999";
        String userName = "";
        String passWord = "";
        String clientId = DeviceUtil.getUniqueDeviceId();

        client = new MqttAndroidClient(AppContext.get(), host, clientId);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        //mqtt连接配置
        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        // 用户名
        conOpt.setUserName(userName);
        // 密码
        conOpt.setPassword(passWord.toCharArray());
    }

    /**
     * 消息回调
     */
    private final MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void connectionLost(Throwable cause) {
            //连接丢失后，可在此处重连
            LogUtil.eTag(TAG, "MQTT链接断开" + (cause != null ? cause.getMessage() : ""));
            connectListener.onNeedReconnect(cause);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            //收到消息
            String receiveMsg = new String(message.getPayload());
            LogUtil.dTag(TAG, String.format("topic: %s , messageArrived : %s", topic, receiveMsg));

            if (messageHandler != null) {
                ThreadUtil.getCachedPool().execute(() -> {
                    try {
                        messageHandler.handleMessage(receiveMsg);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            //publish后会回调该处
            LogUtil.dTag(TAG, "MQTT 发布消息完成");
        }
    };

    /**
     * 连接mqtt
     */
    public void connect() {
        if (client.isConnected()) {
            return;
        }

        try {
            client.connect(conOpt, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    LogUtil.dTag(TAG, "MQTT 连接成功");
                    //连接成功后开始订阅
                    connectListener.onSuccess(asyncActionToken);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    LogUtil.eTag(TAG, "MQTT 连接失败 " + exception.getMessage());
                    //连接失败后可进行重连
                    connectListener.onNeedReconnect(exception);
                }
            });
        } catch (MqttException e) {
            LogUtil.eTag(TAG, "mqttConnect fail:" + e.getMessage());
            //连接失败后可进行重连
            connectListener.onNeedReconnect(e);
        }
    }

    /**
     * 是否已经连接
     */
    public boolean isConnected() {
        return client.isConnected();
    }

    /**
     * 订阅
     */
    public void subscribe(String topic) {
        try {
            int qos = 0;//提供消息的服务质量，可传0、1或2
            // 订阅myTopic话题
            client.subscribe(topic, qos);
            LogUtil.dTag(TAG, "MQTT订阅成功:" + topic);
        } catch (MqttException e) {
            LogUtil.eTag(TAG, "MQTT订阅失败:" + e.getMessage());
        }
    }

    /**
     * 取消订阅
     */
    public void unSubscribe(String topic) {
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发布消息
     */
    public void publish(String topic, String msg) {
        int qos = 0;//提供消息的服务质量，可传0、1或2
        boolean retained = false;//是否在服务器保留断开连接后的最后一条消息
        try {
            if (client != null) {
                client.publish(topic, msg.getBytes(), qos, retained);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            client.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置连接监听
     */
    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    /**
     * 设置处理消息回调
     */
    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
