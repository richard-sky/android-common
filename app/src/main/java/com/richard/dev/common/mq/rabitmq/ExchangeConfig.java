package com.richard.dev.common.mq.rabitmq;

/**
 * @author: Richard
 * @createDate: 2023/4/18 10:05
 * @version: 1.0
 * @description: 交换机配置
 */
public class ExchangeConfig {

    /**交换机*/
    private String exchange;

    /**路由key*/
    private String routeKey;

    /**队列名称*/
    private String queueName;

    public ExchangeConfig() {
    }

    public ExchangeConfig(String exchange, String routeKey, String queueName) {
        this.exchange = exchange;
        this.routeKey = routeKey;
        this.queueName = queueName;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
