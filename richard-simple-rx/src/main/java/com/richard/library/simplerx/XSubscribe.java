package com.richard.library.simplerx;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * <pre>
 * Description : Rxjava 观察者
 * Author : admin-richard
 * Date : 2019-06-21 17:40
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-21 17:40     admin-richard         new file.
 * </pre>
 */
public abstract class XSubscribe<T> implements Observer<T> {

    private Disposable disposable;

    @Override
    public final void onSubscribe(Disposable d) {
        this.disposable = d;
        this.onXSubscribe(d);
    }

    @Override
    public final void onNext(T data) {
        try {
            this.onXNext(data);
        } catch (Throwable e) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            disposable = null;
            throw e;
        } finally {
            this.onEnd();
        }
    }

    @Override
    public final void onError(Throwable e) {
        e.printStackTrace();
        try {
            this.onXError(e);
        } finally {
            this.onEnd();
        }
    }

    @Override
    public final void onComplete() {
        this.onXComplete();
    }

    /**
     * 当开始执行时回调
     */
    public void onXSubscribe(Disposable d) {

    }

    /**
     * 当执行成功时回调
     *
     * @param data 回调数据
     */
    public abstract void onXNext(T data);

    /**
     * 当执行过程中发生错误时回调
     *
     * @param e 异常信息
     */
    public void onXError(Throwable e) {

    }

    /**
     * 当执行成功完成时回调
     */
    public void onXComplete() {

    }

    /**
     * 当执行结束时调用，不管是执行成功或者失败都会调用,并且是在onNext或者onError之后调用
     */
    public void onEnd() {

    }
}
