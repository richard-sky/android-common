package com.richard.library.context.util;

import android.os.Handler;
import android.os.Looper;

/**
 * <pre>
 * Description : UI线程操作
 * Author : admin-richard
 * Date : 2021-06-24 11:24
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021-06-24 11:24     admin-richard         new file.
 * </pre>
 */
public final class UIThread {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private UIThread() {
    }


    /**
     * 获取handler
     */
    public static Handler getHandler() {
        return HANDLER;
    }

    /**
     * 是否属于主线程
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 在UI线程执行体
     *
     * @param runnable 执行体
     */
    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }

    /**
     * 延迟在UI线程执行
     *
     * @param delayMillis 延迟时间（毫秒）
     * @param runnable    执行体
     */
    public static void runOnUiThreadDelayed(long delayMillis, final Runnable runnable) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * 移除回调
     */
    public static void removeCallbacks(Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }
}
