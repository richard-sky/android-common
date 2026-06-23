package com.richard.library.basic.basic;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

/**
 * @author: Richard
 * @createDate: 2025/11/11 15:33
 * @version: 1.0
 * @description: 实现Navigation to fragment功能的Fragment基类
 */
public abstract class BasicNavigationFragment<B extends ViewDataBinding> extends BasicBindingFragment<B> implements IFragment {

    protected FragmentController fragmentController;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentController) {
            fragmentController = (FragmentController) context;
        } else {
            throw new ClassCastException("Host Activity must implement FragmentController");
        }
    }

    /**
     * 处理返回键事件
     *
     * @return true表示已处理，false表示交给Activity处理
     */
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void navigateTo(Class<? extends IFragment> fragmentClass) {
        this.navigateTo(fragmentClass, null);
    }

    @Override
    public void navigateTo(Class<? extends IFragment> fragmentClass, Bundle args) {
        if (fragmentController != null) {
            fragmentController.navigateTo(fragmentClass, args);
        }
    }

    @Override
    public void navigateTo(IFragment fragment) {
        if (fragmentController != null) {
            fragmentController.navigateTo(fragment);
        }
    }

    @Override
    public void goBack() {
        if (fragmentController != null) {
            fragmentController.goBack();
        }
    }

    @Override
    public void goHome() {
        if (fragmentController != null) {
            fragmentController.goHome();
        }
    }
}