package com.richard.dev.common.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ItemTextBinding;

/**
 * @author: Administrator
 * @createDate: 2022/4/1 17:50
 * @version: 1.0
 * @description: 描述
 */
@Route(path = "/test/bindingActivity")
public class TestBindingActivity extends BasicBindingActivity<ItemTextBinding> {

    @Override
    public void initLayoutView() {
        super.setContentView(R.layout.item_text);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("Binding Activity");
        navigationbar.setTitleTextViewShow(true);
//        binding.setDataSource("Test Test");
    }

    @Override
    public void bindListener() {

    }
}
