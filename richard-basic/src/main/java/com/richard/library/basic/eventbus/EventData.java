package com.richard.library.basic.eventbus;

import java.io.Serializable;


/**
 * <pre>
 * Description : EventBus 通用传递事件对象
 * Author : admin-richard
 * Date : 2019-05-25 15:32
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-25 15:32      admin-richard         new file.
 * </pre>
 */
public class EventData<T> implements Serializable {

    private String type;//事件类型标识

    private T data;//事件携带数据

    public EventData(String type) {
        this.type = type;
    }

    public EventData(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
