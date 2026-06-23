package com.richard.dev.common.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.library.basic.basic.adapter.BasicBindingAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.basic.widget.list.SlideRecyclerView;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivitySlideBinding;

import java.util.Arrays;

/**
 * @author: Administrator
 * @createDate: 2022/4/2 13:50
 * @version: 1.0
 * @description: RecyclerView侧滑菜单
 */
@Route(path = "/test/slide")
public class TestSlideActivity extends BasicBindingActivity<ActivitySlideBinding> {

    private TestAdapter adapter;

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_slide);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("Test");
        navigationbar.setTitleTextViewShow(true);

        binding.slideView.addHeaderView(R.layout.view_smart_header,null);
        adapter = new TestAdapter();
        binding.slideView.setAdapter(adapter);
        binding.slideView.setMode(SlideRecyclerView.MOD_BOTH);
        adapter.completeLoad(Arrays.asList("测试1", "测试12", "测试123", "测试1244", "测试12555", "测试1552", "测试1662", "测试166662", "测试1266", "测试12"), true);
    }

    @Override
    public void bindListener() {
    }

    private static class TestAdapter extends BasicBindingAdapter<String> {

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_slide;
        }

        @Override
        protected void convert(BasicViewHolder holder, String itemInfo, int position) {

        }
    }

}
