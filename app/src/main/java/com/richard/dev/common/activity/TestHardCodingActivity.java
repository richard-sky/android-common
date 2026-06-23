package com.richard.dev.common.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityTestBinding;

/**
 * <pre>
 * Description : 系统MediaCodec硬编解码
 * Author : admin-richard
 * Date : 2022/10/28 11:53
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/10/28 11:53      admin-richard         new file.
 * </pre>
 */
@Route(path = "/test/hard/coding")
public class TestHardCodingActivity extends BasicBindingActivity<ActivityTestBinding> {

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_test);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("Test");
        navigationbar.setTitleTextViewShow(true);
    }

    @Override
    public void bindListener() {

    }
}
