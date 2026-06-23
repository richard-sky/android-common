/*
 * Copyright 2016 czy1121
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.richard.library.basic.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.richard.library.basic.R;
import com.richard.library.basic.dialog.PromptDialog;

import java.io.File;

/**
 * <pre>
 * Description : APP版本更新工具类
 * Author : admin-richard
 * Date : 2017/11/28 14:15
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/11/28 14:15      admin-richard         new file.
 * </pre>
 */
public final class InstallApkUtil {
    private static final String PREFS = "ezy.update.prefs";
    private static final String KEY_UPDATE = "ezy.update.prefs.update";

    public static void clean(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(PREFS, 0);
            File file = new File(context.getExternalCacheDir(), sp.getString(KEY_UPDATE, "") + ".apk");
            if (file.exists()) {
                file.delete();
            }
            sp.edit().clear().apply();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 安装APK安装包
     *
     * @param context   context
     * @param apkFile   apk文件
     * @param authority 文件访问授权
     */
    public static void install(Context context, File apkFile, String authority) {
        if (apkFile == null) {
            ToastUtil.showLong("请提供APK安装包");
            return;
        }

        if (!apkFile.exists()) {
            ToastUtil.showLong("安装文件文件不存在");
            return;
        }

        if (!apkFile.isFile()) {
            ToastUtil.showLong("安装包不是APK文件");
            return;
        }

        try {
            if (!checkNonMarketAppsPermStatus(context)) {
                new PromptDialog.Builder(context)
                        .setTitle(R.string.dialog_title)
                        .setMessage("您还未开启允许安装来自未知来源应用权限，请在系统设置中开启")
                        .setLeftBtn(R.string.dialog_cancel_label, null)
                        .setRightBtn("去开启", (dialogInterface, view) -> {
                            startInstallPermissionSettingActivity(context);
                            dialogInterface.dismiss();
                        })
                        .create()
                        .show();
                return;
            }

            authority = TextUtils.isEmpty(authority) ? context.getPackageName() + ".AppUpdateFileProvider" : authority;

            String mimeDefault = "application/vnd.android.package-archive";
            Uri uri;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, authority, apkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(apkFile);
            }

            intent.setDataAndType(uri, mimeDefault);
            context.startActivity(intent);

            SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            sp.edit().putString(KEY_UPDATE, apkFile.getName()).apply();
        } catch (Throwable e) {
            e.printStackTrace();
            ToastUtil.showLong("安装时出现错误");
        }
    }

    /**
     * 开启设置安装未知来源应用权限界面
     */
    public static void startInstallPermissionSettingActivity(Context context) {
        Intent intent = new Intent();

        //获取当前apk包URI，并设置到intent中（这一步设置，可让“未知应用权限设置界面”只显示当前应用的设置项）
        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        intent.setData(packageURI);

        //设置不同版本跳转未知应用的动作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        } else {
            intent.setAction(android.provider.Settings.ACTION_SECURITY_SETTINGS);
        }
        context.startActivity(intent);
    }


    /**
     * 检查系统是否开启可安装未知来源应用
     */
    public static boolean checkNonMarketAppsPermStatus(Context context) {
        boolean hasInstallPermission = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            hasInstallPermission = Settings.Secure.getInt(
                    context.getContentResolver()
                    , Settings.Secure.INSTALL_NON_MARKET_APPS, 0
            ) == 1;
        } else {
            hasInstallPermission = Settings.Global.getInt(
                    context.getContentResolver()
                    , Settings.Global.INSTALL_NON_MARKET_APPS, 0
            ) == 1;
        }

        return hasInstallPermission;
    }
}