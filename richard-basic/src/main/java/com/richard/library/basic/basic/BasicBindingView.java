package com.richard.library.basic.basic;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * <pre>
 * Description : 支持ViewDataBinding的BaseContentView
 * Author : admin-richard
 * Date : 2022/4/2 14:17
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/4/2 14:17      admin-richard         new file.
 * </pre>
 */
public abstract class BasicBindingView<B extends ViewDataBinding> extends BasicView {

    public B binding;

    public BasicBindingView(@NonNull Context context) {
        super(context);
    }

    public BasicBindingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BasicBindingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BasicBindingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public final void setContentView(View view) {
        throw new IllegalArgumentException("ViewDataBinding下不能调用该方法设置布局，请调用setContentView(int layoutResID)");
    }

    @Override
    public void setContentView(int layoutResID) {
        if (isInEditMode()) {
            LayoutInflater.from(getContext()).inflate(layoutResID, this, true);
            return;
        }
        this.binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext())
                , layoutResID
                , this
                , true
        );

        this.binding.setLifecycleOwner(this);
        this.binding.executePendingBindings();
        super.setContentView(binding.getRoot());
    }

    @Override
    protected void onDestroy() {
        if (this.binding != null) {
            this.binding.unbind();
        }
    }
}
