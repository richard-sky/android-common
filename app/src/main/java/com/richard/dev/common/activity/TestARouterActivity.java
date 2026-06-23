package com.richard.dev.common.activity;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityArouterBinding;
import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.library.basic.basic.adapter.BasicAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.simplerx.XObservable;
import com.richard.library.simplerx.XObservableOnSubscribe;
import com.richard.library.simplerx.XSubscribe;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: App开发通用库
 * @Package: com.richard.dev.common
 * @ClassName: SecondActivity
 * @CreateDate: 2022/3/10 17:26
 * @Author: Richard
 * @Version: 1.0
 * @Description: 描述
 */
@Route(path = "/test/second")
public class TestARouterActivity extends BasicScaffoldActivity {

    private ActivityArouterBinding binding;
    private ListAdapter adapter;

    @Override
    public void initLayoutView() {
        binding = ActivityArouterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void initData() {
        adapter = new ListAdapter();
        binding.refreshLayout.setAdapter(adapter);
        loadData();
    }

    @Override
    public void bindListener() {
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }

        });
    }

    private void loadData() {
        XObservable
                .create(new XObservableOnSubscribe<List<String>>() {
                    @Override
                    public List<String> run() throws Throwable {
                        List<String> data = new ArrayList<>();
                        for (int i = 0; i < 300; i++) {
                            data.add("");
                        }
                        return data;
                    }
                })
                .bindLife(this)
                .toAsyncSubscribe(new XSubscribe<List<String>>() {
                    @Override
                    public void onXNext(List<String> data) {
                        adapter.completeLoad(data, false);
                        binding.refreshLayout.completeLoading();
                    }

                    @Override
                    public void onEnd() {
                        binding.refreshLayout.finishRefresh(1000);
                    }
                });
    }

    private static class ListAdapter extends BasicAdapter<String> {

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_goods;
        }

        @Override
        protected void convert(BasicViewHolder holder, String itemInfo, int position) {

        }
    }
}
