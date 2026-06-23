package com.richard.library.basic.basic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * <pre>
 * Description : 支持ViewDataBinding的BaseDialog
 * Author : admin-richard
 * Date : 2022/4/2 14:30
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/4/2 14:30      admin-richard         new file.
 * </pre>
 */
public abstract class BasicBindingDialog<B extends ViewDataBinding> extends BasicDialog {

    protected B binding;

    public BasicBindingDialog(Context context) {
        super(context);
    }

    public BasicBindingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public final void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        throw new IllegalArgumentException("ViewDataBinding下不能调用该方法设置布局，请调用setContentView(int layoutResID)");
    }

    @Override
    public final void setContentView(@NonNull View view) {
        throw new IllegalArgumentException("ViewDataBinding下不能调用该方法设置布局，请调用setContentView(int layoutResID)");
    }

    @Override
    public void setContentView(int layoutResID) {
        this.binding = DataBindingUtil.inflate(
                getLayoutInflater()
                ,layoutResID
                ,null
                ,false
        );
        this.binding.setLifecycleOwner(this);
        this.binding.executePendingBindings();
        super.setContentView(binding.getRoot());
    }

    @Override
    public void onDetachedFromWindow() {
        if(this.binding != null){
            this.binding.unbind();
        }
        super.onDetachedFromWindow();
    }
}
