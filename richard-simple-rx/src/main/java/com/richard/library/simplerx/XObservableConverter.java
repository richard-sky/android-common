package com.richard.library.simplerx;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * <pre>
 * Description : Observable转换器
 * Author : admin-richard
 * Date : 2020/4/14 12:47
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020/4/14 12:47     admin-richard         new file.
 * </pre>
 */
@FunctionalInterface
public interface XObservableConverter<@NonNull T, @NonNull R> {

    /**
     * 将当前XObservable处理的结果转换为下一个待处理的XObservable
     *
     * @param xObservable 当前处理的XObservable
     * @return 返回下一个需处理的XObservable
     */
    default XObservable<R> converter(XObservable<T> xObservable) {
        return XObservable.create(() -> apply(xObservable.toSyncSubscribe(true)));
    }

    /**
     * 具体业务处理方法
     *
     * @param data 上一个完成处理之后的结果数据
     * @return 返回该次处理的结果数据
     */
    R apply(T data);

}
