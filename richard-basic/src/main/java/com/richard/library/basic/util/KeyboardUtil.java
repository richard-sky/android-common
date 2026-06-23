package com.richard.library.basic.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richard.library.context.AppContext;
import com.richard.library.context.util.UIThread;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/2
 *     desc  : 键盘相关工具类
 * </pre>
 */
public final class KeyboardUtil {

    private KeyboardUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Show the soft input.
     */
    public static void showSoftInput() {
        InputMethodManager imm =
                (InputMethodManager) AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Show the soft input.
     */
    public static void showSoftInput(@Nullable Activity activity) {
        if (activity == null) {
            return;
        }
        if (!isSoftInputVisible(activity)) {
            toggleSoftInput();
        }
    }

    /**
     * Show the soft input.
     *
     * @param view The view.
     */
    public static void showSoftInput(@NonNull final View view) {
        showSoftInput(view, false);
    }


    /**
     * 显示软键盘
     */
    public static void showSoftInput(@NonNull final View view, boolean isSelectAll) {
        showSoftInput(view, 0, isSelectAll, 100);
    }

    /**
     * 显示软键盘
     *
     * @param view        控件
     * @param flags       操作标识
     * @param isSelectAll 弹出软键盘时是否默认全部选中
     * @param delayShow   延迟显示软键盘时间,毫秒
     */
    public static void showSoftInput(@NonNull final View view, final int flags, boolean isSelectAll, long delayShow) {
        UIThread.runOnUiThreadDelayed(delayShow, () -> {
            showSoftInput(view, flags);
            if (isSelectAll && view instanceof EditText editText) {
                editText.selectAll();
            }
        });
    }

    /**
     * Show the soft input.
     *
     * @param view  The view.
     * @param flags Provides additional operating flags.  Currently may be
     *              0 or have the {@link InputMethodManager#SHOW_IMPLICIT} bit set.
     */
    public static void showSoftInput(@NonNull final View view, final int flags) {
        InputMethodManager imm =
                (InputMethodManager) AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        imm.showSoftInput(view, flags, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN
                        || resultCode == InputMethodManager.RESULT_HIDDEN) {
                    toggleSoftInput();
                }
            }
        });
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    public static void hideSoftInput(@Nullable final Activity activity) {
        if (activity == null) {
            return;
        }
        hideSoftInput(activity.getWindow());
    }

    /**
     * Hide the soft input.
     *
     * @param window The window.
     */
    public static void hideSoftInput(@Nullable final Window window) {
        if (window == null) {
            return;
        }
        View view = window.getCurrentFocus();
        if (view == null) {
            View decorView = window.getDecorView();
            View focusView = decorView.findViewWithTag("keyboardTagView");
            if (focusView == null) {
                view = new EditText(window.getContext());
                view.setTag("keyboardTagView");
                ((ViewGroup) decorView).addView(view, 0, 0);
            } else {
                view = focusView;
            }
            view.requestFocus();
        }
        hideSoftInput(view);
    }

    /**
     * Hide the soft input.
     *
     * @param view The view.
     */
    public static void hideSoftInput(@NonNull final View view) {
        InputMethodManager imm =
                (InputMethodManager) AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private static long millis;

    /**
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    public static void hideSoftInputByToggle(@Nullable final Activity activity) {
        if (activity == null) {
            return;
        }
        long nowMillis = SystemClock.elapsedRealtime();
        long delta = nowMillis - millis;
        if (Math.abs(delta) > 500 && KeyboardUtil.isSoftInputVisible(activity)) {
            KeyboardUtil.toggleSoftInput();
        }
        millis = nowMillis;
    }

    /**
     * Toggle the soft input display or not.
     */
    public static void toggleSoftInput() {
        InputMethodManager imm =
                (InputMethodManager) AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.toggleSoftInput(0, 0);
    }

    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示。
     */
    public static void setAllowShowSoftInput(TextView view, boolean isAllow) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setShowSoftInputOnFocus(isAllow);
        } else {
            Class<TextView> cls = TextView.class;
            Method method;
            try {
                //setShowSoftInputOnFocus方法是EditText从TextView继承来的的
                //可以用来设置当EditText获得焦点时软键盘是否可见
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(view, isAllow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static int sDecorViewDelta = 0;

    /**
     * Return whether soft input is visible.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSoftInputVisible(@NonNull final Activity activity) {
        return getDecorViewInvisibleHeight(activity.getWindow()) > 0;
    }

    /**
     * 验证当前软件键盘是否已弹出
     */
    public static boolean isSoftInputVisible(@NonNull final View view) {
        Activity activity = AppContext.getActivity(view.getContext());
        if (activity == null) {
            return false;
        }
        return getDecorViewInvisibleHeight(activity.getWindow()) > 0;
    }

    private static int getDecorViewInvisibleHeight(@NonNull final Window window) {
        final View decorView = window.getDecorView();
        final Rect outRect = new Rect();
        decorView.getWindowVisibleDisplayFrame(outRect);
        Log.d("KeyboardUtils",
                "getDecorViewInvisibleHeight: " + (decorView.getBottom() - outRect.bottom));
        int delta = Math.abs(decorView.getBottom() - outRect.bottom);
        if (delta <= getNavBarHeight() + getStatusBarHeight()) {
            sDecorViewDelta = delta;
            return 0;
        }
        return delta - sDecorViewDelta;
    }

    /**
     * Register soft input changed listener.
     *
     * @param view     The view.
     * @param listener The soft input changed listener.
     */
    public static void registerSoftInputChangedListener(@NonNull final View view,
                                                        @NonNull final OnSoftInputChangedListener listener) {
        registerSoftInputChangedListener(view.hashCode(), Objects.requireNonNull(AppContext.getWindow(view)), listener);
    }


    /**
     * Register soft input changed listener.
     *
     * @param activity The activity.
     * @param listener The soft input changed listener.
     */
    public static void registerSoftInputChangedListener(@NonNull final Activity activity,
                                                        @NonNull final OnSoftInputChangedListener listener) {
        registerSoftInputChangedListener(activity.hashCode(), activity.getWindow(), listener);
    }

    /**
     * Register soft input changed listener.
     *
     * @param window   The window.
     * @param listener The soft input changed listener.
     */
    public static void registerSoftInputChangedListener(@NonNull final Window window,
                                                        @NonNull final OnSoftInputChangedListener listener) {
        registerSoftInputChangedListener(window.hashCode(), window, listener);
    }

    /**
     * 注册监听软键盘打开和关闭事件
     *
     * @param tagKey   存储监听事件的key
     * @param window   window
     * @param listener 监听事件
     */
    private static void registerSoftInputChangedListener(int tagKey, @NonNull final Window window,
                                                         @NonNull final OnSoftInputChangedListener listener) {
        final int flags = window.getAttributes().flags;
        if ((flags & WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        final FrameLayout contentView = window.findViewById(android.R.id.content);
        final int[] decorViewInvisibleHeightPre = {getDecorViewInvisibleHeight(window)};
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = getDecorViewInvisibleHeight(window);
                if (decorViewInvisibleHeightPre[0] != height) {
                    listener.onSoftInputChanged(height);
                    decorViewInvisibleHeightPre[0] = height;
                }
            }
        };
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        contentView.setTag(-Math.abs(tagKey), onGlobalLayoutListener);
    }

    /**
     * Unregister soft input changed listener.
     *
     * @param view The view.
     */
    public static void unregisterSoftInputChangedListener(@NonNull final View view) {
        unregisterSoftInputChangedListener(view.hashCode(), Objects.requireNonNull(AppContext.getWindow(view)));
    }

    /**
     * Unregister soft input changed listener.
     *
     * @param activity The activity.
     */
    public static void unregisterSoftInputChangedListener(@NonNull final Activity activity) {
        unregisterSoftInputChangedListener(activity.hashCode(), activity.getWindow());
    }

    /**
     * Unregister soft input changed listener.
     *
     * @param window The window.
     */
    public static void unregisterSoftInputChangedListener(@NonNull final Window window) {
        unregisterSoftInputChangedListener(window.hashCode(), window);
    }

    /**
     * 取消注册监听软键盘打开和关闭事件
     *
     * @param tagKey 存储监听事件的key
     * @param window window
     */
    private static void unregisterSoftInputChangedListener(int tagKey, @NonNull final Window window) {
        final View contentView = window.findViewById(android.R.id.content);
        if (contentView == null) {
            return;
        }
        tagKey = -Math.abs(tagKey);
        Object tag = contentView.getTag(tagKey);
        if (tag instanceof ViewTreeObserver.OnGlobalLayoutListener) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                contentView.getViewTreeObserver().removeOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener) tag);
                //这里会发生内存泄漏 如果不设置为null
                contentView.setTag(tagKey, null);
            }
        }
    }

    /**
     * Fix the bug of 5497 in Android.
     * <p>Don't set adjustResize</p>
     *
     * @param activity The activity.
     */
    public static void fixAndroidBug5497(@NonNull final Activity activity) {
        fixAndroidBug5497(activity.getWindow());
    }

    /**
     * Fix the bug of 5497 in Android.
     * <p>It will clean the adjustResize</p>
     *
     * @param window The window.
     */
    public static void fixAndroidBug5497(@NonNull final Window window) {
        int softInputMode = window.getAttributes().softInputMode;
        window.setSoftInputMode(
                softInputMode & ~WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final FrameLayout contentView = window.findViewById(android.R.id.content);
        final View contentViewChild = contentView.getChildAt(0);
        final int paddingBottom = contentViewChild.getPaddingBottom();
        final int[] contentViewInvisibleHeightPre5497 = {getContentViewInvisibleHeight(window)};
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = getContentViewInvisibleHeight(window);
                if (contentViewInvisibleHeightPre5497[0] != height) {
                    contentViewChild.setPadding(contentViewChild.getPaddingLeft(),
                            contentViewChild.getPaddingTop(), contentViewChild.getPaddingRight(),
                            paddingBottom + getDecorViewInvisibleHeight(window));
                    contentViewInvisibleHeightPre5497[0] = height;
                }
            }
        });
    }

    private static int getContentViewInvisibleHeight(final Window window) {
        final View contentView = window.findViewById(android.R.id.content);
        if (contentView == null) {
            return 0;
        }
        final Rect outRect = new Rect();
        contentView.getWindowVisibleDisplayFrame(outRect);
        Log.d("KeyboardUtils",
                "getContentViewInvisibleHeight: " + (contentView.getBottom() - outRect.bottom));
        int delta = Math.abs(contentView.getBottom() - outRect.bottom);
        if (delta <= getStatusBarHeight() + getNavBarHeight()) {
            return 0;
        }
        return delta;
    }

    /**
     * Fix the leaks of soft input.
     *
     * @param activity The activity.
     */
    public static void fixSoftInputLeaks(@NonNull final Activity activity) {
        fixSoftInputLeaks(activity.getWindow());
    }

    /**
     * Fix the leaks of soft input.
     *
     * @param window The window.
     */
    public static void fixSoftInputLeaks(@NonNull final Window window) {
        InputMethodManager imm =
                (InputMethodManager) AppContext.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String[] leakViews =
                new String[]{"mLastSrvView", "mCurRootView", "mServedView", "mNextServedView"};
        for (String leakView : leakViews) {
            try {
                Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
                if (!leakViewField.isAccessible()) {
                    leakViewField.setAccessible(true);
                }
                Object obj = leakViewField.get(imm);
                if (!(obj instanceof View)) {
                    continue;
                }
                View view = (View) obj;
                if (view.getRootView() == window.getDecorView().getRootView()) {
                    leakViewField.set(imm, null);
                }
            } catch (Throwable ignore) {/**/}
        }
    }

    /**
     * Click blank area to hide soft input.
     * <p>Copy the following code in ur activity.</p>
     */
    public static void clickBlankArea2HideSoftInput() {
        Log.i("KeyboardUtils", "Please refer to the following code.");
        /*
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    KeyboardUtils.hideSoftInput(this);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        // Return whether touch the view.
        private boolean isShouldHideKeyboard(View v, MotionEvent event) {
            if ((v instanceof EditText)) {
                int[] l = {0, 0};
                v.getLocationOnScreen(l);
                int left = l[0],
                        top = l[1],
                        bottom = top + v.getHeight(),
                        right = left + v.getWidth();
                return !(event.getRawX() > left && event.getRawX() < right
                        && event.getRawY() > top && event.getRawY() < bottom);
            }
            return false;
        }
        */
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface

    /// ////////////////////////////////////////////////////////////////////////
    public interface OnSoftInputChangedListener {
        void onSoftInputChanged(int height);
    }

    //---------------------------------------------------------------------------------------------

    /**
     * Return the navigation bar's height.
     *
     * @return the navigation bar's height
     */
    private static int getNavBarHeight() {
        Resources res = Resources.getSystem();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    private static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
