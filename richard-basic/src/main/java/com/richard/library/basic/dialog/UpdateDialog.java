package com.richard.library.basic.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.BasicBindingDialog;
import com.richard.library.basic.databinding.DialogAppUpdateBinding;
import com.richard.library.basic.provider.FileDownloadCallback;
import com.richard.library.basic.provider.FileDownloadProvider;
import com.richard.library.basic.util.InstallApkUtil;
import com.richard.library.basic.util.ToastUtil;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.DeviceUtil;

import java.io.File;


/**
 * <pre>
 * Description : APP版本更新提示框
 * Author : admin-richard
 * Date : 2017/11/28 14:15
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/11/28 14:15      admin-richard         new file.
 * </pre>
 */
public class UpdateDialog extends BasicBindingDialog<DialogAppUpdateBinding> implements View.OnClickListener, DialogInterface.OnDismissListener, FileDownloadCallback {

    private FileDownloadProvider fileDownloadProvider;
    private ConfirmUpgradeListener confirmUpgradeListener;
    private View.OnClickListener cancelClickListener;

    //app icon
    private @DrawableRes int appIconResId;
    //app 版本名称
    private String versionName;
    //apk 文件大小
    private String fileSize;
    //app 更新日志
    private String remark;
    //是否强制更新
    private boolean isForceUpdate;
    //下载APK链接
    private String downLoadLink;
    //下载完成的APK 文件
    private File apkFile;

