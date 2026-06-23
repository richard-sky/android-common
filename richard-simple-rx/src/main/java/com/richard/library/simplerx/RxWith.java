package com.richard.library.simplerx;

import io.reactivex.rxjava3.core.Observable;

/**
 * <pre>
 * Description : rx 操作方法调用连接者
 * Author : admin-richard
 * Date : 2022/3/17 10:28
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/3/17 10:28     admin-richard         new file.
 * </pre>
 */
@FunctionalInterface
public interface RxWith<T> {

    /**
     * RxJava 方法调用
     */
    Observable<T> with(Observable<T> obs);

}
