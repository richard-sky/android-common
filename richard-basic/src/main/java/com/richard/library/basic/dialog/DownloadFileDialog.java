package com.richard.library.basic.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.BasicBindingDialog;
import com.richard.library.basic.databinding.DialogDownloadFileBinding;
import com.richard.library.basic.provider.FileDownloadCallback;
import com.richard.library.basic.provider.FileDownloadProvider;
import com.richard.library.basic.util.ToastUtil;
import com.richard.library.context.AppContext;

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
public class DownloadFileDialog extends BasicBindingDialog<DialogDownloadFileBinding> implements View.OnClickListener, DialogInterface.OnDismissListener, FileDownloadCallback {

    private FileDownloadProvider fileDownloadProvider;

    //下载监听事件
    private OnDownloadListener mOnDownloadListener;

    //标题
    private CharSequence title;
    //文件名称
    private String fileName;
    //文件大小
    private String fileSize;
    //描述
    private String description;
    //下载APK链接
    private String downLoadLink;

    public DownloadFileDialog(Context context) {
        super(context);
    }

    public DownloadFileDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.dialog_download_file);
    }

    @Override
    public void initData() {
        super.setMarginLeftRight(30);
        binding.navigationBar.setTitle("提示");
        binding.navigationBar.setTitleTextViewShow(true);

        //更新进度条初始化
        binding.progressbar.setMax(100);

        binding.tvFileName.setVisibility(View.GONE);
        binding.tvFileSize.setVisibility(View.GONE);
        binding.osvFileDescription.setVisibility(View.GONE);

        //根据显示内容自适应高度
        binding.osvFileDescription.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int actualHeight = binding.osvFileDescription.getMeasuredHeight();
            float dealHeight = AppContext.getScreenHeight() / 3F;
            if (dealHeight < actualHeight) {
                binding.osvFileDescription.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) dealHeight));
            }
        });

        binding.navigationBar.setTitle(title == null ? "" : title.toString());
        binding.tvFileName.setVisibility(TextUtils.isEmpty(fileName) ? View.GONE : View.VISIBLE);
        binding.tvFileName.setText(fileName);
        binding.tvFileSize.setVisibility(TextUtils.isEmpty(fileSize) ? View.GONE : View.VISIBLE);
        binding.tvFileSize.setText(String.format("文件大小 : %sM", fileSize));
        binding.osvFileDescription.setVisibility(TextUtils.isEmpty(description) ? View.GONE : View.VISIBLE);
        binding.tvFileDescription.setText(description);
    }

    @Override
    public void bindListener() {
        //绑定事件监听
        binding.bvCancel.setOnClickListener(this);
        binding.bvDownload.setOnClickListener(this);
        this.setOnDismissListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bv_cancel) {
            //取消
            super.dismiss();
            if (mOnDownloadListener != null) {
                mOnDownloadListener.onCancelDownload();
            }
        } else if (id == R.id.bv_download) {
            //下载
            binding.lineView.setVisibility(View.GONE);
            binding.osvFileDescription.setVisibility(View.GONE);
            binding.llFileProgressContent.setVisibility(View.VISIBLE);
            binding.bvDownload.setVisibility(View.GONE);
            this.setCancelable(false);
            fileDownloadProvider.startDownload(downLoadLink);
            if (mOnDownloadListener != null) {
                mOnDownloadListener.onStartDownload();
            }
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        fileDownloadProvider.cancelDownload();
    }

    @Override
    public void onDownloadProgress(long total, long downloadSize) {
        if (DownloadFileDialog.super.isShowing()) {
            binding.progressbar.setProgress(getProgress(total, downloadSize));
        }
    }

    @Override
    public void onSuccess(File file) {
        binding.tvProgressTip.setText("下载完成");
        binding.bvCancel.setVisibility(View.VISIBLE);
        binding.lineView.setVisibility(View.VISIBLE);
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onFinishDownload(file);
        }
        dismiss();
    }

    @Override
    public void onFailure(String message, String code) {
        ToastUtil.showLong(message);
        binding.lineView.setVisibility(View.VISIBLE);
        binding.osvFileDescription.setVisibility(View.VISIBLE);
        binding.llFileProgressContent.setVisibility(View.GONE);
        binding.bvDownload.setVisibility(View.VISIBLE);
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onErrorDownload(message);
        }
    }


    @Override
    public void setTitle(int titleId) {
        this.setTitleText(getContext().getResources().getText(titleId));
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        this.setTitleText(title);
    }

    /**
     * 设置标题文本
     */
    public DownloadFileDialog setTitleText(CharSequence title) {
        this.title = title;
        return this;
    }

    /**
     * 设置文件名称
     */
    public DownloadFileDialog setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * 设置文件大小
     */
    public DownloadFileDialog setFileSizeText(String fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    /**
     * 设置描述文本
     */
    public DownloadFileDialog setDescriptionText(String description) {
        this.description = description;
        return this;
    }

    /**
     * 设置下载文件链接
     */
    public DownloadFileDialog setDownLoadLink(String downLoadLink) {
        this.downLoadLink = downLoadLink;
        return this;
    }

    /**
     * 设置下载监听事件
     */
    public DownloadFileDialog setOnDownloadListener(OnDownloadListener onDownloadListener) {
        mOnDownloadListener = onDownloadListener;
        return this;
    }

    /**
     * 设置文件下载器提供者
     */
    public DownloadFileDialog setFileDownloadProvider(FileDownloadProvider fileDownloadProvider) {
        this.fileDownloadProvider = fileDownloadProvider;
        this.fileDownloadProvider.attach(this);
        return this;
    }

    /**
     * 下载监听事件
     */
    public interface OnDownloadListener {
        /**
         * 开始下载
         */
        default void onStartDownload() {
        }

        /**
         * 下载中发送错误
         */
        default void onErrorDownload(String message) {
        }

        /**
         * 取消下载
         */
        default void onCancelDownload() {
        }

        /**
         * 完成下载
         */
        void onFinishDownload(File file);
    }
}
