package com.richard.library.context.task;

import android.util.Log;

import com.richard.library.context.util.ObjectUtilKt;
import com.richard.library.context.util.ThreadUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author Richard
 * date 2020/10/31 11:09
 * version V1.0
 * description: 处理任务抽象类
 */
public abstract class ProcessTask<T> extends ThreadUtil.RunTask {

    //任务队列
    private final TaskQueue<T> taskQueue;
    //任务执行失败时重试间隔时间，单位：毫秒
    private long retryIntervalTime = 5000;
    //任务执行模式(默认为)
    private TaskMode taskMode = TaskMode.SINGLE;

    public ProcessTask() {
        taskQueue = new TaskQueue<>();
    }

    public ProcessTask(int taskQueueMaxSize) {
        taskQueue = new TaskQueue<>(taskQueueMaxSize);
    }

    /**
     * 任务执行模式
     */
    public enum TaskMode {
        CONTINUITY,//连续执行(直到获取getTaskDataList的任务数据列表为空时停止该任务线程)
        SINGLE//单次执行(该次任务只获取一次任务数据列表getTaskDataList，直到这次获取到的数据列表中数据全部处理结束为止，并停止该任务线程)
    }

    /**
     * 执行失败的任务数据处理策略
     */
    public enum FailDataStrategy {
        NONE//间隔指定时间后，继续重新执行该次数据处理任务
        , REQUEUE//将失败的任务重新放到队列尾部等待下次执行
        , ABANDON_THIS//该次抛弃当前失败的任务
        , ABANDON_FOREVER//永久抛弃当前失败的任务
    }

    /**
     * 同步尝试启动任务
     */
    public synchronized void syncTryStart(){
        if (!isCanStartTask()) {
            return;
        }

        if (this.isRunning()) {
            return;
        }

        this.run();
    }

    /**
     * 尝试启动任务
     */
    public synchronized void tryStart() {
        if (!isCanStartTask()) {
            return;
        }

        if (this.isRunning()) {
            return;
        }

        ThreadUtil.executeBySchedule(1000, TimeUnit.MILLISECONDS, this);
    }

    /**
     * 尝试启动任务
     *
     * @param delay    延迟执行的时间
     * @param timeUnit 时间单位
     */
    public synchronized void tryStart(long delay, TimeUnit timeUnit) {
        if (!isCanStartTask()) {
            return;
        }

        if (this.isRunning()) {
            return;
        }

        ThreadUtil.executeBySchedule(delay, timeUnit, this);
    }


    /**
     * 尝试启动任务
     *
     * @param initialDelay 首次执行的延迟时间
     * @param delay        每一次执行终止和下一次执行开始之间的延迟
     * @param timeUnit     时间单位
     */
    public synchronized void tryStart(long initialDelay, long delay, TimeUnit timeUnit) {
        if (!isCanStartTask()) {
            return;
        }

        if (this.isRunning()) {
            return;
        }

        ThreadUtil.executeBySchedule(initialDelay, delay, timeUnit,this);
    }

    /**
     * 获取当前任务执行模式
     */
    public TaskMode getTaskMode() {
        return taskMode;
    }

    /**
     * 当前任务是否处于执行中
     */
    public boolean isRunning() {
        return !isDone();
    }

    /**
     * 设置任务执行模式
     */
    public void setTaskMode(TaskMode taskMode) {
        this.taskMode = taskMode;
    }

    /**
     * 设置任务执行失败时重试间隔时间，单位：毫秒
     */
    public void setRetryIntervalTime(long retryIntervalTime) {
        this.retryIntervalTime = retryIntervalTime;
    }

    /**
     * 将任务列入队列
     */
    public void joinOne(T data) {
        if (ObjectUtilKt.isEmpty(data)) {
            return;
        }
        taskQueue.add(data);
    }

    /**
     * 将任务列入队列
     */
    public void joinMulti(List<T> data) {
        if (ObjectUtilKt.isEmpty(data)) {
            return;
        }
        taskQueue.addAll(data);
    }

    /**
     * 获取下一个任务
     */
    protected T peekNextTask() {
        return taskQueue.peek();
    }

    /**
     * 队列是否为空
     */
    protected boolean isEmptyQueue() {
        return taskQueue.isEmpty();
    }

    /**
     * 清空任务队列
     */
    protected void clearTaskQueue() {
        taskQueue.clear();
    }

    /**
     * 移除任务队列
     */
    protected void removeTaskQueue(T task) {
        taskQueue.remove(task);
    }

    /**
     * 任务数据重新入队列尾部，等待下次执行
     */
    protected void requeue(T task) {
        taskQueue.add(task);
    }

    @Override
    public void runEvent() {
        try {
            onTaskStart();

            while (!isDone()) {
                if (isEmptyQueue()) {
                    List<T> dataList = this.getTaskDataList();
                    if (ObjectUtilKt.isNotEmpty(dataList)) {
                        this.joinMulti(dataList);
                    }
                }
                if (isEmptyQueue()) {
                    break;
                }

                T task = peekNextTask();
                boolean isSuccess = false;
                try {
                    isSuccess = runEvent(task);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                //处理成功
                if (isSuccess) {
                    this.removeTaskQueue(task);
                    this.removeTaskData(task);
                } else {//处理失败
                    FailDataStrategy strategy = ObjectUtilKt.getOrDefault(
                            getFailDataStrategy(task), FailDataStrategy.NONE);
                    switch (strategy) {
                        case REQUEUE:
                            this.removeTaskQueue(task);
                            this.requeue(task);
                            break;
                        case ABANDON_THIS:
                            this.removeTaskQueue(task);
                            break;
                        case ABANDON_FOREVER:
                            this.removeTaskQueue(task);
                            this.removeTaskData(task);
                            break;
                        case NONE:
                        default:
                            this.sleep();
                    }
                }

                if (taskMode == TaskMode.SINGLE && isEmptyQueue()) {
                    break;
                }
            }
            onTaskEnd();
        } catch (Throwable e) {
            this.clearTaskQueue();
            Log.e("ProcessTask", e.toString());
        }
    }

    /**
     * 停止任务
     */
    public synchronized void stop() {
        cancel(true);
    }

    /**
     * 睡眠
     */
    protected void sleep() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(retryIntervalTime);
    }

    /**
     * 是否可以启动任务的前置条件的验证(用于判断启动任务时的前置条件，比如：必须有网络才能启动任务)
     */
    protected boolean isCanStartTask() {
        return true;
    }

    /**
     * 当任务开始执行时回调
     */
    protected void onTaskStart() {

    }

    /**
     * 当前任务结束时回调
     */
    protected void onTaskEnd() {

    }

    /**
     * 判断任务在队列中是否已存在
     */
    public final boolean isExists(Object task) {
        if (taskQueue.isEmpty()) {
            return false;
        }

        for (T item : taskQueue) {
            if (validateExists(item, task)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断任务在队列中是否已存在
     */
    protected boolean validateExists(T task1, Object task2) {
        return task1 == task2;
    }

    /**
     * 获取执行失败之后的任务数据处理策略
     *
     * @param data 当前失败任务数据
     */
    protected FailDataStrategy getFailDataStrategy(T data) {
        return FailDataStrategy.NONE;
    }

    /**
     * 获取需要处理的任务数据列表
     */
    protected abstract List<T> getTaskDataList();

    /**
     * 执行具体事件
     *
     * @param data 数据对象
     * @return 执行是否成功
     */
    protected abstract boolean runEvent(T data) throws Throwable;

    /***
     * 任务执行成功后移除持久化的同步任务数据
     * @param data 任务数据
     */
    protected abstract void removeTaskData(T data);
}
