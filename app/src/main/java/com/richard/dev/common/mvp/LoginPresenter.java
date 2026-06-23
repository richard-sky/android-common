package com.richard.dev.common.mvp;

import androidx.lifecycle.LifecycleOwner;

import com.richard.library.basic.util.ToastUtil;
import com.richard.library.simplerx.XSubscribe;

/**
 * @author: Administrator
 * @createDate: 2022/3/22 16:19
 * @version: 1.0
 * @description: 描述
 */
public class LoginPresenter extends BasicPresenter<ILoginContract.View> implements ILoginContract.Presenter {

    private final LoginModel loginModel = new LoginModel();

    public LoginPresenter(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @Override
    public void login(String userName, String password) {
        loginModel.login(userName, password)
                .bindLife(super.getLifecycleOwner())
                .toAsyncSubscribe(new XSubscribe<Boolean>() {

                    @Override
                    public void onXNext(Boolean data) {
                        mView.onLoginSuccess();
                    }

                    @Override
                    public void onXError(Throwable e) {
                        mView.onLoginFail(ToastUtil.getErrorText(e, "登录失败"));
                    }
                });
    }
}
