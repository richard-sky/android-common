package com.richard.library.context.util;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Richard
 * @createDate: 2025/6/22 15:21
 * @version: 1.0
 * @description: 无
 * <p>
 * 线程工具类
 * FixedThreadPool（固定线程池）‌：
 * ‌特点‌：线程池中的线程数量固定，无论任务多少，都只会创建固定数量的线程来执行任务。核心线程数等于最大线程数，当线程池中的线程数量达到最大值时，新任务会在阻塞队列中等待。
 * ‌适用场景‌：适用于能够控制并发线程数量且任务量较为稳定的场景。
 * <p>
 * CachedThreadPool（缓存线程池）‌：
 * ‌特点‌：根据任务数量动态调整线程数量，空闲线程超过60秒后会被回收。没有核心线程的概念，最大线程数为Integer.MAX_VALUE，实际上可以创建任意多的线程。
 * ‌适用场景‌：适用于任务量不确定且任务较短小的场景，能够动态调整线程数量。
 * <p>
 * SingleThreadPool（单个线程池）‌：
 * ‌特点‌：只有一个线程，所有任务按顺序依次执行。
 * ‌适用场景‌：适用于需要按顺序执行任务的场景，且任务数量有限。
 * <p>
 * ScheduledThreadPool（调度线程池）‌：
 * ‌特点‌：可定时执行或周期性执行任务。核心线程数量固定，非核心线程数量没有限制。
 * ‌适用场景‌：适用于需要定时或周期性执行任务的场景。
 */
public final class ThreadUtil {

    private static final Map<Integer, Map<Integer, ExecutorService>> TYPE_PRIORITY_POOLS = new HashMap<>();
    private static final Map<Task, ExecutorService> TASK_POOL_MAP = new ConcurrentHashMap<>();
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final byte TYPE_SINGLE = -1;
    private static final byte TYPE_CACHED = -2;
    private static final byte TYPE_IO = -4;
    private static final byte TYPE_CPU = -8;
    private static final byte TYPE_SCHEDULE = -16;

    /**
     * 当前线程是否为主线程(android UI线程)
     */
    public static boolean isMainThread() {
        return UIThread.isMainThread();
    }

    /**
     * 获取主线程handler
     */
    public static Handler getMainHandler() {
        return UIThread.getHandler();
    }

    /**
     * 在主线程中执行事件
     */
    public static void runOnUiThread(final Runnable runnable) {
        UIThread.runOnUiThread(runnable);
    }

    /**
     * 在主线程中延时执行事件
     *
     * @param delayMillis 延迟时间(毫秒)
     * @param runnable    事件
     */
    public static void runOnUiThreadDelayed(long delayMillis, final Runnable runnable) {
        UIThread.runOnUiThreadDelayed(delayMillis, runnable);
    }

    /**
     * 获取固定线程池
     * ‌特点‌：线程池中的线程数量固定，无论任务多少，都只会创建固定数量的线程来执行任务。核心线程数等于最大线程数，当线程池中的线程数量达到最大值时，新任务会在阻塞队列中等待。
     * ‌适用场景‌：适用于能够控制并发线程数量且任务量较为稳定的场景。
     *
     * @param size 池中线程的大小.
     */
    public static ExecutorService getFixedPool(@IntRange(from = 1) final int size) {
        return getPoolByTypeAndPriority(size);
    }

    /**
     * 获取固定线程池
     * ‌特点‌：线程池中的线程数量固定，无论任务多少，都只会创建固定数量的线程来执行任务。核心线程数等于最大线程数，当线程池中的线程数量达到最大值时，新任务会在阻塞队列中等待。
     * ‌适用场景‌：适用于能够控制并发线程数量且任务量较为稳定的场景。
     *
     * @param size     池中线程的大小.
     * @param priority 优先级.
     */
    public static ExecutorService getFixedPool(@IntRange(from = 1) final int size,
                                               @IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(size, priority);
    }

    /**
     * 获取单线程池
     * ‌特点‌：只有一个线程，所有任务按顺序依次执行。
     * ‌适用场景‌：适用于需要按顺序执行任务的场景，且任务数量有限。
     */
    public static ExecutorService getSinglePool() {
        return getPoolByTypeAndPriority(TYPE_SINGLE);
    }

