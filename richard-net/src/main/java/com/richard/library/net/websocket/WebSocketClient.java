package com.richard.library.net.websocket;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.TypeReference;
import com.richard.library.context.util.JsonKt;
import com.richard.library.context.util.LogUtil;
import com.richard.library.context.util.ObjectUtilKt;
import com.richard.library.context.util.ThreadUtil;
import com.richard.library.net.http.request.Requester;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @author: Richard
 * @createDate: 2026/6/1 16:27
 * @version: 1.0
 * @description: WebSocket客户端
 */
public class WebSocketClient {

    private static final String TAG = "WebSocketClient";

    private WebSocket webSocket;
    private int currentReconnectCount = 0;             // 当前重连次数
    private final Builder config;
    private ConnectionState currentState = ConnectionState.DISCONNECTED;
    private final Set<OnReceive<?>> onReceives = new LinkedHashSet<>();
    private final Set<OnReceiveByte> onReceiveBytes = new LinkedHashSet<>();

    private WebSocketClient(Builder config) {
        this.config = config;
    }

    // 心跳任务
    private ThreadUtil.RunTask heartbeatTask = new ThreadUtil.RunTask() {
        @Override
        public void runEvent() {
            if (isConnected()) {
                sendMessage(config.heartbeatMessage);
            }
        }
    };

    // 重连任务
    private final ThreadUtil.RunTask reconnectTask = new ThreadUtil.RunTask() {
        @Override
        public void runEvent() {
            if (config.autoReconnect && (config.maxReconnectCount < 0 || currentReconnectCount < config.maxReconnectCount)) {
                currentReconnectCount++;
                LogUtil.iTag(TAG, "尝试第 " + currentReconnectCount + " 次重连");

                try {
                    connect();
                } catch (Throwable e) {
                    LogUtil.wTag(TAG, "重连过程中发生错误: " + e.getMessage());
                }
                return;
            }

            if (currentReconnectCount >= config.maxReconnectCount) {
                LogUtil.wTag(TAG, "已达到最大重连次数，停止重连");
            }

            cancel(true);
        }
    };

