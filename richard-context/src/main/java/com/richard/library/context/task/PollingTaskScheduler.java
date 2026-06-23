package com.richard.library.context.task;

import android.util.Log;

import com.richard.library.context.util.ThreadUtil;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Description : 轮询任务调度
 * Author : admin-richard
 * Date : 2020/4/11 12:35
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020/4/11 12:35     admin-richard         new file.
 * </pre>
 */
public final class PollingTaskScheduler {

    private final TaskRunnable taskRunnable;

    private PollingTaskScheduler(PollingTaskConfig pollingTaskConfig) {
        this.taskRunnable = new TaskRunnable(pollingTaskConfig);
    }

    /**
     * 启动任务
     *
     * @param pollingRunnable 具体任务执行体
     */
    public void start(PollingRunnable pollingRunnable) {
        this.start(pollingRunnable, 1000);
    }

    /**
     * 启动任务
     *
     * @param pollingRunnable 具体任务执行体
     * @param delay           延迟多少毫秒开始执行
     */
    public void start(PollingRunnable pollingRunnable, long delay) {
        this.taskRunnable.start(pollingRunnable, delay);
    }

    /**
     * 停止任务
     */
    public void stop() {
        this.taskRunnable.cancel();
    }

    /**
     * 任务执行
     */
    private static class TaskRunnable extends ThreadUtil.RunTask {

        private final PollingTaskConfig config;
        private PollingRunnable pollingRunnable;


        private TaskRunnable(PollingTaskConfig pollingTaskConfig) {
            this.config = pollingTaskConfig;
        }

        /**
         * 启动任务
         *
         * @param pollingRunnable 具体任务执行体
         */
        private synchronized void start(PollingRunnable pollingRunnable, long delay) {
            if (this.pollingRunnable == pollingRunnable && !isDone()) {
                return;
            }
            this.cancel();
            this.pollingRunnable = pollingRunnable;
            ThreadUtil.executeBySchedule(delay < 1000 ? 1000 : delay, TimeUnit.MILLISECONDS, this);
        }

        @Override
        public void runEvent() {
            final PollingRunnable currentRun = pollingRunnable;

            //当前总共已经轮询次数
            int currentTotalPollingQuantity = 1;
            try {
                while (!isDone() && currentRun == pollingRunnable) {

                    //验证当前执行次数是否已经超过最大执行次数
                    if (config.maxPollingQuantity > 0
                            && currentTotalPollingQuantity > config.maxPollingQuantity) {
                        currentRun.onTaskTimeout();
                        break;
                    }

                    //执行具体任务体
                    if (!currentRun.run()) {
                        break;
                    }

                    //使线程睡眠指定时间
                    //优先取回调中的指定的睡眠时间
                    long sleepTime = currentRun.getNextIntervalTime(currentTotalPollingQuantity);
                    if (sleepTime <= 0) {
                        sleepTime = config.fastPollingQuantity > 0
                                && currentTotalPollingQuantity <= config.fastPollingQuantity
                                ? config.fastIntervalTime
                                : config.slowIntervalTime;
                    }

                    if (sleepTime > 0) {
                        TimeUnit.MILLISECONDS.sleep(sleepTime);
                    }

                    if (config.maxPollingQuantity > -1 || config.fastPollingQuantity > -1) {
                        currentTotalPollingQuantity++;
                    }
                }
            } catch (Throwable e) {
                Log.e("error", e.toString());
                pollingRunnable.onException(e);
            }
        }
    }

    /**
     * 轮询执行任务体
     */
    public interface PollingRunnable {
        /**
         * 任务执行方法体
         *
         * @return 任务执行标识 true:任务继续执行，false：任务停止执行
         */
        boolean run() throws Throwable;

        /**
         * 执行过程中发生的异常回调
         */
        void onException(Throwable e);

        /**
         * 获取当前该次和下次之间的间隔时间(单位：ms)
         *
         * @param currentQuantity 当前执行的第几次
         * @return ms
         */
        default long getNextIntervalTime(int currentQuantity) {
            return 0;
        }

        /**
         * 任务执行超时（回调该方法之后，该次任务会内部自动停止，调用者不再需要停止）
         */
        default void onTaskTimeout() {
        }
    }

    /**
     * 轮询任务Builder
     */
    public static class Builder {

        private final PollingTaskConfig mPollingTaskConfig = new PollingTaskConfig();

        /**
         * 设置最大轮询执行次数，超过该次数则整个任务停止执行，小于等于0时代表无限制
         *
         * @param maxPollingQuantity 最大轮询执行次数
         */
        public Builder setMaxPollingQuantity(int maxPollingQuantity) {
            mPollingTaskConfig.maxPollingQuantity = maxPollingQuantity;
            return this;
        }

        /**
         * 设置快速阶段轮询间隔时长（毫秒）
         *
         * @param fastIntervalTime 快速阶段轮询间隔时长（毫秒）
         */
        public Builder setFastIntervalTime(long fastIntervalTime) {
            mPollingTaskConfig.fastIntervalTime = fastIntervalTime;
            return this;
        }

        /**
         * 设置慢速阶段轮询间隔时长（毫秒）
         *
         * @param slowIntervalTime 慢速阶段轮询间隔时长（毫秒）
         */
        public Builder setSlowIntervalTime(long slowIntervalTime) {
            mPollingTaskConfig.slowIntervalTime = slowIntervalTime;
            return this;
        }

        /**
         * 设置快速阶段轮询执行次数，小于等于0时代表无效，大于-1为具体次数
         *
         * @param fastPollingQuantity 快速阶段轮询执行次数
         */
        public Builder setFastPollingQuantity(int fastPollingQuantity) {
            mPollingTaskConfig.fastPollingQuantity = fastPollingQuantity;
            return this;
        }

        public PollingTaskScheduler build() {
            if (mPollingTaskConfig.fastPollingQuantity > mPollingTaskConfig.maxPollingQuantity) {
                throw new RuntimeException("FAST_POLLING_QUANTITY不能大于MAX_POLLING_QUANTITY");
            }
            return new PollingTaskScheduler(mPollingTaskConfig);
        }
    }

    /**
     * 轮询任务配置
     */
    private static class PollingTaskConfig {

        /**
         * 最大轮询执行次数，超过该次数则整个任务停止执行，小于等于0时代表无限制
         */
        private int maxPollingQuantity = -1;

        /**
         * 快速阶段轮询间隔时长（毫秒）
         */
        private long fastIntervalTime = 1000;

        /**
         * 慢速阶段轮询间隔时长（毫秒）
         */
        private long slowIntervalTime = 3000;

        /**
         * 快速阶段轮询执行次数，小于等于0时代表无效，大于-1为具体次数
         */
        private int fastPollingQuantity = -1;
    }
}
