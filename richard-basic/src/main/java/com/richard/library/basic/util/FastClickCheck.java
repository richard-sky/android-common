/*
 * Created by ShawnAnn on 16-11-25 上午11:06
 * This is a personal tool library for usual coding,if you have any good idea,welcome pull requests.
 * My email : annshawn518@gamil.com
 * My QQ：1904508978
 * Copyright (c) 2016. All rights reserved.
 */

package com.richard.library.basic.util;

import android.view.View;

/**
 * 快速点击判断
 * Fast click check util
 *
 * @author ShawnAnn
 * @since 2016/4/21
 */
public class FastClickCheck {

    private static long lastClickTime;


    /**
     * 相同视图点击必须间隔0.5s才能有效,使用{@link #check(View, int)}实现
     * If you click a view,your next click will be effective after 0.5s.
     *
     * @param view 目标视图
     * @see {@link #check(View, int)}
     */
    public static void check(View view) {
        check(view, 500);
    }


    /**
     * 设置间隔点击规则，配置间隔点击时长
     * set the time that the next click can be effective
     *
     * @param view  目标视图
     * @param mills 点击间隔时间（毫秒）
     */
    public static void check(final View view, int millisecond) {
        if (view != null && millisecond > 0) {
            view.setClickable(false);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setClickable(true);
                }
            }, millisecond);
        }
    }

    /**
     * 验证是否为快速点击
     * @return true:快速点击,false:慢速点击
     */
    public static boolean check() {
        return check(1000);
    }

    /**
     * 验证是否为快速点击
     * @return true:快速点击,false:慢速点击
     */
    public static boolean check(long millisecond) {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= millisecond) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

}