    public UpdateDialog(Context context) {
        super(context);
    }

    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.dialog_app_update);
    }

    @Override
    public void initData() {
        if (AppContext.isScreenLandscape()) {
            //横屏
            super.setWidth(440);
        } else if (AppContext.isScreenPortrait()) {
            //竖屏
            super.setMarginLeftRight(15);
        }

        //更新进度条初始化
        binding.progressbar.setMax(100);

        //根据显示内容自适应高度
        binding.osvAppUpgradeRemark.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int actualHeight = binding.osvAppUpgradeRemark.getMeasuredHeight();
            float dealHeight = AppContext.getScreenHeight() / 2F;
            if (dealHeight < actualHeight) {
                binding.osvAppUpgradeRemark.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) dealHeight));
            }
        });


        if (appIconResId != 0) {
            binding.ivAppIcon.setImageResource(appIconResId);
            binding.ivAppIcon.setVisibility(View.VISIBLE);
        }

        binding.tvAppUpgradeVersionName.setText(versionName);
        binding.tvFileSize.setText(String.format("%sM", fileSize));
        binding.tvAppUpgradeRemark.setText(remark);
        binding.bvUpgrade.setVisibility(fileDownloadProvider == null ? View.GONE : View.VISIBLE);

        if (isForceUpdate) {
            binding.bvCancel.setVisibility(View.GONE);
            binding.contentUpgrade.setVisibility(View.VISIBLE);
            this.setCancelable(false);
            this.setCanceledOnTouchOutside(false);
        } else {
            binding.bvCancel.setVisibility(View.VISIBLE);
            binding.contentUpgrade.setVisibility(View.VISIBLE);
            this.setCancelable(true);
            this.setCanceledOnTouchOutside(true);
        }
    }

    @Override
    public void bindListener() {
        //绑定事件监听
        binding.bvCancel.setOnClickListener(this);
        binding.bvUpgrade.setOnClickListener(this);
        binding.bvUpgradeBrowser.setOnClickListener(this);
        binding.bvInstall.setOnClickListener(this);
        this.setOnDismissListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bv_cancel) {
            //取消
            super.dismiss();
            if (cancelClickListener != null) {
                cancelClickListener.onClick(v);
            }
        } else if (id == R.id.bv_upgrade) {
            //开始更新
            if (confirmUpgradeListener != null && !confirmUpgradeListener.onConfirmUpgrade()) {
                return;
            }
            binding.lineView.setVisibility(View.GONE);
            binding.contentUpdateRemark.setVisibility(View.GONE);
            binding.llAppUpgradeProgressContent.setVisibility(View.VISIBLE);
            binding.contentUpgrade.setVisibility(View.GONE);
            this.setCancelable(false);
            this.startUpdate();
        } else if (id == R.id.bv_upgrade_browser) {
            //通过浏览器下载安装
            DeviceUtil.toWebBrowser(downLoadLink);
        } else if (id == R.id.bv_install) {
            //立即安装
            if (apkFile == null) {
                getUIView().showMsg("未找到安装包文件");
                return;
            }

            //验证是否开启了安装未知应用权限
            Context context = getUiContext();
            if (!InstallApkUtil.checkNonMarketAppsPermStatus(context)) {
                getUIView().showConfirmDialog(
                        "您还未开启允许安装来自未知来源应用权限，请在系统设置中开启"
                        , null
                        , (dialogInterface, view) -> {
                            InstallApkUtil.startInstallPermissionSettingActivity(context);
                            dialogInterface.dismiss();
                        }
                );
                return;
            }

            //发起安装
            InstallApkUtil.install(context, apkFile, null);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        if (fileDownloadProvider != null) {
            fileDownloadProvider.cancelDownload();
        }
    }

    /**
     * 开始下载更新
     */
    private void startUpdate() {
        InstallApkUtil.clean(getContext());
        if (fileDownloadProvider != null) {
            fileDownloadProvider.startDownload(downLoadLink);
        } else {
            getUIView().showMsgDialog("未指定下载器");
        }
    }

    @Override
    public void onDownloadProgress(long total, long downloadSize) {
        if (UpdateDialog.super.isShowing()) {
            binding.progressbar.setProgress(getProgress(total, downloadSize));
        }
    }

    @Override
    public void onSuccess(File file) {
        apkFile = file;
        binding.tvProgressTip.setText("下载完成");
        binding.bvCancel.setVisibility(View.VISIBLE);
        binding.lineView.setVisibility(View.VISIBLE);
        binding.bvInstall.setVisibility(View.VISIBLE);
        InstallApkUtil.install(getUiContext(), file, null);
    }

    @Override
    public void onFailure(String message, String code) {
        if (!isForceUpdate) {
            UpdateDialog.super.dismiss();
        }
        ToastUtil.showLong(message);
        binding.lineView.setVisibility(View.VISIBLE);
        binding.contentUpdateRemark.setVisibility(View.VISIBLE);
        binding.llAppUpgradeProgressContent.setVisibility(View.GONE);
        binding.contentUpgrade.setVisibility(View.VISIBLE);
    }

    /**
     * 设置app icon
     */
    public UpdateDialog setAppIcon(@DrawableRes int appIconResId) {
        this.appIconResId = appIconResId;
        return this;
    }

    /**
     * 设置版本名称
     */
    public UpdateDialog setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    /**
     * 设置文件大小
     */
    public UpdateDialog setFileSizeText(String fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    /**
     * 设置更新日志
     */
    public UpdateDialog setUpdateRemark(String remark) {
        this.remark = remark;
        return this;
    }

    /**
     * 设置是否强制更新
     */
    public UpdateDialog setForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
        return this;
    }

    /**
     * 设置下载新版本app链接
     */
    public UpdateDialog setDownLoadLink(String downLoadLink) {
        this.downLoadLink = downLoadLink;
        return this;
    }

    /**
     * 设置确认更新按钮点击回调事件
     */
    public UpdateDialog setConfirmUpgradeListener(ConfirmUpgradeListener confirmUpgradeListener) {
        this.confirmUpgradeListener = confirmUpgradeListener;
        return this;
    }

    /**
     * 设置取消更新按钮点击回调事件
     */
    public UpdateDialog setCancelClickListener(View.OnClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
        return this;
    }

    /**
     * 设置文件下载器提供者
     */
    public UpdateDialog setFileDownloadProvider(FileDownloadProvider fileDownloadProvider) {
        this.fileDownloadProvider = fileDownloadProvider;
        this.fileDownloadProvider.attach(this);
        return this;
    }

    public interface ConfirmUpgradeListener {
        /**
         * 当点击确认更新时回调
         *
         * @return 返回true时代表继续网后续流程执行下载更新逻辑，false则终止后续流程逻辑
         */
        boolean onConfirmUpgrade();
    }
}
