package com.richard.library.context.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richard.library.context.AppContext;
import com.richard.library.context.simple.SimpleCallback;

import java.util.Locale;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2019/06/20
 *     desc  : 语言相关工具类
 * </pre>
 */
public class LanguageUtil {

    private static final String KEY_LOCALE = "KEY_LOCALE";
    private static final String VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM";

    private LanguageUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 应用系统语言
     */
    public static void applySystemLanguage() {
        applySystemLanguage(false);
    }

    /**
     * 应用系统语言
     *
     * @param isRelaunchApp true 为重启应用，false 为重建所有 Activity
     */
    public static void applySystemLanguage(final boolean isRelaunchApp) {
        applyLanguageReal(null, false, isRelaunchApp);
    }

    /**
     * 应用指定语言
     *
     * @param locale 语言区域对象
     */
    public static void applyLanguage(@NonNull final Locale locale) {
        applyLanguage(locale, false);
    }

    /**
     * 应用指定语言
     *
     * @param locale        语言区域对象
     * @param isRelaunchApp true 为重启应用，false 为重建所有 Activity
     */
    public static void applyLanguage(@NonNull final Locale locale,
                                     final boolean isRelaunchApp) {
        applyLanguageReal(locale, false, isRelaunchApp);
    }

    /**
     * 只应用指定语言(不会主动触发Activity recreate和 重启App)
     *
     * @param locale 语言区域对象
     */
    public static void onlyApplyLanguage(@NonNull final Locale locale) {
        applyLanguageReal(locale, true, false);
    }

