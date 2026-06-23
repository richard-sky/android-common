package com.richard.library.basic.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.richard.library.context.AppContext;

import java.util.Locale;

/**
 * <pre>
 * Description : View 工具类
 * Author : admin-richard
 * Date : 2021-07-30 17:31
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021-07-30 17:31     admin-richard         new file.
 * </pre>
 */
public final class ViewUtil {

    private ViewUtil() {
    }

    /**
     * 判断RecyclerView是否滑动到顶部
     */
    public static boolean isToTop(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(-1);
    }

    /**
     * 判断RecyclerView是否滑动到底部
     */
    public static boolean isToBottom(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }

    /**
     * 触摸点是否在视图范围内
     *
     * @param view 视图
     * @param ev   触摸点
     * @return 是否在视图范围内
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        return !(ev.getX() < x) && !(ev.getX() > (x + view.getWidth()))
                && !(ev.getY() < y) && !(ev.getY() > (y + view.getHeight()));
    }

    /**
     * 获取该view的在屏幕的x和y坐标
     *
     * @param view view
     * @return locations[0]:x坐标、locations[1]:y坐标
     */
    public static int[] getLocOnScreen(View view) {
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        return locations;
    }

    /**
     * 获取该view的在window的x和y坐标
     *
     * @param view view
     * @return locations[0]:x坐标、locations[1]:y坐标
     */
    public static int[] getLocOnWindow(View view) {
        int[] locations = new int[2];
        view.getLocationInWindow(locations);
        return locations;
    }

    /**
     * 根据布局文件id加载布局view
     */
    public static View layoutId2View(@LayoutRes final int layoutId) {
        LayoutInflater inflate =
                (LayoutInflater) AppContext.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate.inflate(layoutId, null);
    }

    /**
     * Return whether horizontal layout direction of views are from Right to Left.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLayoutRtl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Locale primaryLocale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                primaryLocale = AppContext.getResources().getConfiguration().getLocales().get(0);
            } else {
                primaryLocale = AppContext.getResources().getConfiguration().locale;
            }
            return TextUtils.getLayoutDirectionFromLocale(primaryLocale) == View.LAYOUT_DIRECTION_RTL;
        }
        return false;
    }
}
