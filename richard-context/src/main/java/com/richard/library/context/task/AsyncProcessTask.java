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
 * description: 异步处理任务抽象类
 */
public abstract class AsyncProcessTask<T> extends ProcessTask<T> {

    //锁
    private final Object locker = new Object();
    //执行结果
    private volatile boolean isSuccess = false;
    //线程等待超时时间(毫秒)
    private long waitTimeout = 0L;

    /**
     * 设置线程等待超时时间(毫秒)
     */
    public void setWaitTimeout(long waitTimeout) {
        this.waitTimeout = waitTimeout;
    }

    /**
     * 进入等待状态
     */
    private void waitRun() throws InterruptedException {
        synchronized (locker) {
            if (waitTimeout <= 0) {
                locker.wait();
            } else {
                locker.wait(waitTimeout);
            }
        }
    }

    /**
     * 通知进入执行状态
     */
    private void notifyRun() {
        synchronized (locker) {
            try {
                locker.notify();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void runEvent() {
        try {
            onTaskStart();

            while (!isDone()) {
                isSuccess = false;
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
                try {
                    ThreadUtil.getCachedPool().execute(() -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                            runEvent(task, runEventResult);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                //等待执行结果返回
                this.waitRun();

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
                            super.sleep();
                    }
                }

                if (super.getTaskMode() == TaskMode.SINGLE && isEmptyQueue()) {
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
     * 执行事件结果回调
     */
    private final RunEventResult runEventResult = new RunEventResult() {
        @Override
        public void onSuccess() {
            isSuccess = true;
            notifyRun();
        }

        @Override
        public void onFail(String error) {
            isSuccess = false;
            notifyRun();
        }
    };

    @Override
    protected final boolean runEvent(T data) throws Throwable {
        return true;
    }

    /**
     * 执行具体事件
     *
     * @param data   数据对象
     * @param result 执行结果回调
     */
    protected abstract void runEvent(T data, RunEventResult result) throws Throwable;

    /**
     * 执行事件结果回调
     */
    protected interface RunEventResult {
        /**
         * 执行成功时回调
         */
        void onSuccess();

        /**
         * 执行失败时回调
         *
         * @param error 错误消息
         */
        void onFail(String error);

    }
}
