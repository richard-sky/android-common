package com.richard.library.basic.basic.uiview;

import android.content.Context;
import android.text.TextUtils;

import com.richard.library.basic.R;
import com.richard.library.basic.dialog.PromptDialog;
import com.richard.library.context.util.StringUtilKt;
import com.richard.library.basic.util.ToastUtil;
import com.richard.library.basic.widget.Loading;
import com.richard.library.context.AppContext;

/**
 * <pre>
 * Description : 显示提示信息UI实现
 * Author : admin-richard
 * Date : 2017/11/14 15:25
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/11/14 15:25     admin-richard         new file.
 * </pre>
 */
public class UIViewImpl implements UIView {

    private final Context context;
    private final Loading loading;
    private PromptDialog tipPromptDialog;
    private boolean defaultShowClose = true;

    public UIViewImpl(Context context) {
        this.context = context;
        loading = new Loading(context);
    }

    @Override
    public void setDefaultIsShowLoadingClose(boolean isShow) {
        this.defaultShowClose = isShow;
    }

    @Override
    public void showLoading() {
        this.showLoading(null, defaultShowClose);
    }

    @Override
    public void showLoading(String message) {
        this.showLoading(message, defaultShowClose);
    }

    @Override
    public void showLoading(String message, boolean isShowClose) {
        loading.setShowClose(isShowClose);
        loading.show(message);
    }

    @Override
    public void showLoading(int second) {
        this.showLoading(null, second, defaultShowClose, null);
    }

    @Override
    public void showLoading(int second, Runnable countDownEndListener) {
        this.showLoading(null, second, defaultShowClose, countDownEndListener);
    }

    @Override
    public void showLoading(String message, int second) {
        this.showLoading(message, second, defaultShowClose, null);
    }

    @Override
    public void showLoading(String message, int second, Runnable countDownEndListener) {
        this.showLoading(message, second, defaultShowClose, countDownEndListener);
    }

    @Override
    public void showLoading(String message, int second, boolean isShowClose, Runnable countDownEndListener) {
        loading.setShowClose(isShowClose);
        loading.show(message, second, countDownEndListener);
    }

    @Override
    public void dismissLoading() {
        this.dismissLoading(null);
    }

    @Override
    public void dismissLoading(String message) {
        if (!TextUtils.isEmpty(message) && message.length() > 12) {
            loading.dismiss(null);
            this.showMsgDialog(message, null);
            return;
        }
        loading.dismiss(message);
    }

    @Override
    public void dismissLoading(String message, boolean isSuccess) {
        loading.dismiss(message, isSuccess, null);
    }

    @Override
    public void dismissLoading(String message, boolean isSuccess, Runnable onCloseRun) {
        loading.dismiss(message, isSuccess, onCloseRun);
    }

    @Override
    public void showSuccessDialog(String message) {
        loading.setShowClose(false);
        loading.showTip(message, true);
    }

    @Override
    public void showErrorDialog(String message) {
        loading.setShowClose(false);
        loading.showTip(message, false);
    }

    @Override
    public void showHtmlMsgDialog(String message) {
        this.showTipDialog(
                null
                , message
                , true
                , "我知道了"
                , null
                , null
                , null
        );
    }

    @Override
    public void showMsgDialog(String message) {
        this.showMsgDialog(message, null);
    }

    @Override
    public void showMsgDialog(String message, PromptDialog.OnDialogClickListener onClickKnowListener) {
        this.showTipDialog(
                null
                , message
                , "我知道了"
                , null
                , onClickKnowListener
                , null
        );
    }

    @Override
    public void showMsgDialog(String title, String message, PromptDialog.OnDialogClickListener onClickKnowListener) {
        this.showTipDialog(
                title
                , message
                , "我知道了"
                , null
                , onClickKnowListener
                , null
        );
    }

    @Override
    public void showConfirmDialog(
            String message
            , PromptDialog.OnDialogClickListener onClickCancelListener
            , PromptDialog.OnDialogClickListener onClickConfirmListener
    ) {
        this.showTipDialog(
                null
                , message
                , AppContext.getString(R.string.dialog_cancel_label)
                , AppContext.getString(R.string.dialog_confirm_label)
                , onClickCancelListener
                , onClickConfirmListener
        );
    }

    @Override
    public void showConfirmDialog(
            String title
            , String message
            , PromptDialog.OnDialogClickListener onClickCancelListener
            , PromptDialog.OnDialogClickListener onClickConfirmListener
    ) {
        this.showTipDialog(
                title
                , message
                , AppContext.getString(R.string.dialog_cancel_label)
                , AppContext.getString(R.string.dialog_confirm_label)
                , onClickCancelListener
                , onClickConfirmListener
        );
    }

    @Override
    public void dismissMsgDialog() {
        if (tipPromptDialog != null) {
            tipPromptDialog.dismiss();
        }
    }

    @Override
    public void showMsg(String message) {
        this.dismissLoading(null);
        ToastUtil.show(context, message);
    }

    @Override
    public void showErrorMsg(Throwable th, String defaultMsg) {
        ToastUtil.showLongError(context, ToastUtil.getErrorText(th, defaultMsg));
    }

    @Override
    public void showErrorMsg(Throwable th) {
        ToastUtil.showLongError(context, StringUtilKt.defaultIfEmpty(th.getMessage(), AppContext.getString(R.string.toast_operate_fail)));
    }

    @Override
    public void showErrorMsg(String message) {
        ToastUtil.showLongError(context, message);
    }

    @Override
    public void showWarningMsg(String message) {
        ToastUtil.showWarning(context, message);
    }

    /**
     * 弹出对话框
     *
     * @param title                 标题
     * @param message               消息
     * @param leftBtnText           左按钮文本
     * @param rightBtnText          右按钮文本
     * @param leftBtnClickListener  左按钮点击事件
     * @param rightBtnClickListener 右按钮点击事件
     */
    private void showTipDialog(
            String title
            , String message
            , String leftBtnText
            , String rightBtnText
            , PromptDialog.OnDialogClickListener leftBtnClickListener
            , PromptDialog.OnDialogClickListener rightBtnClickListener
    ) {
        this.showTipDialog(
                title
                , message
                , false
                , leftBtnText
                , rightBtnText
                , leftBtnClickListener
                , rightBtnClickListener
        );
    }

    /**
     * 弹出对话框
     *
     * @param title                 标题
     * @param message               消息
     * @param leftBtnText           左按钮文本
     * @param rightBtnText          右按钮文本
     * @param leftBtnClickListener  左按钮点击事件
     * @param rightBtnClickListener 右按钮点击事件
     */
    private void showTipDialog(
            String title
            , String message
            , boolean isHtmlParse
            , String leftBtnText
            , String rightBtnText
            , PromptDialog.OnDialogClickListener leftBtnClickListener
            , PromptDialog.OnDialogClickListener rightBtnClickListener
    ) {
        if (tipPromptDialog != null && tipPromptDialog.isShowing()) {
            return;
        }

        tipPromptDialog = new PromptDialog.Builder(context)
                .setTitle(title == null ? AppContext.getString(R.string.dialog_title) : title)
                .setMessage(message, isHtmlParse)
                .setLeftBtn(leftBtnText, leftBtnClickListener)
                .setRightBtn(rightBtnText, rightBtnClickListener)
                .create();
        tipPromptDialog.show();
    }
}
