package com.richard.dev.common.activity;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.TypeReference;
import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.library.context.util.LogUtil;
import com.richard.library.net.http.request.Requester;
import com.richard.library.simplerx.XObservable;
import com.richard.library.simplerx.XObservableOnSubscribe;
import com.richard.library.simplerx.XSubscribe;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * @author: Administrator
 * @createDate: 2022/3/22 17:18
 * @version: 1.0
 * @description: 描述
 */
@Route(path = "/test/request")
public class TestRequestActivity extends BasicScaffoldActivity {

    @Override
    public void initLayoutView() {
        TextView textView = new TextView(getContext());
        textView.setText("测试");
        setContentView(textView);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("Request");
        navigationbar.setTitleTextViewShow(true);


        XObservable.create((XObservableOnSubscribe<String>) () -> Requester.create()
                .get()
                .url("http://www.baidu.com")
                .request(new TypeReference<>(){}))
                .bindLife(this)
                .toAsyncSubscribe(new XSubscribe<String>() {

                    @Override
                    public void onXSubscribe(Disposable d) {
                        getUIView().showLoading();
                    }

                    @Override
                    public void onXNext(String data) {
                        getUIView().dismissLoading();
                        LogUtil.dTag("testtt", data);
                        getUIView().showMsgDialog(data,null);
                    }

                    @Override
                    public void onXError(Throwable e) {
                        getUIView().dismissLoading();
                        LogUtil.dTag("testtt", e);
                        getUIView().showMsgDialog(e.getMessage(),null);
                    }
                });
    }

    @Override
    public void bindListener() {

    }
}
