package com.richard.dev.common.mq.rabitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.richard.library.context.simple.SimpleException;
import com.richard.library.context.util.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * author Richard
 * date 2020-02-15 11:52
 * version V1.0
 * description: MQ 连接
 */
public final class MQClient {

    private final String TAG = "MQ";

    private static MQClient instance;

    //服务端配置
    private MQConfig config;
    private ConnectionFactory factory;
    private Connection connection;

    public static MQClient get() {
        if (instance == null) {
            synchronized (MQClient.class) {
                if (instance == null) {
                    instance = new MQClient();
                }
            }
        }

        return instance;
    }

    /**
     * 初始化配置
     */
    public void init(MQConfig config) {
        this.config = config;
    }


    /**
     * 连接MQ服务
     */
    public Connection connect() throws IOException, TimeoutException {
        if (config == null) {
            throw new SimpleException("未配置MQ连接");
        }

        if (!this.isAvailableConnect()) {
            synchronized (MQClient.class) {
                if (connection == null || !connection.isOpen()) {
                    if (connection != null && connection.isOpen()) {
                        try {
                            connection.close();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    // 1.设置MQ相关的信息
                    factory = new ConnectionFactory();
                    factory.setHost(config.getHost());
                    factory.setPort(config.getPort());
                    factory.setUsername(config.getUsername());
                    factory.setPassword(config.getPassword());
                    factory.setVirtualHost(config.getVirtualHost());
                    factory.setAutomaticRecoveryEnabled(true);//恢复连接，通道
                    factory.setNetworkRecoveryInterval(5000L);
                    factory.setRequestedHeartbeat(5);
                    factory.setConnectionTimeout(10000); //连接超时时间ms
                    factory.setTopologyRecoveryEnabled(true); //恢复通道中 转换器，队列，绑定关系等

                    Map<String, Object> clientPropertiesMap = new HashMap<>();
                    clientPropertiesMap.put("connection_name", config.getClientProvidedName());
                    factory.setClientProperties(clientPropertiesMap);


                    // 2.创建一个新的连接
                    connection = factory.newConnection();
                }
            }
        }

        return connection;
    }

    /**
     * 获取连接
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 连接是否可用
     */
    public boolean isAvailableConnect() {
        return connection != null && connection.isOpen();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param queueName    队列名称
     * @param exchange     交换机
     * @param exchangeType 交换机类型
     * @param routingKey   路由器关键字
     * @param isAutoAck    是否自动消费消息
     * @param handler      消息处理器
     * @return 订阅通道
     * @throws IOException
     */
    public Channel subscribe(String queueName, String exchange, BuiltinExchangeType exchangeType, String routingKey, boolean isAutoAck, final MessageHandler handler) throws IOException {
        if (!this.isAvailableConnect()) {
            throw new IOException("MQ连接不可用");
        }

        // 暂时先使用一个connection多个channel的方式，后续根据量进行优化
        Channel channel = MQClient.get().getConnection().createChannel();
        //这里的创建队列,是为了防止 消费 在 生产 之前
        channel.queueDeclare(queueName, true, false, false, null);
        //交换声明
        channel.exchangeDeclare(exchange, exchangeType, true, false, null);
        //绑定队列
        channel.queueBind(queueName, exchange, routingKey, null);
        //一次只发送一个，处理完成一个再获取下一个
        channel.basicQos(0, 1, false);

        //创建消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                try {
                    String message = new String(body);
                    LogUtil.dTag(TAG, "subscribe接收消息: ", message);

                    //处理消息
                    boolean handleResult = handler.handleMessage(message);

                    if (handleResult) {
                        LogUtil.dTag(TAG, "subscribe消费消息成功: ", message);
                        if (!isAutoAck) {
                            getChannel().basicAck(envelope.getDeliveryTag(), false);
                        }
                    } else {
                        LogUtil.dTag(TAG, "subscribe消费消息失败: ", message);
                        if (!isAutoAck) {
                            //自动把消息塞回RabbitMQ的队列头部（不是尾部）
                            getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                        }
                    }
                } catch (Exception e) {
                    LogUtil.eTag(TAG, "subscribe处理发生异常." + e.getLocalizedMessage());
                    try {
                        if (!isAutoAck) {
                            //自动把消息塞回RabbitMQ的队列头部（不是尾部）
                            getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        // 消费消息，不使用消息自动确认机制
        channel.basicConsume(queueName, isAutoAck, consumer);

        return channel;
    }

    /**
     * 向消息队列中发布一条消息
     *
     * @param exchange     交换机
     * @param exchangeType 交换机类型
     * @param routingKey   路由关键字
     * @param message      要发送的消息
     * @return
     * @throws IOException
     */
    public boolean publish(String exchange, BuiltinExchangeType exchangeType, String routingKey, String message) throws IOException {
        if (!this.isAvailableConnect()) {
            throw new IOException("MQ连接不可用");
        }

        try (Channel channel = MQClient.get().getConnection().createChannel()) {
            //创建一个通道

            channel.exchangeDeclare(
                    exchange
                    , exchangeType
                    , true
            );
            channel.queueDeclare(
                    exchange
                    , true
                    , false
                    , false
                    , null
            );

            //在此频道上启用发布者确认
            channel.confirmSelect();

            //开始发布
            channel.basicPublish(
                    exchange
                    , routingKey
                    , new AMQP.BasicProperties.Builder().deliveryMode(2).build()
                    , message.getBytes()
            );
            LogUtil.dTag(TAG, "MessagePublisher消息推送成功 : " + message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.eTag(TAG, "MessagePublisher消息推送异常, 异常消息 : " + e.getMessage());
        }
        return false;
    }
}
