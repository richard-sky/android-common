package com.richard.library.context.util;

import android.os.Build;
import android.view.View;
import android.view.Window;

import com.richard.library.context.immersionbar.BarHide;

/**
 * author Richard
 * date 2020/12/18 18:01
 * version V1.0
 * description: 隐藏系统底部导航栏工具类
 */
public class HideNavBarUtil {

    /**
     * 隐藏状态栏导航栏
     */
    public static void hideBar(Window window, BarHide barHide) {
        HideNavBarUtil.hideBar(window.getDecorView(), barHide);
    }

    /**
     * 隐藏状态栏导航栏
     */
    public static void hideBar(View v, BarHide barHide) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;

            if (barHide != null) {
                switch (barHide) {
                    case FLAG_HIDE_STATUS_BAR:
                        uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
                        break;
                    case FLAG_HIDE_BAR:
                        uiOptions = uiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    case FLAG_HIDE_NAVIGATION_BAR:
                        uiOptions = uiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                        break;
                    default:
                }
            }

            v.setSystemUiVisibility(uiOptions);
        }
    }
}
