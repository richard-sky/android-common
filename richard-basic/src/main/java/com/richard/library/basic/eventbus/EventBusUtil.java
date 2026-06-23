package com.richard.library.basic.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * <pre>
 * Description : EventBus 工具类
 * Author : admin-richard
 * Date : 2020-02-26 11:10
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020-02-26 11:10      admin-richard         new file.
 * </pre>
 */
public final class EventBusUtil {

    /**
     * 发送仅携带事件类型标识的EventBus事件
     *
     * @param type 事件类型标识
     */
    public static void post(String type) {
        EventBus.getDefault().post(new EventData<>(type));
    }


    /**
     * 发送携带事件类型和事件数据的EventBus事件
     *
     * @param type 事件类型标识
     * @param data 事件携带数据
     */
    public static void post(String type, Object data) {
        EventBus.getDefault().post(new EventData<>(type, data));
    }


    /**
     * 发送可跨进程通知的EventBus事件
     *
     * @param type 事件类型标识
     */
    public static void postCrossProcessEvent(String type) {
        CrossProcessReceiver.sendCrossProcessReceiverMessage(new EventData(type));
    }

    /**
     * 发送可跨进程通知的EventBus事件
     *
     * @param type 事件类型标识
     * @param data 事件携带数据
     */
    public static void postCrossProcessEvent(String type, Object data) {
        CrossProcessReceiver.sendCrossProcessReceiverMessage(new EventData<>(type, data));
    }


}
