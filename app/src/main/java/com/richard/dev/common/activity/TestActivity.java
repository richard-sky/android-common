package com.richard.dev.common.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.dev.common.R;
import com.richard.dev.common.TestKTDialog;
import com.richard.dev.common.databinding.ActivityTestBinding;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.context.util.LanguageUtil;

import java.util.Locale;

/**
 * <pre>
 * Description : 描述
 * Author : admin-richard
 * Date : 2022/10/28 11:53
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/10/28 11:53      admin-richard         new file.
 * </pre>
 */
@Route(path = "/test/test")
public class TestActivity extends BasicBindingActivity<ActivityTestBinding> {

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_test);
    }

    @Override
    public void initData() {

    }

    @Override
    public void bindListener() {
        binding.bvBtn.setOnClickListener(v -> {
            if (LanguageUtil.getAppContextLanguage().getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage())) {
                LanguageUtil.applyLanguage(Locale.ENGLISH);
            } else {
                LanguageUtil.applyLanguage(Locale.SIMPLIFIED_CHINESE);
            }

        });

        binding.bvBtn2.setOnClickListener(v -> {
            TestKTDialog dialog = new TestKTDialog(this);
            dialog.show();
        });
    }
}