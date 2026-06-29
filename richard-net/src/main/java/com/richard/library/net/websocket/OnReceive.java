package com.richard.library.net.websocket;

import androidx.annotation.NonNull;

import com.richard.library.context.util.JsonKt;
import com.richard.library.context.util.UIThread;

import java.lang.reflect.Type;

/**
 * @author: Richard
 * @createDate: 2026/6/2 17:21
 * @version: 1.0
 * @description: 当前接收到消息时的回调
 */
public abstract class OnReceive<T> implements InvokeFilter<T> {

    private final Type type;
    private boolean isCallMain = false;//回调onReceive方法时是否切换至主线程回调(默认)

    public OnReceive() {
        type = JsonKt.getType(this);
    }

    public OnReceive(Type type) {
        this.type = type;
    }

    /**
     * 设置是否切换至主线程回调
     */
    void setCallMain(boolean callMain) {
        isCallMain = callMain;
    }

    /**
     * 处理接收到的消息
     */
    @SuppressWarnings("unchecked")
    void handleReceive(String message) {
        if (message == null) {
            return;
        }

        Class<?> typeClass = JsonKt.getRealClass(type, true);
        if (JsonKt.isEntity(typeClass) && !JsonKt.isJson(message)) {
            return;
        }

        if (JsonKt.isCollectionType(type) && !JsonKt.isJsonArray(message)) {
            return;
        }

        if (type instanceof Class && !JsonKt.isJsonObject(message)) {
            return;
        }

        T data;

        if (JsonKt.isCollectionType(type)) {
            data = (T) JsonKt.toObjectList(message, JsonKt.getRealClass(type, false));
        } else if (JsonKt.isEntity(typeClass)) {
            data = JsonKt.toObject(message, type);
        } else {
            data = (T) message;
        }

        if (data == null) {
            return;
        }

        if (this.filter(data)) {
            if (isCallMain) {
                UIThread.runOnUiThread(() -> this.onReceive(data));
            } else {
                this.onReceive(data);
            }
        }
    }

    /**
     * 筛选出需要接收处理的消息
     */
    @Override
    public boolean filter(@NonNull T data) {
        return true;
    }

    /**
     * 接收并处理消息
     */
    protected abstract void onReceive(@NonNull T data);

}
