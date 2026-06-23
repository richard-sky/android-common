package com.richard.dev.common.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.library.basic.basic.adapter.BasicAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.library.basic.dto.ItemDTO;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityPinnedBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Administrator
 * @createDate: 2022/3/30 9:28
 * @version: 1.0
 * @description: 粘性header 列表
 */
@Route(path = "/test/pinned")
public class TestPinnedActivity extends BasicScaffoldActivity {

    private ActivityPinnedBinding binding;
    private int index = 0;
    private  ItemAdapter itemAdapter = new ItemAdapter();
    private  ItemAdapter itemAdapter2 = new ItemAdapter();


    @Override
    public void initLayoutView() {
        binding = ActivityPinnedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("pinned 列表");
        navigationbar.setTitleTextViewShow(true);
    }

    @Override
    public void bindListener() {
        binding.bvChange.setOnClickListener(v->{
            ItemAdapter adapter;
            if(index == 0){
                index = 1;
                adapter = itemAdapter;
                binding.srvView.setAdapter(itemAdapter);
            }else{
                index = 0;
                adapter = itemAdapter2;
                binding.srvView.setAdapter(itemAdapter2);
            }

            List<ItemDTO<String>> list = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                ItemDTO<String> item = new ItemDTO<String>("", null);
                if (i % 10 == 0) {
                    item.setItemType(ItemAdapter.Type.SECTION);
                } else {
                    item.setItemType(ItemAdapter.Type.ITEM);
                }
                item.setData("" + index);
                list.add(item);
            }
            adapter.completeLoad(list, true);
        });
        binding.bvChange.performClick();
    }

    private static class ItemAdapter extends BasicAdapter<ItemDTO<String>> {

        /**
         * 列表Item类型
         */
        public interface Type {
            /**
             * 具体item项类型
             */
            int ITEM = 0;


            /**
             * 分组section类型
             */
            int SECTION = 1;
        }

        public ItemAdapter() {
            super();
            super.addItemViewType(Type.SECTION);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            switch (viewType) {
                case Type.ITEM:
                    return android.R.layout.simple_list_item_2;
                case Type.SECTION:
                default:
                    return android.R.layout.simple_list_item_1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return getData().get(position).getItemType();
        }

        @Override
        protected void convert(BasicViewHolder holder, ItemDTO<String> itemInfo, int position) {
            switch (holder.getItemViewType()) {
                case Type.SECTION:
                    holder.setText(android.R.id.text1, "header");
                    holder.getRootView().setBackgroundResource(R.color.gray);
                    break;
                case Type.ITEM:
                    holder.setText(android.R.id.text1, "列表项" + (position + 1) + itemInfo.getData());
                    break;
            }
        }
    }
}
