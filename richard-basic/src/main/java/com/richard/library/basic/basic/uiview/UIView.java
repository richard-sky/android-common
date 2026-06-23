package com.richard.library.basic.basic.uiview;


import com.richard.library.basic.dialog.PromptDialog;

/**
 * <pre>
 * Description : ui状态回调接口
 * Author : admin-richard
 * Date : 2017/4/5 15:25
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/4/5 15:25     admin-richard         new file.
 * </pre>
 */
public interface UIView {

    /**
     * 设置加载框默认是否显示关闭按钮
     */
    void setDefaultIsShowLoadingClose(boolean isShow);

    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 显示加载框
     */
    void showLoading(String message);

    /**
     * 显示加载框
     */
    void showLoading(String message, boolean isShowClose);

    /**
     * 显示加载框
     *
     * @param second 倒计时秒数
     */
    void showLoading(int second);

    /**
     * 显示加载框
     *
     * @param second               倒计时秒数
     * @param countDownEndListener 倒计时结束时回调
     */
    void showLoading(int second, Runnable countDownEndListener);

    /**
     * 显示加载框
     *
     * @param message 提示消息
     * @param second  倒计时秒数
     */
    void showLoading(String message, int second);

    /**
     * 显示加载框
     *
     * @param message              提示消息
     * @param second               倒计时秒数
     * @param countDownEndListener 倒计时结束时回调
     */
    void showLoading(String message, int second, Runnable countDownEndListener);

    /**
     * 显示加载框
     *
     * @param message              提示消息
     * @param second               倒计时秒数
     * @param isShowClose          是否显示关闭按钮
     * @param countDownEndListener 倒计时结束时回调
     */
    void showLoading(String message, int second, boolean isShowClose, Runnable countDownEndListener);

    /**
     * 显示加载框
     */
    void dismissLoading();

    /**
     * 关闭加载框
     *
     * @param message 提示内容
     */
    void dismissLoading(String message);

    /**
     * 关闭加载框
     *
     * @param message   提示内容
     * @param isSuccess 是否成功的标识
     */
    void dismissLoading(String message, boolean isSuccess);

    /**
     * 关闭加载框
     *
     * @param message    提示内容
     * @param isSuccess  是否成功的标识
     * @param onCloseRun 在dismiss加载框时执行事件
     */
    void dismissLoading(String message, boolean isSuccess, Runnable onCloseRun);

    /**
     * 短暂提示成功
     *
     * @param message 提示内容
     */
    void showSuccessDialog(String message);

    /**
     * 短暂提示错误
     *
     * @param message 提示内容
     */
    void showErrorDialog(String message);

    /**
     * 显示Html格式消息对话框
     *
     * @param message 提示内容
     */
    void showHtmlMsgDialog(String message);

    /**
     * 显示消息对话框
     *
     * @param message 提示内容
     */
    void showMsgDialog(String message);

    /**
     * 显示消息对话框
     *
     * @param message             提示内容
     * @param onClickKnowListener 是否点击我知道了的时候回调事件,为null时默认为点击按钮时关闭消息框
     */
    void showMsgDialog(String message, PromptDialog.OnDialogClickListener onClickKnowListener);

    /**
     * 显示消息对话框
     *
     * @param title               标题
     * @param message             提示内容
     * @param onClickKnowListener 是否点击我知道了的时候回调事件,为null时默认为点击按钮时关闭消息框
     */
    void showMsgDialog(String title, String message, PromptDialog.OnDialogClickListener onClickKnowListener);


    /**
     * 显示消息对话框
     *
     * @param message                提示内容
     * @param onClickCancelListener  点击的取消按钮时回调事件,为null时默认为点击按钮时关闭消息框
     * @param onClickConfirmListener 点击的确定按钮时回调事件,为null时默认为点击按钮时关闭消息框
     */
    void showConfirmDialog(String message, PromptDialog.OnDialogClickListener onClickCancelListener, PromptDialog.OnDialogClickListener onClickConfirmListener);

    /**
     * 显示消息对话框
     *
     * @param title                  标题
     * @param message                提示内容
     * @param onClickCancelListener  点击的取消按钮时回调事件,为null时默认为点击按钮时关闭消息框
     * @param onClickConfirmListener 点击的确定按钮时回调事件,为null时默认为点击按钮时关闭消息框
     */
    void showConfirmDialog(String title, String message, PromptDialog.OnDialogClickListener onClickCancelListener, PromptDialog.OnDialogClickListener onClickConfirmListener);

    /**
     * 关闭消息提示框
     */
    void dismissMsgDialog();

    /**
     * 显示提示消息
     *
     * @param message 提示消息
     */
    void showMsg(String message);

    /**
     * 显示土司消息
     *
     * @param th         异常信息（判断若属于SimpleException，则会显示message字段数据，否则显示defaultMsg）
     * @param defaultMsg 当th 不属于SimpleException时，会显示该文本
     */
    void showErrorMsg(Throwable th, String defaultMsg);

    /**
     * 显示土司消息
     *
     * @param th 异常信息（判断若属于SimpleException，则会显示message字段数据，否则显示app内配置得默认提示消息）
     */
    void showErrorMsg(Throwable th);

    /**
     * 显示错误提示消息
     *
     * @param message 提示消息
     */
    void showErrorMsg(String message);

    /**
     * 显示警告提示消息
     *
     * @param message 提示消息
     */
    void showWarningMsg(String message);
}
