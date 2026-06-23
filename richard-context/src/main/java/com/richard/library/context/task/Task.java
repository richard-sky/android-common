package com.richard.library.context.task;

import androidx.annotation.NonNull;

import com.richard.library.context.util.ThreadUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author: Richard
 * @createDate: 2025/4/30 10:59
 * @version: 1.0
 * @description: 任务信息
 */
public class Task {

    /**
     * 任务唯一标识
     */
    private String id;

    /**
     * 任务执行事件体
     */
    private Runnable event;

    //----------------------------------------------------------------------------------------------
    /**执行时间 可取值：HH:mm:ss 、 mm:ss 、 ss*/
    String cronTime;

    /**
     * 连续执行之间的间隔时间
     */
    long period;

    /**
     * 连续执行间隔时间的单位
     */
    TimeUnit periodUnit;

    /**
     * 任务提交后的Future
     */
    ThreadUtil.RunTask task;

    public Task(Runnable event) {
        this.id = event.getClass().getName();
        this.event = event;
    }

    public Task(@NonNull String id, @NonNull Runnable event) {
        this.id = id;
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Runnable getEvent() {
        return event;
    }

    public void setEvent(Runnable event) {
        this.event = event;
    }
}
