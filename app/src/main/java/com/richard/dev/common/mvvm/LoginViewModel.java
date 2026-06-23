package com.richard.dev.common.mvvm;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.richard.dev.common.mvvm.common.BasicViewModel;
import com.richard.library.basic.util.ToastUtil;
import com.richard.library.simplerx.XSubscribe;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author: Administrator
 * @createDate: 2022/3/23 18:01
 * @version: 1.0
 * @description: 登录业务
 */
public class LoginViewModel extends BasicViewModel {

    private final LoginRepository loginRepository = new LoginRepository();

    private final ObservableField<User> observableUser = new ObservableField<>();
    private final ObservableField<String> observableLoginTip = new ObservableField<>();

    //可监听值的生命周期的变化
    private final MutableLiveData<Boolean> loginResultLiveData = new MutableLiveData<>(false);

    public LoginViewModel(@NonNull LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @Override
    protected void onCleared() {
        //当不再使用此ViewModel时，将调用此方法并将其销毁。<p>当ViewModel观察到一些数据并且需要清除此订阅以防止此ViewModel泄漏时，这很有用。
    }

    /**
     * 登录
     */
    public void login(String userName, String password) {
        loginRepository.login(userName, password)
                .bindLife(getLifecycleOwner())
                .toAsyncSubscribe(new XSubscribe<User>() {

                    @Override
                    public void onXSubscribe(Disposable d) {
                    }

                    @Override
                    public void onXNext(User data) {
                        observableUser.set(data);
                        observableLoginTip.set("登录成功");

                        //setValue必须在主线程中调用
                        //postValue是将指定值分配到主线程中
                        loginResultLiveData.setValue(true);
                    }

                    @Override
                    public void onXError(Throwable e) {
                        loginResultLiveData.setValue(false);
                        observableLoginTip.set(ToastUtil.getErrorText(e, "登录失败"));
                    }
                });
    }

    /**
     * 获取User信息
     */
    public ObservableField<User> getUser() {
        return this.observableUser;
    }

    /**
     * 获取登录结果提示消息
     */
    public ObservableField<String> getTip() {
        return this.observableLoginTip;
    }

    public MutableLiveData<Boolean> getLoginResultLiveData() {
        return loginResultLiveData;
    }
}
