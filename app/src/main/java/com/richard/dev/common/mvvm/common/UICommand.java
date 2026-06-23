package com.richard.dev.common.mvvm.common;

import androidx.lifecycle.Observer;

import com.richard.library.basic.basic.uiview.UIView;
import com.richard.library.basic.util.ToastUtil;
import com.richard.library.net.http.exception.DataErrorException;
import com.richard.library.net.http.exception.ResponseEmptyException;


/**
 * @author: admin-richard
 * @createDate: 2023/4/7 12:07
 * @version: 1.0
 * @description: UI指令触发
 */
public class UICommand<T> {

    private Command state;
    private String message;
    private T data;

    /**
     * 设置默认通用观察者
     *
     * @param uiView ui状态显示回调接口
     * @return Observer
     */
    public static <T> Observer<UICommand<T>> defaultObserver(UIView uiView) {
        return defaultObserver(uiView, null);
    }

    /**
     * 设置默认通用观察者
     *
     * @param uiView     ui状态显示回调接口
     * @param loadingTip 加载框提示文本
     * @return Observer
     */
    public static <T> Observer<UICommand<T>> defaultObserver(UIView uiView, String loadingTip) {
        return new Observer<UICommand<T>>() {
            @Override
            public void onChanged(UICommand<T> uiCommand) {
                switch (uiCommand.getState()) {
                    case ON_START:
                        uiView.showLoading(loadingTip);
                        break;
                    case ON_COMPLETE:
                        uiView.dismissLoading();
                        break;
                    case ON_ERROR:
                        uiView.dismissLoading();
                    case SHOW_MSG:
                        uiView.showMsg(uiCommand.getMessage());
                        break;
                }
            }
        };
    }

    public static <T> UICommand<T> create(Command state) {
        UICommand<T> uiCommand = new UICommand<T>();
        uiCommand.state = state;
        return uiCommand;
    }

    public static <T> UICommand<T> create(Command state, T data) {
        UICommand<T> uiCommand = new UICommand<T>();
        uiCommand.state = state;
        uiCommand.data = data;

        return uiCommand;
    }

    public static <T> UICommand<T> create(Command state, String message) {
        UICommand<T> uiCommand = new UICommand<T>();
        uiCommand.state = state;
        uiCommand.message = message;
        return uiCommand;
    }

    public static <T> UICommand<T> create(Command state, Throwable e) {
        UICommand<T> uiCommand = new UICommand<T>();
        uiCommand.state = state;
        if (e instanceof ResponseEmptyException || e instanceof DataErrorException) {
            uiCommand.message = "发生了错误，请稍后再试";
        } else {
            uiCommand.message = ToastUtil.getErrorText(e, "操作失败");
        }
        return uiCommand;
    }

    public static <T> UICommand<T> create(Throwable e) {
        if (e instanceof ResponseEmptyException || e instanceof DataErrorException) {
            return create(e, "发生了错误，请稍后再试");
        }
        return create(e, "操作失败");
    }

    public static <T> UICommand<T> create(Throwable e, String defaultMessage) {
        UICommand<T> uiCommand = new UICommand<T>();
        uiCommand.state = Command.ON_ERROR;
        uiCommand.message = ToastUtil.getErrorText(e, defaultMessage);
        return uiCommand;
    }

    public Command getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