    /**
     * 连接WebSocket(异步)
     */
    public void connect() {
        try {
            this.startConnect(false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 阻塞连接WebSocket
     */
    public void blockingConnect() throws Throwable {
        this.startConnect(true);
    }

    /**
     * 连接WebSocket
     *
     * @param isBlocking 是否同步阻塞连接webSocket
     */
    private synchronized void startConnect(boolean isBlocking) throws Throwable {
        if (currentState == ConnectionState.CONNECTED ||
                currentState == ConnectionState.CONNECTING) {
            LogUtil.wTag(TAG, "WebSocket已连接或正在连接");
            return;
        }

        setState(ConnectionState.CONNECTING);

        //同步锁相关
        CountDownLatch countDownLatch;
        Throwable[] error;
        if (isBlocking) {
            countDownLatch = new CountDownLatch(1);
            error = new Throwable[1];
        } else {
            countDownLatch = null;
            error = null;
        }

        Request request = Requester.create()
                .url(config.url, config.params)
                .headers(config.header)
                .build();

        webSocket = config.okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                try {
                    setState(ConnectionState.CONNECTED);
                    currentReconnectCount = 0;  // 重置重连计数

                    LogUtil.iTag(TAG, "WebSocket连接成功");

                    // 在主线程执行回调
                    if (config.callback != null) {
                        config.callback.onConnected(response);
                    }

                    // 开始心跳
                    if (config.enableHeartbeat && heartbeatTask.isDone()) {
                        heartbeatTask = ThreadUtil.executeBySchedule(1000, config.heartbeatInterval, TimeUnit.MILLISECONDS, heartbeatTask);
                    }
                } finally {
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                    }
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                LogUtil.dTag(TAG, "收到消息: " + text);
                if (config.callback != null) {
                    config.callback.onMessage(text);
                }
                onHandleMessage(text);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                LogUtil.dTag(TAG, "收到二进制消息，大小: " + bytes.size());
                if (config.callback != null) {
                    config.callback.onMessage(bytes);
                }
                onHandleMessage(bytes);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                LogUtil.wTag(TAG, "连接关闭中，code: " + code + ", reason: " + reason);
                setState(ConnectionState.DISCONNECTED);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                setState(ConnectionState.DISCONNECTED);
                stopHeartbeat();

                LogUtil.wTag(TAG, "连接已关闭，code: " + code + ", reason: " + reason);

                if (config.callback != null) {
                    config.callback.onDisconnected(code, reason);
                }

                // 尝试重连
                if (config.autoReconnect) {
                    scheduleReconnect();
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                try {
                    if (error != null) {
                        error[0] = t;
                    }
                    setState(ConnectionState.DISCONNECTED);
                    stopHeartbeat();

                    LogUtil.eTag(TAG, "连接失败: " + t.getMessage());

                    if (config.callback != null) {
                        config.callback.onError(t);
                    }

                    // 尝试重连
                    if (config.autoReconnect) {
                        scheduleReconnect();
                    }
                } finally {
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                    }
                }
            }
        });

        if (countDownLatch != null) {
            try {
                // 当前行阻塞，直到异步完成
                countDownLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (error[0] != null) {
                throw error[0];
            }
        }
    }

    /**
     * 发送消息
     *
     * @param message 消息内容
     * @return 是否发送成功
     */
    public boolean sendMessage(String message) {
        if (webSocket != null && isConnected()) {
            boolean result = webSocket.send(message);
            if (result) {
                LogUtil.dTag(TAG, "发送消息: " + message);
            } else {
                LogUtil.eTag(TAG, "发送消息失败");
            }
            return result;
        }
        LogUtil.eTag(TAG, "WebSocket未连接，无法发送消息");
        return false;
    }

    /**
     * 发送二进制消息
     *
     * @param bytes 二进制数据
     * @return 是否发送成功
     */
    public boolean sendMessage(ByteString bytes) {
        if (webSocket != null && isConnected()) {
            boolean result = webSocket.send(bytes);
            if (result) {
                LogUtil.dTag(TAG, "发送二进制消息，大小: " + bytes.size());
            } else {
                LogUtil.eTag(TAG, "发送二进制消息失败");
            }
            return result;
        }
        LogUtil.eTag(TAG, "WebSocket未连接，无法发送消息");
        return false;
    }


    /**
     * 同步调用api获取数据(阻塞方法)
     *
     * @param params 请求参数
     * @param filter 接收数据过滤
     * @param <T>    返回数据泛型
     * @return 返回数据
     */
    public <T> T invoke(Object params, TypeReference<T> type, @NonNull InvokeFilter<T> filter) {
        return this.invoke(params, type.getType(), filter);
    }

    /**
     * 同步调用api获取数据(阻塞方法)
     *
     * @param params 请求参数
     * @param filter 接收数据过滤
     * @param <T>    返回数据泛型
     * @return 返回数据
     */
    @SuppressWarnings("unchecked")
    public <T> T invoke(Object params, Type type, @NonNull InvokeFilter<T> filter) {
        CountDownLatch latch = new CountDownLatch(1);
        OnReceive<T> onReceive;
        final Object[] result = new Object[1];

        this.on(params, onReceive = new OnReceive<>(type) {

            @Override
            public boolean filter(@NonNull T data) {
                return filter.filter(data);
            }

            @Override
            protected void onReceive(@NonNull T data) {
                result[0] = data;
                latch.countDown();
            }
        });

        try {
            latch.await(config.invokeTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            this.removeOn(onReceive);
        }

        return (T) result[0];
    }

    /**
     * 监听消息
     *
     * @param listener 接收监听
     * @param <T>      回调数据泛型
     */
    public <T> void onMain(@NonNull OnReceive<T> listener) {
        listener.setCallMain(true);
        this.on(null, listener);
    }

    /**
     * 监听消息
     *
     * @param params   发送参数
     * @param listener 接收监听
     * @param <T>      回调数据泛型
     */
    public <T> void onMain(Object params, @NonNull OnReceive<T> listener) {
        listener.setCallMain(true);
    }

    /**
     * 监听消息
     *
     * @param listener 接收监听
     * @param <T>      回调数据泛型
     */
    public <T> void on(@NonNull OnReceive<T> listener) {
        this.on(null, listener);
    }

    /**
     * 监听消息
     *
     * @param params   发送参数
     * @param listener 接收监听
     * @param <T>      回调数据泛型
     */
    public <T> void on(Object params, @NonNull OnReceive<T> listener) {
        if (onReceives.contains(listener)) {
            return;
        }
        listener.setCallMain(false);
        onReceives.add(listener);

        if (ObjectUtilKt.isEmpty(params)) {
            return;
        }

        if (JsonKt.isEntity(params.getClass())) {
            this.sendMessage(JsonKt.toJson(params));
        } else {
            this.sendMessage(params.toString());
        }
    }

    /**
     * 监听二进制消息消息
     *
     * @param listener 接收监听
     */
    public void on(@NonNull OnReceiveByte listener) {
        this.on(null, listener);
    }

    /**
     * 监听二进制消息消息
     *
     * @param params   发送参数
     * @param listener 接收监听
     */
    public void on(Object params, @NonNull OnReceiveByte listener) {
        if (onReceiveBytes.contains(listener)) {
            return;
        }
        onReceiveBytes.add(listener);

        if (ObjectUtilKt.isEmpty(params)) {
            return;
        }

        if (JsonKt.isEntity(params.getClass())) {
            this.sendMessage(JsonKt.toJson(params));
        } else {
            this.sendMessage(params.toString());
        }
    }

    /**
     * 移除监听
     */
    public void removeOn(OnReceive<?>... listener) {
        for (OnReceive<?> item : listener) {
            if (item == null) {
                continue;
            }
            onReceives.remove(item);
        }
    }

    /**
     * 移除监听
     */
    public void removeOn(OnReceiveByte... listener) {
        for (OnReceiveByte item : listener) {
            if (item == null) {
                continue;
            }
            onReceiveBytes.remove(item);
        }
    }

    /**
     * 断开连接
     *
     * @param code   关闭码
     * @param reason 关闭原因
     */
    public void disconnect(int code, String reason) {
        config.autoReconnect = false;  // 手动断开时不自动重连
        reconnectTask.cancel(true);
        stopHeartbeat();

        if (webSocket != null) {
            webSocket.close(code, reason);
            webSocket = null;
        }

        this.onReceives.clear();
        this.onReceiveBytes.clear();

        setState(ConnectionState.DISCONNECTED);
        LogUtil.iTag(TAG, "WebSocket已断开");
    }

    /**
     * 优雅断开
     */
    public void disconnect() {
        disconnect(1000, "normal closure");
    }

    /**
     * 判断是否已连接
     */
    public boolean isConnected() {
        return currentState == ConnectionState.CONNECTED;
    }

    /**
     * 获取当前状态
     */
    public ConnectionState getCurrentState() {
        return currentState;
    }

    /**
     * 获取OkHttpClient实例（用于自定义配置）
     */
    public OkHttpClient getClient() {
        return config.okHttpClient;
    }

    /**
     * 创建Builder
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * 设置当前连接状态
     *
     * @param state 当前连接状态
     * @see ConnectionState
     */
    private void setState(ConnectionState state) {
        this.currentState = state;
    }

    /**
     * 启动重连任务
     */
    private void scheduleReconnect() {
        if (currentState != ConnectionState.DISCONNECTED) {
            return;
        }

        setState(ConnectionState.RECONNECTING);

        if (reconnectTask.isDone()) {
            ThreadUtil.executeBySchedule(config.reconnectInterval, TimeUnit.MILLISECONDS, reconnectTask);
        }
    }

    /**
     * 停止心跳任务
     */
    private void stopHeartbeat() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
        }
    }

