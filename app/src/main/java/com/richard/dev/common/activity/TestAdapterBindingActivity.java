package com.richard.dev.common.activity;

import android.view.View;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.library.basic.basic.adapter.BasicBindingAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityAdapterBindingBinding;
import com.richard.dev.common.databinding.ItemTextBinding;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Administrator
 * @createDate: 2022/4/1 16:48
 * @version: 1.0
 * @description: adapter binding
 */
@Route(path = "/test/adapterBinding")
public class TestAdapterBindingActivity extends BasicScaffoldActivity {

    private ActivityAdapterBindingBinding binding;

    @Override
    public void initLayoutView() {
        binding = ActivityAdapterBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("Adapter viewDataBinding");
        navigationbar.setTitleTextViewShow(true);

        ViewItemAdapter recyclerViewItemAdapter = new ViewItemAdapter();
        binding.srvView.setAdapter(recyclerViewItemAdapter);


        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            dataList.add("a" + (i + 1));
        }

        recyclerViewItemAdapter.completeLoad(dataList, true);
    }

    @Override
    public void bindListener() {
        binding.btnTest.setOnClickListener(v->{
            binding.refreshLayout.autoRefresh();
        });
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                binding.refreshLayout.finishLoadMore(500);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                binding.refreshLayout.finishRefresh(500);
            }
        });
        binding.refreshLayout.autoRefresh();
    }

    private static class ViewItemAdapter extends BasicBindingAdapter<String> {

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_text;
        }

        @Override
        protected void convert(BasicViewHolder holder, String itemInfo, int position) {
            ItemTextBinding binding = (ItemTextBinding) holder.getBinding();
            binding.setDataSource(itemInfo);
        }
    }

}