    /**
     * 获取单线程池
     * ‌特点‌：只有一个线程，所有任务按顺序依次执行。
     * ‌适用场景‌：适用于需要按顺序执行任务的场景，且任务数量有限。
     *
     * @param priority 优先级
     */
    public static ExecutorService getSinglePool(@IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(TYPE_SINGLE, priority);
    }

    /**
     * 获取缓存线程池
     * ‌特点‌：根据任务数量动态调整线程数量，空闲线程超过60秒后会被回收。没有核心线程的概念，最大线程数为Integer.MAX_VALUE，实际上可以创建任意多的线程。
     * ‌适用场景‌：适用于任务量不确定且任务较短小的场景，能够动态调整线程数量。
     */
    public static ExecutorService getCachedPool() {
        return getPoolByTypeAndPriority(TYPE_CACHED);
    }

    /**
     * 获取缓存线程池
     * ‌特点‌：根据任务数量动态调整线程数量，空闲线程超过60秒后会被回收。没有核心线程的概念，最大线程数为Integer.MAX_VALUE，实际上可以创建任意多的线程。
     * ‌适用场景‌：适用于任务量不确定且任务较短小的场景，能够动态调整线程数量。
     *
     * @param priority 优先级
     */
    public static ExecutorService getCachedPool(@IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(TYPE_CACHED, priority);
    }

    /**
     * 获取 IO 线程池，该线程池创建（2个CPU_COUNT+1）个线程，这些线程在大小为128的队列中运行。
     */
    public static ExecutorService getIoPool() {
        return getPoolByTypeAndPriority(TYPE_IO);
    }

    /**
     * 获取 IO 线程池，该线程池创建（2个CPU_COUNT+1）个线程，这些线程在大小为128的队列中运行。
     *
     * @param priority 优先级
     */
    public static ExecutorService getIoPool(@IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(TYPE_IO, priority);
    }

    /**
     * 获取 CPU 线程池，该线程池创建（CPU_COUNT+1）个线程，这些线程在大小为128且最大线程数等于（2 CPU_COUNT+1）的队列中运行。
     */
    public static ExecutorService getCpuPool() {
        return getPoolByTypeAndPriority(TYPE_CPU);
    }

    /**
     * 获取 CPU 线程池，该线程池创建（CPU_COUNT+1）个线程，这些线程在大小为128且最大线程数等于（2 CPU_COUNT+1）的队列中运行。
     *
     * @param priority 优先级
     */
    public static ExecutorService getCpuPool(@IntRange(from = 1, to = 10) final int priority) {
        return getPoolByTypeAndPriority(TYPE_CPU, priority);
    }

    /**
     * 获取调度类型的线程池
     */
    public static ScheduledExecutorService getSchedulePool() {
        return (ScheduledExecutorService) getPoolByTypeAndPriority(TYPE_SCHEDULE);
    }

    /**
     * 延迟执行任务
     *
     * @param delay 延迟时间
     * @param unit  延迟时间单位
     * @param task  任务
     */
    public static RunTask executeBySchedule(long delay, TimeUnit unit, RunTask task) {
        return executeDelay(getSchedulePool(), delay, unit, task);
    }

    /**
     * 延迟执行任务
     *
     * @param initDelay 首次延迟时间
     * @param delay     后续连续延迟时间
     * @param unit      延迟时间单位
     * @param task      任务
     */
    public static RunTask executeBySchedule(long initDelay, long delay, TimeUnit unit, RunTask task) {
        return executeDelay(getSchedulePool(), initDelay, delay, unit, task);
    }

    /**
     * 在固定线程池中执行给定的任务
     *
     * @param size 固定线程池中线程的大小.
     * @param task 指定执行的任务
     */
    public static RunTask executeByFixed(@IntRange(from = 1) final int size, final RunTask task) {
        return execute(getPoolByTypeAndPriority(size), task);
    }

    /**
     * 在固定线程池中执行给定的任务。
     *
     * @param size     固定线程池中线程的大小。
     * @param priority 轮询中线程的优先级。
     * @param task     要执行的任务。
     */
    public static RunTask executeByFixed(@IntRange(from = 1) final int size,
                                         @IntRange(from = 1, to = 10) final int priority,
                                         final RunTask task) {
        return execute(getPoolByTypeAndPriority(size, priority), task);
    }

    /**
     * 在单个线程池中执行给定的任务。
     *
     * @param task 执行任务
     */
    public static RunTask executeBySingle(final RunTask task) {
        return execute(getPoolByTypeAndPriority(TYPE_SINGLE), task);
    }

