package com.richard.dev.common.mvp;

import com.richard.library.simplerx.XObservable;
import com.richard.library.simplerx.XObservableOnSubscribe;

/**
 * @author: Administrator
 * @createDate: 2022/3/22 16:21
 * @version: 1.0
 * @description: 描述
 */
public class LoginModel implements ILoginContract.Model {

    @Override
    public XObservable<Boolean> login(String userName, String password) {
        return XObservable.create(new XObservableOnSubscribe<Boolean>() {
            @Override
            public Boolean run() throws Throwable {
                Thread.sleep(3000);
                return true;
            }
        });
    }
}
