package com.richard.library.context.immersionbar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.richard.library.context.AppContext;

/**
 * @author: Richard
 * @createDate: 2026/5/11 17:49
 * @version: 1.0
 * @description: 系统导航栏和状态栏工具类
 * 注意: APP 内的Theme的windowFullscreen属性需设置为false才能完全有效
 */
public final class SystemBarUtil {

    private static BarHide barHide = BarHide.FLAG_SHOW_BAR;
    private static Integer statusBarColor;
    private static Integer navigationBarColor;

    /**
     * 是否隐藏状态栏
     */
    public static boolean isStatusBarHidden(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets insets = window.getDecorView().getRootWindowInsets();
            return !insets.isVisible(WindowInsets.Type.statusBars());
        } else {
            int uiFlags = window.getDecorView().getSystemUiVisibility();
            return (uiFlags & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0;
        }
    }

    /**
     * 是否隐藏导航栏
     */
    public static boolean isNavigationBarHidden(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets insets = window.getDecorView().getRootWindowInsets();
            return !insets.isVisible(WindowInsets.Type.navigationBars());
        } else {
            int uiFlags = window.getDecorView().getSystemUiVisibility();
            return (uiFlags & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0;
        }
    }

    /**
     * 是否存在状态栏或导航栏
     */
    public static boolean isHideBar(Object uiObject) {
        ImmersionBar bar = startWithBar(uiObject);

        if (bar == null) {
            return barHide != BarHide.FLAG_SHOW_BAR;
        }

        return bar.isHideBar();
    }

    /**
     * 获取当前全局设置的BarHide
     */
    public static BarHide getBarHide(Object uiObject) {
        ImmersionBar bar = startWithBar(uiObject);
        if (bar == null) {
            return barHide;
        }
        return bar.getBarParams().barHide;
    }

    /**
     * 设置隐藏或显示系统状态栏和导航栏方式
     */
    public static void hideBar(@NonNull BarHide barHide) {
        SystemBarUtil.barHide = barHide;
    }

    /**
     * 设置状态栏和导航栏背景颜色
     */
    public static void barColor(@ColorInt int barColor) {
        SystemBarUtil.statusBarColor = barColor;
        SystemBarUtil.navigationBarColor = barColor;
    }

    /**
     * 设置状态栏颜色
     */
    public static void setStatusBarColor(@ColorInt int statusBarColor) {
        SystemBarUtil.statusBarColor = statusBarColor;
    }

    /**
     * 设置导航栏颜色
     */
    public static void setNavigationBarColor(@ColorInt int navigationBarColor) {
        SystemBarUtil.navigationBarColor = navigationBarColor;
    }


    /**
     * 初始化并获取系统状态栏和导航栏控制
     */
    public static ImmersionBar withBar(Activity activity) {
        return SystemBarUtil.startWithBar(activity);
    }

    /**
     * 初始化并获取系统状态栏和导航栏控制
     */
    public static ImmersionBar withBar(Fragment fragment) {
        return SystemBarUtil.startWithBar(fragment);
    }

    /**
     * 初始化并获取系统状态栏和导航栏控制
     */
    public static ImmersionBar withBar(android.app.Fragment fragment) {
        return SystemBarUtil.startWithBar(fragment);
    }

    /**
     * 初始化并获取系统状态栏和导航栏控制
     */
    public static ImmersionBar withBar(DialogFragment dialogFragment) {
        return SystemBarUtil.startWithBar(dialogFragment);
    }

    /**
     * 初始化并获取系统状态栏和导航栏控制
     */
    public static ImmersionBar withBar(android.app.DialogFragment dialogFragment) {
        return SystemBarUtil.startWithBar(dialogFragment);
    }

    /**
     * 初始化并获取系统状态栏和导航栏控制
     *
     * @param uiObject ui宿主对象
     */
    private static ImmersionBar startWithBar(Object uiObject) {
        ImmersionBar bar = null;

        if (uiObject instanceof Activity activity) {
            bar = ImmersionBar.with(activity);
        } else if (uiObject instanceof DialogFragment dialogFragment) {
            if (dialogFragment.getDialog() != null) {
                bar = ImmersionBar.with(dialogFragment);
            } else if (dialogFragment.getActivity() != null) {
                bar = ImmersionBar.with(dialogFragment.getActivity());
            }
        } else if (uiObject instanceof android.app.DialogFragment dialogFragment) {
            if (dialogFragment.getDialog() != null) {
                bar = ImmersionBar.with(dialogFragment);
            } else if (dialogFragment.getActivity() != null) {
                bar = ImmersionBar.with(dialogFragment.getActivity());
            }
        } else if (uiObject instanceof Fragment fragment) {
            bar = ImmersionBar.with(fragment);
        } else if (uiObject instanceof android.app.Fragment fragment) {
            bar = ImmersionBar.with(fragment);
        }

        if (bar == null) {
            return null;
        }

        if (AppContext.isFullScreen(bar.getWindow())) {
            bar.hideBar(BarHide.FLAG_HIDE_STATUS_BAR);
        } else if (SystemBarUtil.barHide != null) {
            bar.hideBar(barHide);
        }

        if (statusBarColor != null) {
            bar.statusBarColorInt(statusBarColor);
            bar.statusBarDarkFont(statusBarColor == Color.WHITE);
        }

        if (navigationBarColor != null) {
            bar.navigationBarColorInt(navigationBarColor);
        }

        return bar.keyboardEnable(true);
    }
}
