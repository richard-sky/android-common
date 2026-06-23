package com.richard.library.basic.basic;

import android.os.Bundle;

/**
 * @author: Richard
 * @createDate: 2025/11/11 17:07
 * @version: 1.0
 * @description: Fragment 实现类
 */
public interface IFragment {

    /**
     * 返回键事件
     *
     * @return true表示已处理，false表示交给Activity处理
     */
    boolean onBackPressed();

    /**
     * 跳转到新fragment
     */
    void navigateTo(Class<? extends IFragment> fragmentClass);

    /**
     * 跳转到新fragment
     */
    void navigateTo(Class<? extends IFragment> fragmentClass, Bundle args);

    /**
     * 跳转到新fragment
     */
    void navigateTo(IFragment fragment);

    /**
     * 返回到上一个fragment
     */
    void goBack();

    /**
     * 返回到首页
     */
    void goHome();
}
