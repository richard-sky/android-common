package com.richard.library.context.task;

import com.richard.library.context.util.ObjectUtilKt;
import com.richard.library.context.util.ThreadUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author: Richard
 * @createDate: 2024/1/30 15:43
 * @version: 1.0
 * @description: 定时任务
 */
public final class TimerTask {

    /**
     * 任务管理Map,key:任务id，value：任务信息
     */
    private final ConcurrentMap<String, Task> taskManager = new ConcurrentHashMap<>();

    /**
     * 执行任务(每到指定的cronTime时间时执行一次事件)
     *
     * @param cronTime 执行时间 可取值：HH:mm:ss 、 mm:ss 、 ss
     * @param task     任务
     */
    public synchronized void add(String cronTime, Task task) {
        Task value = taskManager.get(task.getId());
        if (value != null) {
            return;
        }
        task.cronTime = cronTime;
        taskManager.put(task.getId(), task);
    }

    /**
     * 实例化每满整指定时间后根据everyTime间隔执行的一次定时事件(例:值为5的时候、并且时间单位为分，则执行时间为：10:00、10：05、10：10、10：15...)
     *
     * @param period     指定的满足的时间
     * @param periodUnit 时间单位
     * @param task       任务
     */
    public synchronized void add(int period, TimeUnit periodUnit, Task task) {
        Task value = taskManager.get(task.getId());
        if (value != null) {
            return;
        }

        task.period = period;
        task.periodUnit = periodUnit;
        taskManager.put(task.getId(), task);
    }

    /**
     * 启动全部定时任务
     */
    public synchronized void start() {
        Set<Map.Entry<String, Task>> entrySet = taskManager.entrySet();
        for (Map.Entry<String, Task> item : entrySet) {
            this.runEvent(item.getValue());
        }
    }

    /**
     * 启动指定任务id的定时任务
     */
    public synchronized void start(String id) {
        Task task = taskManager.get(id);
        if (task == null) {
            return;
        }
        this.runEvent(task);
    }

    /**
     * 停止全部定时任务
     */
    public void stop() {
        Set<Map.Entry<String, Task>> entrySet = taskManager.entrySet();
        for (Map.Entry<String, Task> item : entrySet) {
            this.stop(item.getValue().getId());
        }
    }

    /**
     * 停止指定任务
     */
    public void stop(String id) {
        Task task = taskManager.get(id);
        if (task == null || task.task == null) {
            return;
        }
        if (task.task.isDone()) {
            return;
        }
        task.task.cancel(true);
    }

    /**
     * 根据任务id标识验证任务是否已存在
     */
    public boolean isExists(String id) {
        return taskManager.containsKey(id);
    }

    /**
     * 验证指定id的任务是否处于运行中
     */
    public boolean isRunning(String id) {
        Task task = taskManager.get(id);
        if (task == null || task.task == null) {
            return false;
        }
        return !task.task.isDone();
    }

    /**
     * 启动执行任务
     */
    private void runEvent(Task task) {
        if (task.task != null && !task.task.isDone()) {
            return;
        }

        long initialDelay;
        long periodSecond;
        if (ObjectUtilKt.isNotEmpty(task.cronTime)) {
            long[] result = TaskTimeUtil.parseCron(task.cronTime);
            initialDelay = result[0];
            periodSecond = result[1];
        } else {
            initialDelay = TaskTimeUtil.getCronEveryDelaySecond(task.period, task.periodUnit);
            switch (task.periodUnit) {
                case SECONDS:
                    periodSecond = task.period;
                    break;
                case MINUTES:
                    periodSecond = task.period * 60L;
                    break;
                case HOURS:
                    periodSecond = task.period * 60L * 60L;
                    break;
                default:
                    throw new RuntimeException("只支持TimeUnit为SECONDS、MINUTES、HOURS的值");
            }
        }

        if (task.getEvent() instanceof ProcessTask<?> processTask) {
            processTask.tryStart(initialDelay, periodSecond, TimeUnit.SECONDS);
            task.task = processTask;
        } else {
            task.task = new ThreadUtil.RunTask() {
                @Override
                public void runEvent() {
                    task.getEvent().run();
                }
            };
            ThreadUtil.executeBySchedule(initialDelay, periodSecond, TimeUnit.SECONDS, task.task);
        }
    }
}
