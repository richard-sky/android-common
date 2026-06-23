package com.richard.dev.common.activity;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.view.View;

import androidx.lifecycle.Observer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityMvvmBinding;
import com.richard.dev.common.mvvm.LoginViewModel;
import com.richard.library.basic.basic.BasicBindingActivity;

/**
 * @author: Administrator
 * @createDate: 2022/3/23 18:02
 * @version: 1.0
 * @description: MVVM架构模式示例（详见https://www.jianshu.com/p/bd9016418af2）
 */
@Route(path = "/test/mvvm")
public class TestMVVMActivity extends BasicBindingActivity<ActivityMvvmBinding> {

    private LoginViewModel loginViewModel;


    @SuppressLint("SetTextI18n")
    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_mvvm);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);

        //绑定ViewModel
        loginViewModel = new LoginViewModel(this);
        binding.setLoginViewModel(loginViewModel);
    }

    @Override
    public void bindListener() {
        binding.setEvent(new Event());

        loginViewModel.getLoginResultLiveData()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        getUIView().showMsgDialog(aBoolean ? "登录成功" : "登录失败",null);
                    }
                });
    }

    /**
     * 事件
     */
    public class Event {

        /**
         * 点击登录按钮事件
         */
        public void onClickLogin() {
            loginViewModel.login(
                    binding.etUsername.getText().toString()
                    ,binding.etPassword.getText().toString()
            );
        }

        /**
         * text文本变化事件
         */
        public void onTextChanged(Editable s) {
            getUIView().showMsg(s.toString());
        }
    }
}
