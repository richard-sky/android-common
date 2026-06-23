package com.richard.library.simplerx.concurrent;

/**
 * <pre>
 * Description : 多线程执行任务
 * Author : admin-richard
 * Date : 2020/2/29 10:46
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020/2/29 10:46     admin-richard         new file.
 * </pre>
 */
@SuppressWarnings("all")
public class ExecuteTask {

    /**
     * 选填 任务类型标识
     */
    private final int type;

    /**
     * 选填 任务名称
     */
    private final String name;

    /**
     * 选填 携带的任务数据
     */
    private final Object taskData;

    /**
     * 任务执行成功之后的结果数据
     */
    private Object resultData;

    /**
     * 执行过程中发生异常信息
     */
    private Throwable error;

    /**
     * 任务执行体
     */
    private final TRRunnable runnable;


    public ExecuteTask(TRRunnable runnable) {
        this(0, null, null, runnable);
    }

    public ExecuteTask(String name, TRRunnable runnable) {
        this(0, name, null, runnable);
    }

    public ExecuteTask(Object taskData, TRRunnable runnable) {
        this(0, null, taskData, runnable);
    }

    public ExecuteTask(int type, String name, TRRunnable runnable) {
        this(type, name, null, runnable);
    }

    public ExecuteTask(int type, String name, Object taskData, TRRunnable runnable) {
        this.type = type;
        this.name = name;
        this.taskData = taskData;
        this.runnable = runnable;
    }

    /**
     * 执行任务
     */
    public void invokeRunMethod() throws Exception {
        if (runnable == null) {
            return;
        }
        this.resultData = runnable.run(this.getTaskData());
    }

    /**
     * 获取任务类型标识
     */
    public int getType() {
        return type;
    }

    /**
     * 获取任务名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取携带的任务数据
     */
    public Object getTaskData() {
        return taskData;
    }

    /**
     * 获取该任务执行成功之后的结果数据
     */
    public Object getResultData() {
        return resultData;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    @FunctionalInterface
    public interface TRRunnable<T, R> {
        /**
         * 任务具体执行体
         *
         * @param taskData 实例化ExecuteTask时传入的值，不传则null
         */
        R run(T taskData) throws Exception;
    }
}
