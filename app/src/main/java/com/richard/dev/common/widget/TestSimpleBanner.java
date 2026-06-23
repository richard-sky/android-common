package com.richard.dev.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richard.library.basic.widget.SimpleBanner;
import com.richard.library.basic.util.ImageLoader;

/**
 * @author: Richard
 * @createDate: 2024/3/29 19:04
 * @version: 1.0
 * @description: 描述
 */
public class TestSimpleBanner extends SimpleBanner<String> {

    public TestSimpleBanner(@NonNull Context context) {
        super(context);
    }

    public TestSimpleBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestSimpleBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onClickImage(String itemInfo) {
        Log.d("testtt","========================>>>>>>>>>>>>>");
    }

    @Override
    protected void loadImage(ImageView imageView,String itemInfo) {
        ImageLoader.get().loadAnim(imageView,itemInfo);
    }
}
