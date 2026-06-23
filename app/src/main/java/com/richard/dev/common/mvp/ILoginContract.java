package com.richard.dev.common.mvp;

import com.richard.library.simplerx.XObservable;

/**
 * @author: Administrator
 * @createDate: 2022/3/22 16:09
 * @version: 1.0
 * @description: 描述
 */
public interface ILoginContract {

    interface Model {
        XObservable<Boolean> login(String userName, String password);
    }

    interface View extends BasicView {

        void onLoginSuccess();

        void onLoginFail(String message);
    }

    interface Presenter {

        void login(String userName, String password);

    }

}
