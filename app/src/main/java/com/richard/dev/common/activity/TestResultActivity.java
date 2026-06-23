package com.richard.dev.common.activity;

import android.content.Intent;
import android.view.View;

import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityResultBinding;

/**
 * @author: Administrator
 * @createDate: 2022/3/29 10:42
 * @version: 1.0
 * @description: activity结果返回
 */
public class TestResultActivity extends BasicScaffoldActivity {

    private ActivityResultBinding binding;

    @Override
    public void initLayoutView() {
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void initData() {
        overridePendingTransition(R.anim.activity_bottom_enter,R.anim.activity_bottom_silent);
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("Activity result");
        navigationbar.setTitleTextViewShow(true);

        binding.setEvent(this);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_bottom_silent,R.anim.activity_bottom_exit);
    }

    public void onClickReturnResult() {
        Intent intent = new Intent();
        intent.putExtra("result", "this is true");
        setResult(RESULT_OK, intent);
        finish();
    }
}
