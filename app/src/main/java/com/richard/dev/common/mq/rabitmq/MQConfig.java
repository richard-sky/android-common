package com.richard.dev.common.mq.rabitmq;

/**
 * author Richard
 * date 2019-06-10 15:17
 * version V1.0
 * description: MQ配置
 */
public class MQConfig {
    /**
     * 服务器域名或IP
     */
    private String host;

    /**
     * 端口，默认5672
     */
    private int port;

    /**
     * 用户名，默认guest
     */
    private String username;

    /**
     * 用户密码，默认guest
     */
    private String password;

    /**
     * 虚拟目录，默认/
     */
    private String virtualHost = "/";

    /**
     * 客户端提供者名称
     */
    private String clientProvidedName;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getClientProvidedName() {
        return clientProvidedName;
    }

    public void setClientProvidedName(String clientProvidedName) {
        this.clientProvidedName = clientProvidedName;
    }
}
