package com.richard.dev.common.mvvm.common;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.richard.library.simplerx.XSubscribe;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author: Richard
 * @createDate: 2023/4/7 14:35
 * @version: 1.0
 * @description: ViewModel 基类
 */
public abstract class BasicViewModel extends ViewModel {

    private final LifecycleOwner lifecycleOwner;

    public BasicViewModel(@NonNull LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    /**
     * 生成默认XSubscribe
     *
     * @param stateLiveData 状态实时数据
     * @return XSubscribe
     */
    protected <T> XSubscribe<T> defaultXSubscribe(MutableLiveData<UICommand<T>> stateLiveData) {
        return this.defaultXSubscribe(stateLiveData, null);
    }

    /**
     * 生成默认XSubscribe
     *
     * @param resultField 处理结果字段
     * @return XSubscribe
     */
    protected <T> XSubscribe<T> defaultXSubscribe(ObservableField<T> resultField) {
        return this.defaultXSubscribe(null, resultField);
    }

    /**
     * 生成默认XSubscribe
     *
     * @param stateLiveData 状态实时数据
     * @param resultField   存储结果字段
     * @return XSubscribe<T>
     */
    protected <T> XSubscribe<T> defaultXSubscribe(MutableLiveData<UICommand<T>> stateLiveData, ObservableField<T> resultField) {
        return new XSubscribe<T>() {
            @Override
            public void onXSubscribe(Disposable d) {
                if (stateLiveData != null) {
                    stateLiveData.postValue(UICommand.create(Command.ON_START));
                }
            }

            @Override
            public void onXNext(T data) {
                if (resultField != null) {
                    resultField.set(data);
                }
                if (stateLiveData != null) {
                    stateLiveData.postValue(UICommand.create(Command.ON_COMPLETE, data));
                }
            }

            @Override
            public void onXError(Throwable e) {
                if (stateLiveData != null) {
                    stateLiveData.postValue(UICommand.create(e));
                }
            }
        };
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }
}

