package com.richard.library.simplerx.concurrent;

/**
 * <pre>
 * Description : 并发回调
 * Author : admin-richard
 * Date : 2020/3/4 11:47
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020/3/4 11:47     admin-richard         new file.
 * </pre>
 */
@FunctionalInterface
public interface ConcurrentCallback {

    /**
     * 当某个子任务执行完成时回调（由参数决定是否主线程回调）
     *
     * @param task 子任务
     */
    void onSubTaskFinish(ExecuteTask task);

}