    /**
     * 处理文本消息
     */
    private void onHandleMessage(String message) {
        if (onReceives.isEmpty()) {
            return;
        }

        for (OnReceive<?> item : onReceives) {
            try {
                item.handleReceive(message);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理二进制消息
     */
    private void onHandleMessage(ByteString bytes) {
        if (onReceiveBytes.isEmpty()) {
            return;
        }
        for (OnReceiveByte listener : onReceiveBytes) {
            try {
                listener.onReceiveByte(bytes);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * WebSocket连接参数配置
     */
    public static class Builder {
        private String url;                                 //连接地址
        private OkHttpClient okHttpClient;
        private Map<String, String> header;                 //发起连接的请求头
        private Map<String, Object> params;                 //发起连接的请求参数
        private WebSocketCallback callback;                 //WebSocket连接回调

        // 重连相关
        private boolean autoReconnect = true;              // 是否自动重连
        private int reconnectInterval = 5000;              // 重连间隔(ms)
        private int maxReconnectCount = -1;                // 最大重连次数(小于0代表不限制)

        // 心跳相关
        private boolean enableHeartbeat = true;             // 是否开启心跳
        private int heartbeatInterval = 30000;              // 心跳间隔(ms)
        private String heartbeatMessage = "ping";           // 心跳消息

        //invoke 相关
        private int invokeTimeout = 10000;                  // invoke调用 超时时间(ms)

        /**
         * 设置连接地址
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置发起连接服务端的header信息
         */
        public Builder setHeaders(Map<String, String> header) {
            this.header = header;
            return this;
        }

        /**
         * 设置发起连接服务端的参数信息
         */
        public Builder setParams(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        /**
         * 设置OkHttpClient
         */
        public Builder setOkHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        /**
         * 设置回调
         */
        public Builder callback(WebSocketCallback callback) {
            this.callback = callback;
            return this;
        }

        /**
         * 设置自动重连
         *
         * @param autoReconnect 是否自动重连
         */
        public Builder autoReconnect(boolean autoReconnect) {
            this.autoReconnect = autoReconnect;
            return this;
        }

        /**
         * 设置重连间隔
         *
         * @param reconnectInterval 重连间隔时间,单位: 毫秒
         */
        public Builder reconnectInterval(int reconnectInterval) {
            this.reconnectInterval = reconnectInterval;
            return this;
        }

        /**
         * 设置最大重连次数
         *
         * @param maxReconnectCount 最大重连次数(小于0代表不限制)
         */
        public Builder maxReconnectCount(int maxReconnectCount) {
            this.maxReconnectCount = maxReconnectCount;
            return this;
        }

        /**
         * 设置心跳相关参数
         *
         * @param enable   是否启用心跳任务
         * @param interval 心跳间隔时间,单位：毫秒
         * @param message  发送的心跳消息内容
         */
        public Builder heartbeatConfig(boolean enable, int interval, String message) {
            this.enableHeartbeat = enable;
            this.heartbeatInterval = interval;
            this.heartbeatMessage = message;
            return this;
        }

        /**
         * 设置invoke调用超时时间
         *
         * @param invokeTimeout invoke调用超时时间,单位: 毫秒
         */
        public Builder invokeTimeout(int invokeTimeout) {
            this.invokeTimeout = invokeTimeout;
            return this;
        }

        public WebSocketClient build() {
            if (okHttpClient == null) {
                okHttpClient = new OkHttpClient.Builder()
                        .pingInterval(20, TimeUnit.SECONDS)  // 设置Ping间隔
                        .connectTimeout(10, TimeUnit.SECONDS)  // 连接超时
                        .readTimeout(10, TimeUnit.SECONDS)      // 读取超时
                        .writeTimeout(10, TimeUnit.SECONDS)    // 写入超时
                        .retryOnConnectionFailure(true)         // 连接失败时重试
                        .build();
            }
            return new WebSocketClient(this);
        }

    }
}
