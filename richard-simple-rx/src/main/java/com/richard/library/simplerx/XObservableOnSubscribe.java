package com.richard.library.simplerx;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

/**
 * <pre>
 * Description : RxJava Observable 执行体
 * Author : admin-richard
 * Date : 2019-06-21 18:31
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-21 18:31     admin-richard         new file.
 * </pre>
 */
@FunctionalInterface
public interface XObservableOnSubscribe<@NonNull T> extends ObservableOnSubscribe<@NonNull T> {

    /**
     * 执行体
     *
     * @return 返回执行结果
     * @throws Throwable 异常
     */
    T run() throws Throwable;

    @Override
    default void subscribe(@NonNull ObservableEmitter<@NonNull T> emitter) {
        try {
            emitter.onNext(this.run());
            emitter.onComplete();
        } catch (Throwable ex) {
            emitter.onError(ex);
        }
    }
}
