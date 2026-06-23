package com.richard.dev.common.mvvm;

import com.richard.dev.common.mvvm.common.BasicRepository;
import com.richard.library.context.simple.SimpleException;
import com.richard.library.simplerx.XObservable;
import com.richard.library.simplerx.XObservableOnSubscribe;

/**
 * @author: Administrator
 * @createDate: 2022/3/23 18:02
 * @version: 1.0
 * @description: 请求登录接口（模拟）
 */
public class LoginRepository extends BasicRepository {

    /**
     * 登录
     *
     * @param userName 用户名
     * @param password 密码
     * @return 用户信息
     */
    public XObservable<User> login(String userName, String password) {
        return XObservable.create(new XObservableOnSubscribe<User>() {
            @Override
            public User run() throws Throwable {
                Thread.sleep(2000);
                User user = new User();
                user.setName("Richard123");
                user.setJob("bug工程师");

                if (Math.random() > 0.5) {
                    throw new SimpleException("用户名或者密码错误");
                }

                return user;
            }
        });
    }

}