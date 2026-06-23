package com.richard.library.context.util;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 * Description : 事件触发频率控制器
 * Author : admin-richard
 * Date : 2018/6/20 11:34
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/6/20 11:34     admin-richard         new file.
 * </pre>
 * <p>
 * 机制：前一次请求尚未执行完成，则仅保留最新一次请求
 * 注意：同一LimitRequester对象提交的所有Runnable视为同一类可合并请求
 *
 * @author miss
 * LimitRequester requester = new LimitRequester(new Handler(Looper.getMainLooper()), 1000);
 * for (int i = 0; i < 1000; i++) {
 * String task = (i + 1) + "A";
 * requester.postLimit(new Runnable() {
 * @Override public void run() {
 * LogUtil.dTag("testtt", "执行名称" + task);
 * }
 * });
 * }
 */
public class LimitRequester {

    /**
     * handler
     */
    private final Handler mHandler;

    /**
     * 自动控制时间执行run
     */
    private final AutoTimeRunnable autoTimeRunnable;

    /**
     * 频率控制间隔时间戳(在间隔该频率时间内不执行用户事件)
     */
    private final long frequencyTime;

    /**
     * 超时执行间隔时间(长时间处于频率控制时间时，超过该时间，则立即执行一次)
     */
    private final long timeOutExecuteTime;

    /**
     * 创建限制刷新的Handler
     * 上个请求尚未执行完，则仅保留最新一次请求
     *
     * @param handler 实际使用的线程句柄 Handler
     */
    public LimitRequester(Handler handler) {
        this(handler, 1000, 0);
    }

    /**
     * 创建间隔刷新的Handler
     * 两次请求提交间隔不能超过interval，否则丢弃仅执行最新一次
     *
     * @param handler       handler
     * @param frequencyTime 频率控制间隔时间戳(在间隔该频率时间内不执行用户事件)
     */
    public LimitRequester(Handler handler, long frequencyTime) {
        this(handler, frequencyTime, 0);
    }

    /**
     * 创建间隔刷新的Handler
     * 两次请求提交间隔不能超过interval，否则丢弃仅执行最新一次
     *
     * @param handler            handler
     * @param frequencyTime      频率控制间隔时间戳(在间隔该频率时间内不执行用户事件)
     * @param timeOutExecuteTime 超时执行间隔时间(长时间处于频率控制时间时，超过该时间，则立即执行一次)
     */
    public LimitRequester(Handler handler, long frequencyTime, long timeOutExecuteTime) {
        if (frequencyTime <= 0) {
            throw new IllegalArgumentException("LimitRequester interval must > 0");
        }

        if (timeOutExecuteTime > 0 && timeOutExecuteTime <= frequencyTime) {
            throw new IllegalArgumentException("LimitRequester timeOutExecuteTime must > frequencyTime");
        }

        mHandler = handler;
        this.frequencyTime = frequencyTime;
        this.timeOutExecuteTime = timeOutExecuteTime;
        autoTimeRunnable = new AutoTimeRunnable();
    }

    /**
     * 设置空闲时是否延迟执行
     */
    public void setIdleDelayExecute(boolean idleDelayExecute) {
        autoTimeRunnable.setIdleDelayExecute(idleDelayExecute);
    }

    /**
     * 设置当前是否可Post 事件
     */
    public void setPost(boolean canPost) {
        autoTimeRunnable.setPost(canPost);
    }

    /**
     * 提交请求，默认为同一类别
     * 若Handler处于非空闲状态，则仅执行最新一次提交
     */
    public void post(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        autoTimeRunnable.post(runnable);
    }

    /**
     * 清除执行事件
     */
    public void clearPost() {
        autoTimeRunnable.clearPost();
    }

    /**
     * 运行任务
     */
    private class AutoTimeRunnable implements Runnable {

        private Runnable postDelayedRunnable;
        private Runnable runnable;
        private long lastExecuteTime = 0L;//最近执行或即将执行事件的时间戳
        private long lastActualExecuteTime = 0L;//最近实际执行事件的时间戳
        private boolean isCanPost = true;//是否可发送事件
        private boolean isIdleDelayExecute = false;//空闲时是否延迟执行

        //标识是否空闲，默认是空闲状态
        private final AtomicBoolean isIdle = new AtomicBoolean(true);

        /**
         * 发送要执行的事件
         */
        public void post(Runnable runnable) {
            this.runnable = runnable;
            if (!isCanPost) {
                return;
            }

            //若空闲，则执行，不空闲，则仅保存最新一次Runnable
            if (isIdle.compareAndSet(true, false)) {
                postDelayedRunnable = null;
                execute(true);
            } else {
                if (postDelayedRunnable != null) {
                    isIdle.set(false);
                    mHandler.removeCallbacks(postDelayedRunnable);
                    postDelayedRunnable = null;
                    execute(false);
                }
            }
        }

        /**
         * 设置空闲时是否延迟执行
         */
        public void setIdleDelayExecute(boolean idleDelayExecute) {
            isIdleDelayExecute = idleDelayExecute;
        }

        /**
         * 设置是否可发送事件
         */
        public void setPost(boolean canPost) {
            isCanPost = canPost;
            //通知执行后一次发送的未执行的事件
            if (isCanPost && runnable != null) {
                post(runnable);
            }
        }

        /**
         * 清除未执行的事件
         */
        public void clearPost() {
            this.runnable = null;
            if (postDelayedRunnable != null) {
                mHandler.removeCallbacks(postDelayedRunnable);
            }
            isIdle.set(true);
        }

        /**
         * 执行最新保存的Runnable
         * 执行完成后，若发现有新请求，则执行
         * 若没有新请求，则置为空闲状态
         */
        @Override
        public void run() {
            Runnable r = runnable;
            if(r != null){
                r.run();
            }
            if (r != runnable) {
                execute(false);
            } else {
                isIdle.set(true);
                runnable = null;
            }
        }

        /**
         * 具体执行逻辑
         * 若无间隔，则立刻执行
         * 若有间隔，两次执行间隔超过要求间隔，立刻执行
         * 两次执行间隔不及要求间隔，则延迟间隔时间后执行
         */
        private void execute(boolean currentIsIdle) {
            if (frequencyTime <= 0) {
                mHandler.post(this);
                return;
            }

            long now = System.currentTimeMillis();
            long interval = now - lastExecuteTime;

            if (interval >= frequencyTime) {
                lastExecuteTime = now;//现执行事件时间戳记录
                lastActualExecuteTime = now;

                if(isIdleDelayExecute && currentIsIdle){
                    postDelayedRunnable = this;
                    mHandler.postDelayed(postDelayedRunnable,frequencyTime);
                }else{
                    mHandler.post(this);
                }
            } else {
                lastExecuteTime = now + frequencyTime;//即将执行事件时间戳记录

                if (timeOutExecuteTime > 0 && now - lastActualExecuteTime >= timeOutExecuteTime) {
                    lastActualExecuteTime = lastExecuteTime;
                    mHandler.post(this);
                } else {
                    postDelayedRunnable = this;
                    mHandler.postDelayed(postDelayedRunnable, frequencyTime);
                }
            }
        }
    }
}