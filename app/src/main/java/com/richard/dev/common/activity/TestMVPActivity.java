package com.richard.dev.common.activity;

import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.dev.common.mvp.ILoginContract;
import com.richard.dev.common.mvp.LoginPresenter;
import com.richard.library.basic.util.ToastUtil;

/**
 * @author: Administrator
 * @createDate: 2022/3/22 16:35
 * @version: 1.0
 * @description: 描述
 */
@Route(path = "/test/mvp")
public class TestMVPActivity extends BasicScaffoldActivity implements ILoginContract.View {

    private LoginPresenter loginPresenter;

    @Override
    public void initLayoutView() {
        TextView textView = new TextView(getContext());
        textView.setText("测试");
        setContentView(textView);
    }

    @Override
    public void initData() {
        loginPresenter = new LoginPresenter(this);
        loginPresenter.attachView(this);
        loginPresenter.login("123","123");
    }

    @Override
    public void bindListener() {
        ToastUtil.showCustom(getContext(),"测试2222",ToastUtil.defaultConfig(),null);
    }

    @Override
    public void onLoginSuccess() {
        getUIView().showMsg("登录成功");
    }

    @Override
    public void onLoginFail(String message) {
        getUIView().showMsg(message);
    }

    @Override
    protected void onDestroy() {
        loginPresenter.detachView();
        super.onDestroy();
    }
}
