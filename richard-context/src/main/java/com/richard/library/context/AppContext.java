package com.richard.library.context;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.richard.library.context.life.ActivityLifecycleImpl;

/**
 * <pre>
 * Description : App Context 统一管理
 * Author : admin-richard
 * Date : 2018/6/20 11:34
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/6/20 11:34     admin-richard         new file.
 * </pre>
 */
public final class AppContext {

    private static Application application;
    private static boolean isDebug;

    private AppContext() {
    }

    /**
     * 初始化
     *
     * @param application application
     * @param isDebug     是否debug模式
     */
    public static void init(Application application, boolean isDebug) {
        AppContext.isDebug = isDebug;

        if (AppContext.application == null) {
            AppContext.application = application;
            ActivityLifecycleImpl.INSTANCE.init(AppContext.application);
            return;
        }

        if (AppContext.application.equals(application)) return;
        ActivityLifecycleImpl.INSTANCE.unInit(AppContext.application);
        AppContext.application = application;
        ActivityLifecycleImpl.INSTANCE.init(AppContext.application);
    }

    /**
     * 获取applicationContext
     */
    public static Context get() {
        if (application == null) {
            Log.e("error", "Uninitialized ApplicationContext");
        }
        return application;
    }

    /**
     * 获取Application
     */
    public static Application getApplication() {
        return application;
    }

    /**
     * 应用是否属于debug模式
     */
    public static boolean isDebug() {
        return isDebug;
    }

    /**
     * 获取应用包名
     */
    public static String getPackageName() {
        return application.getPackageName();
    }

    /**
     * 获取应用resource
     */
    public static Resources getResources() {
        return get().getResources();
    }

    /**
     * 获取string
     */
    public static String getString(@StringRes int id) {
        return getResources().getString(id);
    }

    /**
     * 获取格式化后的string
     *
     * @param formatId   格式化文本资源id
     * @param formatArgs 填充文本
     */
    public static String getString(@StringRes int formatId, Object... formatArgs) {
        return format(getString(formatId), formatArgs);
    }

    /**
     * 格式化字符
     *
     * @param format     格式化
     * @param formatArgs 填充文本
     */
    public static String format(String format, Object... formatArgs) {
        return String.format(format, formatArgs);
    }

    /**
     * 获取颜色值
     */
    public static int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    /**
     * 获取颜色值
     */
    @SuppressWarnings("all")
    public static int getColor(@ColorRes int id, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(id, theme);
        }
        return getResources().getColor(id);
    }

    /**
     * 获取px大小
     */
    public static int getDimensionPixelSize(@DimenRes int id) {
        return getResources().getDimensionPixelSize(id);
    }

    /**
     * 获取大小（单位px）
     */
    public static float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

    /**
     * 获取Drawable对象
     */
    public static Drawable getDrawable(@DrawableRes int id) {
        return getDrawable(id, null);
    }

    /**
     * 获取Drawable对象
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getDrawable(@DrawableRes int id, Resources.Theme theme) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable;
        if (theme == null) {
            drawable = getResources().getDrawable(id);
        } else {
            drawable = getResources().getDrawable(id, theme);
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    /**
     * 当前是否是横屏
     */
    public static boolean isScreenLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 当前是否是横屏
     */
    public static boolean isScreenLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 当前是否是竖屏
     */
    public static boolean isScreenPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isScreenPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 安全的获取window
     */
    public static Window getWindow(View view) {
        Context context = getUIContext(view.getContext());
        if (context != null) {
            return ((Activity) context).getWindow();
        }
        return null;
    }

    /**
     * 安全的获取Activity的context
     */
    public static Context getUIContext(View view) {
        return getUIContext(view.getContext());
    }

    /**
     * 安全的获取Activity的context
     *
     * @param context 上下文
     * @return 提取到的 Activity，否则为 null。
     */
    public static Context getUIContext(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return context;
        } else if (context instanceof ContextWrapper) {
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            return getUIContext(baseContext);
        }
        return null;
    }

    public static Activity getActivity(Context context) {
        if (getUIContext(context) instanceof Activity activity) {
            return activity;
        }
        return null;
    }

    /**
     * 获得屏幕高度(单位：像素)
     */
    public static int getScreenWidth() {
        return getScreenWidth(AppContext.get());
    }


    /**
     * 获得屏幕高度(单位：像素)
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


    /**
     * 获得屏幕宽度(单位：像素)
     */
    public static int getScreenHeight() {
        return getScreenHeight(AppContext.get());
    }

    /**
     * 获得屏幕宽度(单位：像素)
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得副屏幕高度(单位：像素)
     */
    public static int getDisplayWidth(Display display) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得副屏幕宽度(单位：像素)
     */
    public static int getDisplayHeight(Display display) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取状态栏高度(单位：像素)
     *
     * @return 状态栏高度
     */
    public static int getStatusHeight() {
        @SuppressLint("DiscouragedApi") int resourceId = AppContext.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        return AppContext.getResources().getDimensionPixelSize(resourceId);
    }


    /**
     * 获取状态栏高度(单位：像素)
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取导航栏高度(单位：像素)
     */
    public static int getNavBarHeight() {
        Resources resources = AppContext.getResources();
        @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = AppContext.getScreenWidth();
        int height = AppContext.getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = AppContext.getScreenWidth();
        int height = AppContext.getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取Window是否是全屏显示
     */
    public static boolean isFullScreen(Window window) {
        if (window == null) {
            return false;
        }
        WindowManager.LayoutParams attrs = window.getAttributes();
        return (attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }
}
