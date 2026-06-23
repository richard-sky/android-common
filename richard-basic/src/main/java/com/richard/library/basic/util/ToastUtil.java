package com.richard.library.basic.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.context.simple.SimpleException;
import com.richard.library.basic.widget.ToastLayout;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.StringUtilKt;
import com.richard.library.context.util.UIThread;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;


public final class ToastUtil {

    private static final Config config = new Config();
    private static WeakReference<MyToast> lastToast;

    private ToastUtil() {
        // avoiding instantiation
    }

    /**
     * 默认配置
     */
    public static Config defaultConfig() {
        return config;
    }

    /**
     * 创建新的配置对象
     */
    public static Config make() {
        return new Config();
    }

    public static void show(@StringRes int textResId) {
        show(AppContext.getString(textResId));
    }

    public static void show(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, defaultConfig(), Toast.LENGTH_SHORT);
    }

    public static void show(@NonNull String format, Object... args) {
        showCustom(AppContext.get(), StringUtilKt.format(format, args), defaultConfig(), Toast.LENGTH_SHORT);
    }

    public static void showLong(@StringRes int textResId) {
        showLong(AppContext.getString(textResId));
    }

    public static void showLong(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, defaultConfig(), Toast.LENGTH_LONG);
    }

    public static void showLong(@NonNull String format, Object... args) {
        showCustom(AppContext.get(), StringUtilKt.format(format, args), defaultConfig(), Toast.LENGTH_LONG);
    }

    public static void showNormal(@StringRes int textResId) {
        showNormal(AppContext.getString(textResId));
    }

    public static void showNormal(@NonNull CharSequence message) {
        normalWithDarkThemeSupport(AppContext.get(), message, null, Toast.LENGTH_SHORT);
    }

    public static void showNormal(@NonNull String format, Object... args) {
        normalWithDarkThemeSupport(AppContext.get(), StringUtilKt.format(format, args), null, Toast.LENGTH_SHORT);
    }

    public static void showLongNormal(@StringRes int textResId) {
        showLongNormal(AppContext.getString(textResId));
    }

    public static void showLongNormal(@NonNull CharSequence message) {
        normalWithDarkThemeSupport(AppContext.get(), message, null, Toast.LENGTH_SHORT);
    }

    public static void showLongNormal(@NonNull String format, Object... args) {
        normalWithDarkThemeSupport(AppContext.get(), StringUtilKt.format(format, args), null, Toast.LENGTH_SHORT);
    }

    public static void showWarning(@StringRes int textResId) {
        showWarning(AppContext.getString(textResId));
    }

    public static void showWarning(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, make()
                .setIcon(R.drawable.ic_error_outline_white_24dp)
                .setBgColor(getColor(R.color.warningColor))
                .setTextColor(getColor(R.color.white)), Toast.LENGTH_SHORT);
    }

    public static void showWarning(@NonNull String format, Object... args) {
        showWarning(StringUtilKt.format(format, args));
    }

    public static void showLongWarning(@StringRes int textResId) {
        showLongWarning(AppContext.getString(textResId));
    }

    public static void showLongWarning(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, make()
                .setIcon(R.drawable.ic_error_outline_white_24dp)
                .setBgColor(getColor(R.color.warningColor))
                .setTextColor(getColor(R.color.white)), Toast.LENGTH_LONG);
    }

    public static void showLongWarning(@NonNull String format, Object... args) {
        showLongWarning(StringUtilKt.format(format, args));
    }

    public static void showInfo(@StringRes int textResId) {
        showInfo(AppContext.getString(textResId));
    }

    public static void showInfo(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, make()
                        .setIcon(R.drawable.ic_info_outline_white_24dp)
                        .setBgColor(getColor(R.color.infoColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_SHORT);
    }

    public static void showInfo(@NonNull String format, Object... args) {
        showInfo(StringUtilKt.format(format, args));
    }

    public static void showLongInfo(@StringRes int textResId) {
        showLongInfo(AppContext.getString(textResId));
    }

    public static void showLongInfo(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, make()
                        .setIcon(R.drawable.ic_info_outline_white_24dp)
                        .setBgColor(getColor(R.color.infoColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_LONG);
    }

    public static void showLongInfo(@NonNull String format, Object... args) {
        showLongInfo(StringUtilKt.format(format, args));
    }

    public static void showSuccess(@StringRes int textResId) {
        showSuccess(AppContext.getString(textResId));
    }

    public static void showSuccess(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, make()
                        .setIcon(R.drawable.ic_check_white_24dp)
                        .setBgColor(getColor(R.color.successColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_SHORT);
    }

    public static void showSuccess(@NonNull String format, Object... args) {
        showSuccess(StringUtilKt.format(format, args));
    }

    public static void showLongSuccess(@StringRes int textResId) {
        showLongSuccess(AppContext.getString(textResId));
    }

    public static void showLongSuccess(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, make()
                        .setIcon(R.drawable.ic_check_white_24dp)
                        .setBgColor(getColor(R.color.successColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_LONG);
    }

    public static void showLongSuccess(@NonNull String format, Object... args) {
        showLongSuccess(StringUtilKt.format(format, args));
    }

    public static void showError(@StringRes int textResId) {
        showError(AppContext.getString(textResId));
    }

    public static void showError(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, make()
                        .setIcon(R.drawable.ic_clear_white_24dp)
                        .setBgColor(getColor(R.color.errorColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_SHORT);
    }

    public static void showError(@NonNull String format, Object... args) {
        showError(StringUtilKt.format(format, args));
    }

    public static void showLongError(@StringRes int textResId) {
        showLongError(AppContext.getString(textResId));
    }

    public static void showLongError(@NonNull CharSequence message) {
        showCustom(AppContext.get(), message, make()
                        .setIcon(R.drawable.ic_clear_white_24dp)
                        .setBgColor(getColor(R.color.errorColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_LONG);
    }

    public static void showLongError(@NonNull String format, Object... args) {
        showLongError(StringUtilKt.format(format, args));
    }

    public static void show(Throwable throwable, String defaultMsg) {
        showError(getErrorText(throwable, defaultMsg));
    }

    public static void show(Throwable throwable) {
        show(throwable, AppContext.getString(R.string.toast_operate_fail));
    }


    public static void show(Context context, @StringRes int textResId) {
        show(context, AppContext.getString(textResId));
    }

    public static void show(Context context, @NonNull CharSequence message) {
        showCustom(context, message, defaultConfig(), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, @NonNull String format, Object... args) {
        showCustom(context, StringUtilKt.format(format, args), defaultConfig(), Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, @StringRes int textResId) {
        showLong(context, AppContext.getString(textResId));
    }

    public static void showLong(Context context, @NonNull CharSequence message) {
        showCustom(context, message, defaultConfig(), Toast.LENGTH_LONG);
    }

    public static void showLong(Context context, @NonNull String format, Object... args) {
        showCustom(context, StringUtilKt.format(format, args), defaultConfig(), Toast.LENGTH_LONG);
    }

    public static void showNormal(Context context, @StringRes int textResId) {
        showNormal(context, AppContext.getString(textResId));
    }

    public static void showNormal(Context context, @NonNull CharSequence message) {
        normalWithDarkThemeSupport(context, message, null, Toast.LENGTH_SHORT);
    }

    public static void showNormal(Context context, @NonNull String format, Object... args) {
        normalWithDarkThemeSupport(context, StringUtilKt.format(format, args), null, Toast.LENGTH_SHORT);
    }

    public static void showLongNormal(Context context, @StringRes int textResId) {
        showLongNormal(context, AppContext.getString(textResId));
    }

    public static void showLongNormal(Context context, @NonNull CharSequence message) {
        normalWithDarkThemeSupport(context, message, null, Toast.LENGTH_SHORT);
    }

    public static void showLongNormal(Context context, @NonNull String format, Object... args) {
        normalWithDarkThemeSupport(context, StringUtilKt.format(format, args), null, Toast.LENGTH_SHORT);
    }

    public static void showWarning(Context context, @StringRes int textResId) {
        showWarning(context, AppContext.getString(textResId));
    }

    public static void showWarning(Context context, @NonNull CharSequence message) {
        showCustom(context, message, make()
                .setIcon(R.drawable.ic_error_outline_white_24dp)
                .setBgColor(getColor(R.color.warningColor))
                .setTextColor(getColor(R.color.white)), Toast.LENGTH_SHORT);
    }

    public static void showWarning(Context context, @NonNull String format, Object... args) {
        showWarning(context, StringUtilKt.format(format, args));
    }

    public static void showLongWarning(Context context, @StringRes int textResId) {
        showLongWarning(context, AppContext.getString(textResId));
    }

    public static void showLongWarning(Context context, @NonNull CharSequence message) {
        showCustom(context, message, make()
                .setIcon(R.drawable.ic_error_outline_white_24dp)
                .setBgColor(getColor(R.color.warningColor))
                .setTextColor(getColor(R.color.white)), Toast.LENGTH_LONG);
    }

    public static void showLongWarning(Context context, @NonNull String format, Object... args) {
        showLongWarning(context, StringUtilKt.format(format, args));
    }

    public static void showInfo(Context context, @StringRes int textResId) {
        showInfo(context, AppContext.getString(textResId));
    }

    public static void showInfo(Context context, @NonNull CharSequence message) {
        showCustom(context, message, make()
                        .setIcon(R.drawable.ic_info_outline_white_24dp)
                        .setBgColor(getColor(R.color.infoColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_SHORT);
    }

    public static void showInfo(Context context, @NonNull String format, Object... args) {
        showInfo(context, StringUtilKt.format(format, args));
    }

    public static void showLongInfo(Context context, @StringRes int textResId) {
        showLongInfo(context, AppContext.getString(textResId));
    }

    public static void showLongInfo(Context context, @NonNull CharSequence message) {
        showCustom(context, message, make()
                        .setIcon(R.drawable.ic_info_outline_white_24dp)
                        .setBgColor(getColor(R.color.infoColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_LONG);
    }

    public static void showLongInfo(Context context, @NonNull String format, Object... args) {
        showLongInfo(context, StringUtilKt.format(format, args));
    }

    public static void showSuccess(Context context, @StringRes int textResId) {
        showSuccess(context, AppContext.getString(textResId));
    }

    public static void showSuccess(Context context, @NonNull CharSequence message) {
        showCustom(context, message, make()
                        .setIcon(R.drawable.ic_check_white_24dp)
                        .setBgColor(getColor(R.color.successColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_SHORT);
    }

    public static void showSuccess(Context context, @NonNull String format, Object... args) {
        showSuccess(context, StringUtilKt.format(format, args));
    }

    public static void showLongSuccess(Context context, @StringRes int textResId) {
        showLongSuccess(context, AppContext.getString(textResId));
    }

    public static void showLongSuccess(Context context, @NonNull CharSequence message) {
        showCustom(context, message, make()
                        .setIcon(R.drawable.ic_check_white_24dp)
                        .setBgColor(getColor(R.color.successColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_LONG);
    }

    public static void showLongSuccess(Context context, @NonNull String format, Object... args) {
        showLongSuccess(context, StringUtilKt.format(format, args));
    }

    public static void showError(Context context, @StringRes int textResId) {
        showError(context, AppContext.getString(textResId));
    }

    public static void showError(Context context, @NonNull CharSequence message) {
        showCustom(context, message, make()
                        .setIcon(R.drawable.ic_clear_white_24dp)
                        .setBgColor(getColor(R.color.errorColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_SHORT);
    }

    public static void showError(Context context, @NonNull String format, Object... args) {
        showError(context, StringUtilKt.format(format, args));
    }

    public static void showLongError(Context context, @StringRes int textResId) {
        showLongError(context, AppContext.getString(textResId));
    }

    public static void showLongError(Context context, @NonNull CharSequence message) {
        showCustom(context, message, make()
                        .setIcon(R.drawable.ic_clear_white_24dp)
                        .setBgColor(getColor(R.color.errorColor))
                        .setTextColor(getColor(R.color.white))
                , Toast.LENGTH_LONG);
    }

    public static void showLongError(Context context, @NonNull String format, Object... args) {
        showLongError(context, StringUtilKt.format(format, args));
    }

    public static void show(Context context, Throwable throwable, String defaultMsg) {
        showError(context, getErrorText(throwable, defaultMsg));
    }

    public static void show(Context context, Throwable throwable) {
        show(context, throwable, AppContext.getString(R.string.toast_operate_fail));
    }


    public static void showCustom(Context context, @NonNull CharSequence message, Config config, Integer duration) {
        UIThread.runOnUiThread(() -> {
            //为了解决副屏不能显示Toast的问题
            if (MultiScreenUtil.isSecondaryDisplay(context)) {
                try {
                    Activity activity = AppContext.getActivity(context);
                    if (activity != null) {
                        SnackBarUtil.with(activity.findViewById(android.R.id.content))
                                .setBgDrawable(ToastUtil.tint9PatchDrawableFrame(config.bgColor))
                                .setMessageTextColor(config.textColor)
                                .setMessageTextSize(config.textSize)
                                .setMessage(message)
                                .setMessageWidthWrapContent(true)
                                .show(true);

                        if (!config.isMultiScreenShow) {
                            return;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            MyToast toast = new MyToast();
            if (config.isRTL && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                toast.iconView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            toast.setBackground(config.bgColor);
            toast.setIcon(config.icon, config.textColor);
            toast.setDuration(duration == null ? Toast.LENGTH_SHORT : duration);
            toast.setText(message);
            toast.setTextColor(config.textColor);
            toast.setTypeface(config.typeface);
            toast.setTextSize(config.textSize);
            toast.setGravity(config.gravity, config.xOffset, config.yOffset);

            cancel();
            lastToast = new WeakReference<>(toast);
            toast.show();
        });
    }

    /**
     * Cancel the toast.
     */
    private static void cancel() {
        if (lastToast != null) {
            final MyToast toast = lastToast.get();
            if (toast != null) {
                toast.cancel();
            }
            lastToast = null;
        }
    }

    private static void normalWithDarkThemeSupport(Context context, @NonNull CharSequence message, @DrawableRes Integer iconResId, Integer duration) {
        if (config.supportDarkTheme && Build.VERSION.SDK_INT >= 29) {
            int uiMode = AppContext.get().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (uiMode == Configuration.UI_MODE_NIGHT_NO) {
                withLightTheme(context, message, iconResId, duration);
            } else {
                withDarkTheme(context, message, iconResId, duration);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                withLightTheme(context, message, iconResId, duration);
            } else {
                withDarkTheme(context, message, iconResId, duration);
            }
        }
    }

    private static void withLightTheme(Context context, @NonNull CharSequence message, @DrawableRes Integer iconResId, Integer duration) {
        showCustom(context, message, make().setIcon(iconResId), duration);
    }

    private static void withDarkTheme(Context context, @NonNull CharSequence message, @DrawableRes int iconResId, int duration) {
        showCustom(context, message, make().setIcon(iconResId).setBgColor(getColor(R.color.defaultTextColor)).setTextColor(getColor(R.color.normalColor)), duration);
    }

    /**
     * 获取错误消息文本
     *
     * @param throwable  异常信息
     * @param defaultMsg 默认显示文本
     */
    public static String getErrorText(Throwable throwable, String defaultMsg) {
        if (throwable instanceof SimpleException) {
            return throwable.getMessage();
        }
        return defaultMsg;
    }

    private static Drawable tintIcon(@DrawableRes Integer resId, @ColorInt int tintColor) {
        Drawable drawable = getDrawable(resId);
        if (drawable != null) {
            drawable.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
        }
        return drawable;
    }

    private static Drawable tint9PatchDrawableFrame(@ColorInt int tintColor) {
        return tintIcon(R.drawable.toast_frame, tintColor);
    }

    private static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }

    private static Drawable getDrawable(@DrawableRes Integer id) {
        if (id == null) {
            return null;
        }
        return AppCompatResources.getDrawable(AppContext.get(), id);
    }

    private static int getColor(@ColorRes int colorResId) {
        return ContextCompat.getColor(AppContext.get(), colorResId);
    }

    /**
     * 自定义Toalst
     */
    private static class MyToast extends Toast {

        private ToastLayout toastLayout;
        private ImageView iconView;
        private TextView textView;

        public MyToast() {
            super(AppContext.get());
            this.init();
        }

        private void init() {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                try {
                    //noinspection JavaReflectionMemberAccess
                    Field mTNField = Toast.class.getDeclaredField("mTN");
                    mTNField.setAccessible(true);
                    Object mTN = mTNField.get(this);
                    Field mTNmHandlerField = mTNField.getType().getDeclaredField("mHandler");
                    mTNmHandlerField.setAccessible(true);
                    Handler tnHandler = (Handler) mTNmHandlerField.get(mTN);
                    mTNmHandlerField.set(mTN, new SafeHandler(tnHandler));
                } catch (Exception ignored) {/**/}
            }

            toastLayout = (ToastLayout) ViewUtil.layoutId2View(R.layout.view_toasty_toast);
            iconView = toastLayout.findViewById(R.id.toast_icon);
            textView = toastLayout.findViewById(R.id.toast_text);
            iconView.setVisibility(View.GONE);

            toastLayout.setCallback(() -> {
                if (lastToast != null && lastToast.get() == this) {
                    lastToast.clear();
                    lastToast = null;
                }
                toastLayout = null;
                iconView = null;
                textView = null;
            });

            super.setView(toastLayout);
        }

        @Override
        public void setText(int resId) {
            textView.setText(AppContext.getString(resId));
        }

        @Override
        public void setText(CharSequence message) {
            textView.setText(message);
        }

        public void setTextSize(float size) {
            textView.setTextSize(size);
        }

        public void setTextColor(@ColorInt int color) {
            textView.setTextColor(color);
        }

        public void setTypeface(Typeface typeface) {
            textView.setTypeface(typeface);
        }

        public void setIcon(@DrawableRes Integer icon, @ColorInt int textColor) {
            Drawable tintIcon = ToastUtil.tintIcon(icon, textColor);
            if (tintIcon != null) {
                ToastUtil.setBackground(iconView, ToastUtil.tintIcon(icon, textColor));
                iconView.setVisibility(View.VISIBLE);
            }
        }

        public void setBackground(@ColorInt int color) {
            ToastUtil.setBackground(toastLayout, ToastUtil.tint9PatchDrawableFrame(color));
        }

        static class SafeHandler extends Handler {
            private final Handler impl;

            SafeHandler(Handler impl) {
                this.impl = impl;
            }

            @Override
            public void handleMessage(@NonNull Message msg) {
                impl.handleMessage(msg);
            }

            @Override
            public void dispatchMessage(@NonNull Message msg) {
                try {
                    impl.dispatchMessage(msg);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 个性化配置
     */
    public static class Config {

        private Typeface typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
        private int gravity = Gravity.CENTER;
        private int xOffset = 0;
        private int yOffset = 0;
        private boolean supportDarkTheme = true;
        private boolean isRTL = false;
        private boolean isMultiScreenShow = false;//是否主副屏幕同时显示

        private int textSize = 16;
        private int textColor = getColor(R.color.defaultTextColor);
        private int bgColor = getColor(R.color.normalColor);
        private Integer icon;

        private Config() {
            // avoiding instantiation
        }

        public Config setTextSize(int sizeInSp) {
            this.textSize = sizeInSp;
            return this;
        }

        public Config setGravity(int gravity, int xOffset, int yOffset) {
            this.gravity = gravity;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            return this;
        }

        public Config setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Config supportDarkTheme(boolean supportDarkTheme) {
            this.supportDarkTheme = supportDarkTheme;
            return this;
        }

        public Config setRTL(boolean isRTL) {
            this.isRTL = isRTL;
            return this;
        }

        public Config setMultiScreenShow(boolean multiScreenShow) {
            this.isMultiScreenShow = multiScreenShow;
            return this;
        }

        public Config setTypeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public Config setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Config setBgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Config setIcon(Integer icon) {
            this.icon = icon;
            return this;
        }
    }
}