    /**
     * 应用语言
     *
     * @param locale                语言区域对象
     * @param noRecreateAndRelaunch true 为不重建所有 Activity和不重启App，false 可以使用重建Activity或重启App
     * @param isRelaunchApp         true 为重启应用，false 为重建所有 Activity
     */
    private static void applyLanguageReal(final Locale locale, final boolean noRecreateAndRelaunch, final boolean isRelaunchApp) {
        if (locale == null) {
            SPUtil.put(KEY_LOCALE, VALUE_FOLLOW_SYSTEM);
        } else {
            SPUtil.put(KEY_LOCALE, locale2String(locale));
        }

        Locale destLocal = locale == null ? getLocal(Resources.getSystem().getConfiguration()) : locale;
        updateAppContextLanguage(destLocal, new SimpleCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean success) {
                if (noRecreateAndRelaunch) {
                    return;
                }
                if (success) {
                    restart(isRelaunchApp);
                } else {
                    // 使用重启应用方式
                    AppUtil.relaunchApp();
                }
            }
        });
    }

    private static void restart(final boolean isRelaunchApp) {
        if (isRelaunchApp) {
            AppUtil.relaunchApp();
        } else {
            for (Activity activity : ActivityUtil.getActivityList()) {
                activity.recreate();
            }
        }
    }

    /**
     * 判断是否已通过 {@link LanguageUtil} 应用了自定义语言
     *
     * @return true：是<br>false：否
     */
    public static boolean isAppliedLanguage() {
        return getAppliedLanguage() != null;
    }

    /**
     * 判断是否已通过 {@link LanguageUtil} 应用了指定语言
     *
     * @param locale 语言区域对象
     * @return true：是<br>false：否
     */
    public static boolean isAppliedLanguage(@NonNull Locale locale) {
        Locale appliedLocale = getAppliedLanguage();
        if (appliedLocale == null) {
            return false;
        }
        return isSameLocale(locale, appliedLocale);
    }

    /**
     * 当前应用是否使用简体中文
     */
    public static boolean isSimpleChinaForApp() {
        return getAppContextLanguage().getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage());
    }

    /**
     * 当前应用是否使用英文
     */
    public static boolean isSimpleEnglishForApp() {
        return getAppContextLanguage().getLanguage().equals(Locale.ENGLISH.getLanguage());
    }

    /**
     * 获取已应用的语言区域
     *
     * @return 已应用的语言区域对象
     */
    public static Locale getAppliedLanguage() {
        final String spLocaleStr = SPUtil.getString(KEY_LOCALE);
        if (TextUtils.isEmpty(spLocaleStr) || VALUE_FOLLOW_SYSTEM.equals(spLocaleStr)) {
            return null;
        }
        return string2Locale(spLocaleStr);
    }

    /**
     * 获取上下文对应的语言区域
     *
     * @return 上下文对应的语言区域对象
     */
    public static Locale getContextLanguage(Context context) {
        return getLocal(context.getResources().getConfiguration());
    }

    /**
     * 获取应用上下文对应的语言区域
     *
     * @return 应用上下文对应的语言区域对象
     */
    public static Locale getAppContextLanguage() {
        return getContextLanguage(AppContext.get());
    }

    /**
     * 获取系统语言区域
     *
     * @return 系统语言区域对象
     */
    public static Locale getSystemLanguage() {
        return getLocal(Resources.getSystem().getConfiguration());
    }

    /**
     * 更新应用上下文的语言区域
     *
     * @param destLocale 目标语言区域
     * @param consumer   回调接口
     */
    public static void updateAppContextLanguage(@NonNull Locale destLocale, @Nullable SimpleCallback<Boolean> consumer) {
        pollCheckAppContextLocal(destLocale, 0, consumer);
    }

    static void pollCheckAppContextLocal(final Locale destLocale, final int index, final SimpleCallback<Boolean> consumer) {
        Resources appResources = AppContext.getResources();
        Configuration appConfig = appResources.getConfiguration();
        Locale appLocal = getLocal(appConfig);

        setLocal(appConfig, destLocale);

        DisplayMetrics dm = new DisplayMetrics();
        dm.setTo(appResources.getDisplayMetrics());

        AppContext.getResources().updateConfiguration(appConfig, null);

        appResources.getDisplayMetrics().setTo(dm);

        if (consumer == null) return;

        if (isSameLocale(appLocal, destLocale)) {
            consumer.onCompleted(true);
        } else {
            if (index < 20) {
                UIThread.runOnUiThreadDelayed(16, new Runnable() {
                    @Override
                    public void run() {
                        pollCheckAppContextLocal(destLocale, index + 1, consumer);
                    }
                });
                return;
            }
            Log.e("LanguageUtils", "应用语言未更新。");
            consumer.onCompleted(false);
        }
    }

    /**
     * 如果设置语言不生效，尝试在 {@link Activity#attachBaseContext(Context)} 中调用此方法
     *
     * @param context 基础上下文
     * @return 已设置语言的上下文
     */
    public static Context attachBaseContext(Context context) {
        String spLocaleStr = SPUtil.getString(KEY_LOCALE);
        if (TextUtils.isEmpty(spLocaleStr) || VALUE_FOLLOW_SYSTEM.equals(spLocaleStr)) {
            return context;
        }

        Locale settingsLocale = string2Locale(spLocaleStr);
        if (settingsLocale == null) return context;

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        setLocal(config, settingsLocale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return context.createConfigurationContext(config);
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            dm.setTo(resources.getDisplayMetrics());

            resources.updateConfiguration(config, null);

            resources.getDisplayMetrics().setTo(dm);
            return context;
        }
    }

    public static void applyLanguage(final Activity activity) {
        String spLocale = SPUtil.getString(KEY_LOCALE);
        if (TextUtils.isEmpty(spLocale)) {
            return;
        }

        Locale destLocal;
        if (VALUE_FOLLOW_SYSTEM.equals(spLocale)) {
            destLocal = getLocal(Resources.getSystem().getConfiguration());
        } else {
            destLocal = string2Locale(spLocale);
        }

        if (destLocal == null) return;

        updateConfiguration(activity, destLocal);
        updateConfiguration(AppContext.get(), destLocal);
    }

    private static void updateConfiguration(Context context, Locale destLocal) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        setLocal(config, destLocal);

        DisplayMetrics dm = new DisplayMetrics();
        dm.setTo(resources.getDisplayMetrics());

        resources.updateConfiguration(config, null);

        resources.getDisplayMetrics().setTo(dm);
    }

    private static String locale2String(Locale locale) {
        String localLanguage = locale.getLanguage(); // 可能为空
        String localCountry = locale.getCountry(); // 可能为空
        return localLanguage + "$" + localCountry;
    }

    private static Locale string2Locale(String str) {
        Locale locale = string2LocaleReal(str);
        if (locale == null) {
            Log.e("LanguageUtils", "字符串 " + str + " 格式不正确。");
            SPUtil.remove(KEY_LOCALE);
        }
        return locale;
    }

    private static Locale string2LocaleReal(String str) {
        if (!isRightFormatLocalStr(str)) {
            return null;
        }

        try {
            int splitIndex = str.indexOf("$");
            return new Locale(str.substring(0, splitIndex), str.substring(splitIndex + 1));
        } catch (Exception ignore) {
            return null;
        }
    }

    private static boolean isRightFormatLocalStr(String localStr) {
        char[] chars = localStr.toCharArray();
        int count = 0;
        for (char c : chars) {
            if (c == '$') {
                if (count >= 1) {
                    return false;
                }
                ++count;
            }
        }
        return count == 1;
    }

    private static boolean isSameLocale(Locale l0, Locale l1) {
        return StringUtilKt.equals(l1.getLanguage(), l0.getLanguage())
                && StringUtilKt.equals(l1.getCountry(), l0.getCountry());
    }

    private static Locale getLocal(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return configuration.getLocales().get(0);
        } else {
            return configuration.locale;
        }
    }

    private static void setLocal(Configuration configuration, Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
    }
}