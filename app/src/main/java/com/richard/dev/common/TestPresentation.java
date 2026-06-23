package com.richard.dev.common;

import android.content.Context;
import android.view.Display;

import com.richard.dev.common.databinding.PresentationAdBinding;
import com.richard.library.basic.basic.BasicBindingPresentation;

import java.util.List;

/**
 * @author: Richard
 * @createDate: 2024/4/1 9:53
 * @version: 1.0
 * @description: 描述
 */
public class TestPresentation extends BasicBindingPresentation<PresentationAdBinding> {

    public TestPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public TestPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.presentation_ad);
    }

    @Override
    public void initData() {
        binding.banner.setData(List.of( "https://pic.rmb.bdstatic.com/bjh/914b8c0f9814b14c5fedeec7ec6615df5813.jpeg"
                , "https://inews.gtimg.com/om_bt/OjPq2cnMN_-ivDKjxpCZ2kk_ab8YC5VMnL-0pQ21fUvd4AA/1000"
                , "https://inews.gtimg.com/om_bt/OuevRi3lDJoCccAqM17UARGbNlk9CRf3pGPv7He7zA8yYAA/1000"
                , "https://inews.gtimg.com/om_bt/OlegioDYjvpy63qO7jZ2KjSHuRv68eNlxkY0dSLPC-udwAA/1000"
                , "https://img1.baidu.com/it/u=2833240280,2198243692&fm=253&fmt=auto&app=138&f=JPEG?w=890&h=500"));
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void onAttachedToWindow() {
        binding.banner.startScroll();
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        binding.banner.stopScroll();
        super.onDetachedFromWindow();
    }
}
