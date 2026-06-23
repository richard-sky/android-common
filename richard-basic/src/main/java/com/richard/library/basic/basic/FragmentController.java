package com.richard.library.basic.basic;

import android.os.Bundle;

/**
 * @author: Richard
 * @createDate: 2025/11/11 16:39
 * @version: 1.0
 * @description: Fragment控制器
 */
public interface FragmentController {

    /**
     * 跳转到指定Fragment
     */
    void navigateTo(Class<? extends IFragment> fragmentClass, Bundle args);

    /**
     * 跳转到指定Fragment
     */
    void navigateTo(IFragment fragment);

    /**
     * 返回上一个Fragment
     */
    void goBack();

    /**
     * 返回到第一个Fragment
     */
    void goHome();

    /**
     * 获取当前显示的Fragment
     */
    IFragment getCurrentFragment();

}
