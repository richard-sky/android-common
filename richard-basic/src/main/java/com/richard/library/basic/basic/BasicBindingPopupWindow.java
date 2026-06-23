package com.richard.library.basic.basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * <pre>
 * Description : 支持ViewDataBinding的BasePopupWindow
 * Author : admin-richard
 * Date : 2022/4/2 14:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/4/2 14:57      admin-richard         new file.
 * </pre>
 */
public abstract class BasicBindingPopupWindow<B extends ViewDataBinding> extends BasicPopupWindow {

    protected B binding;

    public BasicBindingPopupWindow(Context context) {
        super(context);
    }

    @Override
    public final void setContentView(View contentView) {
        if (contentView == null) {
            return;
        }
        throw new IllegalArgumentException("ViewDataBinding下不能调用该方法设置布局，请调用setContentView(int layoutResID)");
    }

    @Override
    public BasicPopupWindow setContentView(int layoutResId) {
        this.binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext())
                , layoutResId
                , null
                , false
        );
        this.binding.setLifecycleOwner(this);
        this.binding.executePendingBindings();
        super.setContentView(this.binding.getRoot());

        getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if(binding != null){
                        binding.unbind();
                    }
                    getLifecycle().removeObserver(this);
                }
            }
        });

        return this;
    }
}
