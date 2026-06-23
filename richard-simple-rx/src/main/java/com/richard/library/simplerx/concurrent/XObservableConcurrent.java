package com.richard.library.simplerx.concurrent;

import android.text.TextUtils;

import com.richard.library.simplerx.XObservable;
import com.richard.library.simplerx.XObservableOnSubscribe;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * <pre>
 * Description : 并发执行观察者
 * Author : admin-richard
 * Date : 2020/3/4 13:48
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020/3/4 13:48     admin-richard         new file.
 * </pre>
 */
public final class XObservableConcurrent {

    private final XObservableOnSubscribe<List<ExecuteTask>> xObservableOnSubscribe;
    private int maxConcurrency = Integer.MAX_VALUE;

    //当有异常时处理策略
    private final int STRATEGY_IGNORE = 0;//忽略异常继续执行
    private final int STRATEGY_DIRECT_TERMINATE = 1;//发生异常立即终止执行并抛出异常
    private final int STRATEGY_FINISH_ERROR = 2;//全部任务执行完成后，最后统一抛出异常


    private XObservableConcurrent(XObservableOnSubscribe<List<ExecuteTask>> xObservableOnSubscribe) {
        this.xObservableOnSubscribe = xObservableOnSubscribe;
    }

    public static <T> XObservableConcurrent create(XObservableOnSubscribe<List<ExecuteTask>> xObservableOnSubscribe) {
        return new XObservableConcurrent(xObservableOnSubscribe);
    }

    /**
     * 设置同时最大并发线程数
     */
    public XObservableConcurrent maxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
        return this;
    }

    /**
     * 多线程并发执行任务(若执行任务过程中发生了异常会立即终止执行并抛出异常)
     */
    public XObservable<List<ExecuteTask>> execute() {
        return this.execute(false, STRATEGY_DIRECT_TERMINATE, null);
    }

    /**
     * 多线程并发执行任务(若执行任务过程中发生了异常会立即终止执行并抛出异常)
     * 处理线程：子(异步)线程
     * 回调线程：由isSubTaskCallToUIThread参数决定
     *
     * @param isSubTaskCallToUIThread 必填 子级任务执行完成之后是否回调至主线程，true:回调处为主线程、false:回调处线程由该方法调用处线程决定
     * @param callback                选填 当某子任务执行完成时回调
     */
    public XObservable<List<ExecuteTask>> execute(boolean isSubTaskCallToUIThread, ConcurrentCallback callback) {
        return this.execute(isSubTaskCallToUIThread, STRATEGY_DIRECT_TERMINATE, callback);
    }

    /**
     * 多线程并发执行任务(若执行单个任务过程中发生了异常则等到全部任务执行完成后统一抛出异常)
     */
    public XObservable<List<ExecuteTask>> executeAndFinishError() {
        return this.execute(false, STRATEGY_FINISH_ERROR, null);
    }

    /**
     * 多线程并发执行任务(若执行单个任务过程中发生了异常则等到全部任务执行完成后统一抛出异常)
     * 处理线程：子(异步)线程
     * 回调线程：由isSubTaskCallToUIThread参数决定
     *
     * @param isSubTaskCallToUIThread 必填 子级任务执行完成之后是否回调至主线程，true:回调处为主线程、false:回调处线程由该方法调用处线程决定
     * @param callback                选填 当某子任务执行完成时回调
     */
    public XObservable<List<ExecuteTask>> executeAndFinishError(boolean isSubTaskCallToUIThread, ConcurrentCallback callback) {
        return this.execute(isSubTaskCallToUIThread, STRATEGY_FINISH_ERROR, callback);
    }

    /**
     * 多线程并发执行任务(忽略异常继续执行)
     */
    public XObservable<List<ExecuteTask>> executeAndIgnoreError() {
        return this.execute(false, STRATEGY_IGNORE, null);
    }

    /**
     * 多线程并发执行任务(忽略异常继续执行)
     * 处理线程：子(异步)线程
     * 回调线程：由isSubTaskCallToUIThread参数决定
     *
     * @param isSubTaskCallToUIThread 必填 子级任务执行完成之后是否回调至主线程，true:回调处为主线程、false:回调处线程由该方法调用处线程决定
     * @param callback                选填 当某子任务执行完成时回调
     */
    public XObservable<List<ExecuteTask>> executeAndIgnoreError(boolean isSubTaskCallToUIThread, ConcurrentCallback callback) {
        return this.execute(isSubTaskCallToUIThread, STRATEGY_IGNORE, callback);
    }


    /**
     * 多线程并发执行任务
     * 处理线程：子(异步)线程
     * 回调线程：由isSubTaskCallToUIThread参数决定
     *
     * @param isSubTaskCallToUIThread 必填 子级任务执行完成之后是否回调至主线程，true:回调处为主线程、false:回调处线程由该方法调用处线程决定
     * @param errorHandleStrategy     必填 发生异常时内部处理策略
     * @param callback                选填 当某子任务执行完成时回调
     */
    private XObservable<List<ExecuteTask>> execute(boolean isSubTaskCallToUIThread, int errorHandleStrategy, ConcurrentCallback callback) {
        return XObservable.create(() -> {
            Observable<ExecuteTask> innerObservable = Observable
                    .fromIterable(xObservableOnSubscribe.run())
                    .flatMap(task -> Observable
                            .just(task)
                            .observeOn(Schedulers.io())
                            .map(executeTask -> {
                                try {
                                    executeTask.setError(null);
                                    executeTask.invokeRunMethod();
                                } catch (Throwable e) {
                                    if (errorHandleStrategy == STRATEGY_DIRECT_TERMINATE) {
                                        throw e;
                                    } else {
                                        e.printStackTrace();
                                    }
                                    executeTask.setError(e);
                                }
                                return executeTask;
                            }), maxConcurrency);

            if (isSubTaskCallToUIThread) {
                innerObservable = innerObservable.observeOn(AndroidSchedulers.mainThread());
            }

            List<ExecuteTask> data = innerObservable
                    .map(executeTask -> {
                        if (callback != null) {
                            callback.onSubTaskFinish(executeTask);
                        }
                        return executeTask;
                    })
                    .toList()
                    .blockingGet();

            if (errorHandleStrategy == STRATEGY_FINISH_ERROR) {
                StringBuilder errorBuilder = new StringBuilder();
                for (int i = 0, size = data.size(); i < size; i++) {
                    ExecuteTask task = data.get(i);
                    if (task.getError() == null) {
                        continue;
                    }

                    String message = task.getError().getMessage();
                    if (TextUtils.isEmpty(message)) {
                        message = "未知错误: " + task.getError().toString();
                    }

                    if (TextUtils.isEmpty(task.getName())) {
                        errorBuilder.append(message);
                    } else {
                        errorBuilder.append(String.format("%s: %s", task.getName(), message));
                    }

                    if (i <= size - 1) {
                        errorBuilder.append("\n");
                    }
                }
                if (errorBuilder.length() > 0) {
                    throw new RuntimeException(errorBuilder.toString());
                }
            }

            return data;
        });
    }

}