    /**
     * 在单个线程池中执行给定的任务
     *
     * @param priority 优先级
     * @param task     执行任务
     */
    public static RunTask executeBySingle(@IntRange(from = 1, to = 10) final int priority, final RunTask task) {
        return execute(getPoolByTypeAndPriority(TYPE_SINGLE, priority), task);
    }

    /**
     * 在缓存的线程池中执行给定的任务。
     *
     * @param task 执行任务
     */
    public static RunTask executeByCached(final RunTask task) {
        return execute(getPoolByTypeAndPriority(TYPE_CACHED), task);
    }

    /**
     * 在缓存的线程池中执行给定的任务
     *
     * @param priority 优先级
     * @param task     执行任务
     */
    public static RunTask executeByCached(@IntRange(from = 1, to = 10) final int priority, final RunTask task) {
        return execute(getPoolByTypeAndPriority(TYPE_CACHED, priority), task);
    }

    /**
     * 在IO线程池中执行给定的任务。
     *
     * @param task 执行任务
     */
    public static RunTask executeByIo(final RunTask task) {
        return execute(getPoolByTypeAndPriority(TYPE_IO), task);
    }

    /**
     * 在IO线程池中执行给定的任务
     *
     * @param priority 优先级
     * @param task     执行任务
     */
    public static RunTask executeByIo(@IntRange(from = 1, to = 10) final int priority, final RunTask task) {
        return execute(getPoolByTypeAndPriority(TYPE_IO, priority), task);
    }

    /**
     * 在cpu线程池中执行给定的任务。
     *
     * @param task 执行任务
     */
    public static RunTask executeByCpu(final RunTask task) {
        return execute(getPoolByTypeAndPriority(TYPE_CPU), task);
    }

    /**
     * 在cpu线程池中执行给定的任务
     *
     * @param priority 优先级
     * @param task     执行任务
     */
    public static RunTask executeByCpu(@IntRange(from = 1, to = 10) final int priority, final RunTask task) {
        return execute(getPoolByTypeAndPriority(TYPE_CPU, priority), task);
    }

    /**
     * 延迟执行任务
     *
     * @param delay 延迟时间
     * @param unit  延迟时间单位
     * @param task  任务
     */
    public static <T> CallableTask<T> executeBySchedule(long delay, TimeUnit unit, CallableTask<T> task) {
        return executeDelay(getSchedulePool(), delay, unit, task);
    }

