package com.richard.library.basic.basic;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * <pre>
 * Description : 支持ViewDataBinding Activity基类
 * Author : admin-richard
 * Date : 2022/4/1 17:44
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/4/1 17:44      admin-richard         new file.
 * </pre>
 */
public abstract class BasicBindingActivity<B extends ViewDataBinding> extends BasicScaffoldActivity {

    protected B binding;

    @Override
    public final void setContentView(View contentView) {
        throw new IllegalArgumentException("ViewDataBinding下不能调用该方法设置布局，请调用setContentView(int layoutResID)");
    }

    @Override
    public void setContentView(int layoutResID) {
        this.binding = DataBindingUtil.bind(getLayoutInflater().inflate(layoutResID, null));
        if (this.binding != null) {
            this.binding.setLifecycleOwner(this);
            this.binding.executePendingBindings();
            super.setContentView(this.binding.getRoot());
        }
    }

    @Override
    protected void onDestroy() {
        if(this.binding != null){
            this.binding.unbind();
        }
        super.onDestroy();
    }
}
