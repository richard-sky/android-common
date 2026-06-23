package com.richard.dev.common;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richard.dev.common.databinding.ActivityMvvmBinding;
import com.richard.library.basic.basic.BasicBindingView;

/**
 * @author: Richard
 * @createDate: 2024/3/14 11:03
 * @version: 1.0
 * @description: 描述
 */
public class TestView extends BasicBindingView<ActivityMvvmBinding> {

    public TestView(@NonNull Context context) {
        super(context);
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TestView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLifeMode() {
        return LifeMode.MAIN;
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_mvvm);
    }

    @Override
    public void initData() {
    }

    @Override
    public void bindListener() {

    }
}