    /**
     * 在固定线程池中执行给定的任务
     *
     * @param size 固定线程池中线程的大小.
     * @param task 指定执行的任务
     */
    public static <T> CallableTask<T> executeByFixed(@IntRange(from = 1) final int size, final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(size), task);
    }

    /**
     * 在固定线程池中执行给定的任务。
     *
     * @param size     固定线程池中线程的大小。
     * @param priority 轮询中线程的优先级。
     * @param task     要执行的任务。
     */
    public static <T> CallableTask<T> executeByFixed(@IntRange(from = 1) final int size,
                                                     @IntRange(from = 1, to = 10) final int priority,
                                                     final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(size, priority), task);
    }

    /**
     * 在单个线程池中执行给定的任务。
     *
     * @param task 执行任务
     */
    public static <T> CallableTask<T> executeBySingle(final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(TYPE_SINGLE), task);
    }

    /**
     * 在单个线程池中执行给定的任务
     *
     * @param priority 优先级
     * @param task     执行任务
     */
    public static <T> CallableTask<T> executeBySingle(@IntRange(from = 1, to = 10) final int priority, final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(TYPE_SINGLE, priority), task);
    }

    /**
     * 在缓存的线程池中执行给定的任务。
     *
     * @param task 执行任务
     */
    public static <T> CallableTask<T> executeByCached(final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(TYPE_CACHED), task);
    }

    /**
     * 在缓存的线程池中执行给定的任务
     *
     * @param priority 优先级
     * @param task     执行任务
     */
    public static <T> CallableTask<T> executeByCached(@IntRange(from = 1, to = 10) final int priority, final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(TYPE_CACHED, priority), task);
    }

    /**
     * 在IO线程池中执行给定的任务。
     *
     * @param task 执行任务
     */
    public static <T> CallableTask<T> executeByIo(final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(TYPE_IO), task);
    }

    /**
     * 在IO线程池中执行给定的任务
     *
     * @param priority 优先级
     * @param task     执行任务
     */
    public static <T> CallableTask<T> executeByIo(@IntRange(from = 1, to = 10) final int priority, final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(TYPE_IO, priority), task);
    }

    /**
     * 在cpu线程池中执行给定的任务。
     *
     * @param task 执行任务
     */
    public static <T> CallableTask<T> executeByCpu(final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(TYPE_CPU), task);
    }

    /**
     * 在cpu线程池中执行给定的任务
     *
     * @param priority 优先级
     * @param task     执行任务
     */
    public static <T> CallableTask<T> executeByCpu(@IntRange(from = 1, to = 10) final int priority, final CallableTask<T> task) {
        return execute(getPoolByTypeAndPriority(TYPE_CPU, priority), task);
    }

    /**
     * 执行线程事件
     */
    public static RunTask execute(final ExecutorService pool, final RunTask task) {
        synchronized (TASK_POOL_MAP) {
            if (TASK_POOL_MAP.get(task) != null) {
                Log.e("ThreadUtil", "The task is running.");
                return task;
            }
            TASK_POOL_MAP.put(task, pool);
        }
        pool.execute(task);
        return task;
    }

    /**
     * 延迟执行线程事件
     *
     * @param pool  线程池
     * @param delay 延迟执行时间
     * @param unit  延迟执行时间单位
     * @param task  执行事件
     */
    public static RunTask executeDelay(final ScheduledExecutorService pool, long delay, final TimeUnit unit, final RunTask task) {
        synchronized (TASK_POOL_MAP) {
            if (TASK_POOL_MAP.get(task) != null) {
                Log.e("ThreadUtil", "The task is running.");
                return task;
            }
            TASK_POOL_MAP.put(task, pool);
        }

        task.future = pool.schedule(task, delay, unit);
        return task;
    }

    /**
     * 延迟执行线程事件
     *
     * @param pool      线程池
     * @param initDelay 延迟执行时间
     * @param delay     每一次执行终止和下一次执行开始之间的延迟时间
     * @param unit      时间单位
     * @param task      执行事件
     */
    public static RunTask executeDelay(final ScheduledExecutorService pool, long initDelay, long delay, final TimeUnit unit, final RunTask task) {
        synchronized (TASK_POOL_MAP) {
            if (TASK_POOL_MAP.get(task) != null) {
                Log.e("ThreadUtil", "The task is running.");
                return task;
            }
            TASK_POOL_MAP.put(task, pool);
        }

        task.isSchedule = true;
        task.future = pool.scheduleWithFixedDelay(task, initDelay, delay, unit);
        return task;
    }

    /**
     * 执行线程事件
     */
    public static <T> CallableTask<T> execute(final ExecutorService pool, final CallableTask<T> task) {
        synchronized (TASK_POOL_MAP) {
            if (TASK_POOL_MAP.get(task) != null) {
                Log.e("ThreadUtil", "The task is running.");
                return task;
            }
            TASK_POOL_MAP.put(task, pool);
        }
        task.future = pool.submit(task);
        return task;
    }

    /**
     * 延迟执行线程事件
     *
     * @param pool  线程池
     * @param delay 延迟执行时间
     * @param unit  延迟执行时间单位
     * @param task  执行事件
     */
    public static <T> CallableTask<T> executeDelay(final ScheduledExecutorService pool, long delay, final TimeUnit unit, final CallableTask<T> task) {
        synchronized (TASK_POOL_MAP) {
            if (TASK_POOL_MAP.get(task) != null) {
                Log.e("ThreadUtil", "The task is running.");
                return task;
            }
            TASK_POOL_MAP.put(task, pool);
        }

        task.future = pool.schedule(task, delay, unit);
        return task;
    }

    /**
     * 取消制定线程池下全部线程任务
     */
    public static void cancel(ExecutorService executorService) {
        if (executorService instanceof ThreadPoolExecutor4Util) {
            for (Map.Entry<Task, ExecutorService> taskTaskInfoEntry : TASK_POOL_MAP.entrySet()) {
                if (taskTaskInfoEntry.getValue() == executorService) {
                    cancel(taskTaskInfoEntry.getKey());
                }
            }
        } else {
            Log.e("ThreadUtil", "The executorService is not ThreadUtil's pool.");
        }
    }

    /**
     * 取消任务
     *
     * @param task 任务
     */
    public static void cancel(final Task task) {
        if (task == null) return;
        task.cancel();
    }

    /**
     * 批量取消任务
     *
     * @param tasks 任务
     */
    public static void cancel(final Task... tasks) {
        if (tasks == null) return;
        for (Task task : tasks) {
            if (task == null) continue;
            task.cancel();
        }
    }

    /**
     * 批量取消任务
     *
     * @param tasks 任务
     */
    public static void cancel(final List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return;
        for (Task task : tasks) {
            if (task == null) continue;
            task.cancel();
        }
    }


    /**
     * 根据线程池类型获取线程池
     *
     * @param type 线程池类型
     */
    private static ExecutorService getPoolByTypeAndPriority(final int type) {
        return getPoolByTypeAndPriority(type, Thread.NORM_PRIORITY);
    }

    /**
     * 根据线程池类型和优先级获取线程池
     */
    private static ExecutorService getPoolByTypeAndPriority(final int type, final int priority) {
        synchronized (TYPE_PRIORITY_POOLS) {
            ExecutorService pool;
            Map<Integer, ExecutorService> priorityPools = TYPE_PRIORITY_POOLS.get(type);
            if (priorityPools == null) {
                priorityPools = new ConcurrentHashMap<>();
                pool = ThreadPoolExecutor4Util.createPool(type, priority);
                priorityPools.put(priority, pool);
                TYPE_PRIORITY_POOLS.put(type, priorityPools);
            } else {
                pool = priorityPools.get(priority);
                if (pool == null) {
                    pool = ThreadPoolExecutor4Util.createPool(type, priority);
                    priorityPools.put(priority, pool);
                }
            }
            return pool;
        }
    }

    /**
     * 线程池对象
     */
    static final class ThreadPoolExecutor4Util extends ThreadPoolExecutor {

        private static ExecutorService createPool(final int type, final int priority) {
            switch (type) {
                case TYPE_SINGLE:
                    return new ThreadPoolExecutor4Util(1, 1,
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue4Util(),
                            new UtilsThreadFactory("single", priority)
                    );
                case TYPE_CACHED:
                    return new ThreadPoolExecutor4Util(0, 128,
                            60L, TimeUnit.SECONDS,
                            new LinkedBlockingQueue4Util(true),
                            new UtilsThreadFactory("cached", priority)
                    );
                case TYPE_IO:
                    return new ThreadPoolExecutor4Util(2 * CPU_COUNT + 1, 2 * CPU_COUNT + 1,
                            30, TimeUnit.SECONDS,
                            new LinkedBlockingQueue4Util(),
                            new UtilsThreadFactory("io", priority)
                    );
                case TYPE_CPU:
                    return new ThreadPoolExecutor4Util(CPU_COUNT + 1, 2 * CPU_COUNT + 1,
                            30, TimeUnit.SECONDS,
                            new LinkedBlockingQueue4Util(true),
                            new UtilsThreadFactory("cpu", priority)
                    );
                case TYPE_SCHEDULE:
                    return Executors.newScheduledThreadPool(128, new UtilsThreadFactory("schedule", priority));
                default:
                    return new ThreadPoolExecutor4Util(type, type,
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue4Util(),
                            new UtilsThreadFactory("fixed(" + type + ")", priority)
                    );
            }
        }

        private final AtomicInteger mSubmittedCount = new AtomicInteger();

        private final LinkedBlockingQueue4Util mWorkQueue;

        ThreadPoolExecutor4Util(int corePoolSize, int maximumPoolSize,
                                long keepAliveTime, TimeUnit unit,
                                LinkedBlockingQueue4Util workQueue,
                                ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize,
                    keepAliveTime, unit,
                    workQueue,
                    threadFactory
            );
            workQueue.mPool = this;
            mWorkQueue = workQueue;
        }

        private int getSubmittedCount() {
            return mSubmittedCount.get();
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            mSubmittedCount.decrementAndGet();
            super.afterExecute(r, t);
        }

        @Override
        public void execute(@NonNull Runnable command) {
            if (this.isShutdown()) return;
            mSubmittedCount.incrementAndGet();
            try {
                super.execute(command);
            } catch (RejectedExecutionException ignore) {
                Log.e("ThreadUtil", "This will not happen!");
                mWorkQueue.offer(command);
            } catch (Throwable t) {
                mSubmittedCount.decrementAndGet();
            }
        }
    }

    /**
     * 线程队列
     */
    private static final class LinkedBlockingQueue4Util extends LinkedBlockingQueue<Runnable> {

        private volatile ThreadPoolExecutor4Util mPool;

        private int mCapacity = Integer.MAX_VALUE;

        LinkedBlockingQueue4Util() {
            super();
        }

        LinkedBlockingQueue4Util(boolean isAddSubThreadFirstThenAddQueue) {
            super();
            if (isAddSubThreadFirstThenAddQueue) {
                mCapacity = 0;
            }
        }

        LinkedBlockingQueue4Util(int capacity) {
            super();
            mCapacity = capacity;
        }

        @Override
        public boolean offer(@NonNull Runnable runnable) {
            if (mCapacity <= size() &&
                    mPool != null && mPool.getPoolSize() < mPool.getMaximumPoolSize()) {
                // create a non-core thread
                return false;
            }
            return super.offer(runnable);
        }
    }

    /**
     * 线程工厂
     */
    static final class UtilsThreadFactory extends AtomicLong implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        @Serial
        private static final long serialVersionUID = -9209200509960368598L;
        private final String namePrefix;
        private final int priority;
        private final boolean isDaemon;

        UtilsThreadFactory(String prefix, int priority) {
            this(prefix, priority, false);
        }

        UtilsThreadFactory(String prefix, int priority, boolean isDaemon) {
            namePrefix = prefix + "-pool-" +
                    POOL_NUMBER.getAndIncrement() +
                    "-thread-";
            this.priority = priority;
            this.isDaemon = isDaemon;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r, namePrefix + getAndIncrement()) {
                @Override
                public void run() {
                    try {
                        super.run();
                    } catch (Throwable t) {
                        Log.e("ThreadUtil", "Request threw uncaught throwable", t);
                    }
                }
            };
            t.setDaemon(isDaemon);
            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    System.out.println(e);
                }
            });
            t.setPriority(priority);
            return t;
        }
    }

    /**
     * 执行任务抽象
     */
    public abstract static class Task {

        protected volatile boolean isSchedule = false;
        protected volatile Thread thread;
        protected volatile Future<?> future;

        /**
         * 执行是否完成
         */
        public boolean isDone() {
            if (future != null) {
                return future.isDone();
            }
            return true;
        }

        public void cancel() {
            cancel(true);
        }

        public void cancel(boolean mayInterruptIfRunning) {
            try {
                if (future != null) {
                    future.cancel(mayInterruptIfRunning);
                }
                if (thread != null && mayInterruptIfRunning) {
                    thread.interrupt();
                }
            } finally {
                TASK_POOL_MAP.remove(this);
            }
        }
    }

    /**
     * Runnable 任务
     */
    public abstract static class RunTask extends Task implements Runnable {

        final AtomicBoolean isRunEnd = new AtomicBoolean(true);

        @Override
        public final void run() {
            if (!isRunEnd.get()) {
                return;
            }
            try {
                isRunEnd.set(false);
                thread = Thread.currentThread();
                this.runEvent();
            } finally {
                isRunEnd.set(true);
                if (!isSchedule) {
                    TASK_POOL_MAP.remove(this);
                }
            }
        }

        @Override
        public boolean isDone() {
            if (future != null) {
                return future.isDone();
            }
            return isRunEnd.get();
        }

        public abstract void runEvent();
    }

    /**
     * Callable任务
     */
    public abstract static class CallableTask<T> extends Task implements Callable<T> {

        @Override
        public final T call() throws Exception {
            thread = Thread.currentThread();
            try {
                return this.runEvent();
            } finally {
                if (!isSchedule) {
                    TASK_POOL_MAP.remove(this);
                }
            }
        }

        /**
         * 是否已经取消
         */
        public boolean isCancelled() {
            if (future == null) {
                return true;
            }
            return future.isCancelled();
        }

        /**
         * 获取执行结果
         */
        public T getResult() throws ExecutionException, InterruptedException {
            return future != null ? ObjectUtilKt.toT(future.get()) : null;
        }

        /**
         * 获取执行结果
         */
        public T getResult(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
            return future != null ? ObjectUtilKt.toT(future.get(timeout, unit)) : null;
        }

        /**
         * 执行事件
         */
        public abstract T runEvent() throws Exception;
    }
}
