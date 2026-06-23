package com.richard.dev.common;

import android.content.Context;

import com.richard.dev.common.databinding.DialogTestBinding;
import com.richard.library.basic.basic.BasicBindingDialog;

/**
 * @author: Richard
 * @createDate: 2024/10/14 11:22
 * @version: 1.0
 * @description: none
 */
public class TestDialog extends BasicBindingDialog<DialogTestBinding> {

    public TestDialog(Context context) {
        super(context);
    }

    public TestDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.dialog_test);
    }

    @Override
    public void initData() {
        setSize(300, 200);
    }

    @Override
    public void bindListener() {

    }
}
