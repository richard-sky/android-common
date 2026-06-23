package com.richard.library.basic.widget.webview;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.richard.library.context.util.DensityUtilKt;

import java.io.File;

/**
 * <pre>
 * Description : 自定义WebChromeClient
 * Author : admin-richard
 * Date : 2018/11/20 11:58
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/11/20 11:58     admin-richard         new file.
 * </pre>
 */
public class ReWebChromeClient extends WebChromeClient {

    private Context context;
    private ReWebView mReWebView;
    private ReWebView.ReceiveTitleCallback receiveTitleCallback;


    public ReWebChromeClient(ReWebView reWebView) {
        this.mReWebView = reWebView;
        this.context = mReWebView.getContext();
    }


    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (receiveTitleCallback != null) {
            receiveTitleCallback.callback(mReWebView, title);
        }
    }

    @Override
    public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
        Toast.makeText(webView.getContext(), message, Toast.LENGTH_SHORT).show();
        return super.onJsAlert(webView, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView webView, String url, String message, JsResult jsResult) {
        this.onJsConfirmInternal(message, jsResult);
        return super.onJsConfirm(webView, url, message, jsResult);
    }

    @Override
    public boolean onJsPrompt(WebView webView, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
        this.onJsPromptInternal(message, defaultValue, jsPromptResult);
        return super.onJsPrompt(webView, url, message, defaultValue, jsPromptResult);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mReWebView.onProgressChanged(newProgress);
    }


    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        openFileChooser(valueCallback, "");
    }

    //For Android 3.0+
    public void openFileChooser(ValueCallback valueCallback, String acceptType) {
        mFileChooserCallBack.openFileChooserCallBack(valueCallback, acceptType);
    }

    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        openFileChooser(valueCallback, acceptType);
    }


    // For Android > 5.0
    @Override
    public boolean onShowFileChooser(WebView webView
            , ValueCallback<Uri[]> filePathCallback
            , WebChromeClient.FileChooserParams fileChooserParams
    ) {
        if (Build.VERSION.SDK_INT >= 21) {
            mFileChooserCallBack.openFileChooserCallBack(filePathCallback, fileChooserParams);
            return true;
        } else {
            return false;
        }
    }


    public void setReceiveTitleCallback(ReWebView.ReceiveTitleCallback receiveTitleCallback) {
        this.receiveTitleCallback = receiveTitleCallback;
    }

    /**
     * 自定义接口  方便MainActivity调用
     */
    public interface FileChooserCallBack {
        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);

        void openFileChooserCallBack(ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams);
    }

    //********************************************文件选择相关 startByColumnId****************************************************

    // 相册、拍照
    private ValueCallback mUploadMsg;
    private PickerImageCallback mPickerImageCallback;


    /**
     * 在网页点击选择文件时回调
     */
    private final FileChooserCallBack mFileChooserCallBack = new FileChooserCallBack() {
        @Override
        public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMsg = uploadMsg;
            showOptions();
        }

        @Override
        public void openFileChooserCallBack(ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
            mUploadMsg = uploadMsg;
            showOptions();
        }
    };


    /**
     * 处理选择文件或者拍照回调逻辑
     */
    public void handleImageFile(File imageFile) {
        if (mUploadMsg == null) {
            return;
        }
        if (imageFile != null && imageFile.exists()) {
            if (Build.VERSION.SDK_INT >= 21) {
                mUploadMsg.onReceiveValue(new Uri[]{Uri.fromFile(imageFile)});
            } else {
                mUploadMsg.onReceiveValue(Uri.fromFile(imageFile));
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mUploadMsg.onReceiveValue(new Uri[]{});
            } else {
                mUploadMsg.onReceiveValue(null);
            }
        }
    }


    /**
     * 弹出显示文件选择框
     */
    private void showOptions() {
        //只提供支持拍照获取
        mPickerImageCallback.takePhotoPickImage();
        //mPickerImageCallback.openGalleyPickImage();
    }


    public void setPickerImageCallback(PickerImageCallback pickerImageCallback) {
        mPickerImageCallback = pickerImageCallback;
    }

    public interface PickerImageCallback {
        void takePhotoPickImage();

        void openGalleyPickImage();
    }

    //********************************************文件选择相关 end****************************************************
    private AlertDialog mConfirmDialog;
    private AlertDialog mPromptDialog;

    /**
     * 弹出js确认框
     */
    private void onJsConfirmInternal(String message, JsResult jsResult) {
        if (mConfirmDialog == null) {
            mConfirmDialog = new AlertDialog.Builder(mReWebView.getContext())
                    .setMessage(message)//
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                        jsResult.cancel();
                    })
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                        jsResult.confirm();
                    })
                    .setOnCancelListener(dialog -> {
                        dialog.dismiss();
                        jsResult.cancel();
                    })
                    .create();

        }
        mConfirmDialog.setMessage(message);
        mConfirmDialog.show();
    }


    /**
     * 弹出js输入框
     */
    private void onJsPromptInternal(String message, String defaultValue, JsPromptResult jsPromptResult) {
        if (mPromptDialog == null) {
            LinearLayout contentView = new LinearLayout(mReWebView.getContext());
            contentView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            contentView.setOrientation(LinearLayout.VERTICAL);

            final EditText et = new EditText(mReWebView.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.leftMargin = DensityUtilKt.dp2px(10, context);
            lp.rightMargin = DensityUtilKt.dp2px(10, context);

            et.setLayoutParams(lp);
            et.setGravity(Gravity.CENTER_VERTICAL);
            et.setText(defaultValue);
            contentView.addView(et);

            mPromptDialog = new AlertDialog.Builder(mReWebView.getContext())
                    .setView(contentView)
                    .setTitle(message)
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        dialog.dismiss();
                        jsPromptResult.cancel();
                    })
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                        jsPromptResult.confirm(et.getText().toString());
                    })
                    .setOnCancelListener(dialog -> {
                        dialog.dismiss();
                        jsPromptResult.cancel();
                    })
                    .create();
        }
        mPromptDialog.show();
    }
}
