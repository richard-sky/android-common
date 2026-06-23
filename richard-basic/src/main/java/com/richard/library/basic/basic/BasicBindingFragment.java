package com.richard.library.basic.basic;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * <pre>
 * Description : 支持ViewDataBinding的BaseFragment
 * Author : admin-richard
 * Date : 2022/4/2 14:53
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/4/2 14:53      admin-richard         new file.
 * </pre>
 */
public abstract class BasicBindingFragment<B extends ViewDataBinding> extends BasicScaffoldFragment {

    protected B binding;

    @Override
    protected final void setContentView(View contentView) {
        throw new IllegalArgumentException("ViewDataBinding下不能调用该方法设置布局，请调用setContentView(int layoutResID)");
    }

    @Override
    protected void setContentView(int layoutId) {
        this.binding = DataBindingUtil.inflate(
                getLayoutInflater()
                , layoutId
                , null
                , false
        );
        this.binding.setLifecycleOwner(getViewLifecycleOwner());
        this.binding.executePendingBindings();
        super.setContentView(this.binding.getRoot());
    }

    @Override
    public void onDestroy() {
        if (this.binding != null) {
            this.binding.unbind();
        }
        super.onDestroy();
    }
}
