package com.richard.library.basic.basic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.richard.library.basic.widget.flowlayout.FlowLayout;
import com.richard.library.basic.widget.flowlayout.TagView;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Description : 标签列表适配器
 * Author : admin-richard
 * Date : 2017/9/22 14:00
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/9/22 14:00      admin-richard         new file.
 * </pre>
 */
public abstract class BasicTagAdapter<T> {

    private final Context context;
    private final FlowLayout flowLayout;
    private final List<T> data = new ArrayList<>();
    private OnSelectedItemListener<T> onSelectedItemListener;
    private OnItemClickListener<T> onItemClickListener;
    private int maxSelectedCount = -1;//最大能选中的数量 -1：无限制
    private TagView lastCheckedTagView;
    private final ArrayMap<Integer, T> selectedPosition = new ArrayMap<>();

    public BasicTagAdapter(@NonNull FlowLayout flowLayout) {
        this.flowLayout = flowLayout;
        this.context = flowLayout.getContext();
    }

    /**
     * 获取数据列表
     */
    public List<T> getData() {
        return data;
    }

    /**
     * 批量添加数据
     */
    public void addData(List<T> dataList) {
        this.data.addAll(dataList);
    }

    /**
     * 添加一条数据
     */
    public void addData(T item) {
        this.data.add(item);
    }

    /**
     * 获取数据数量
     */
    public int getCount() {
        return data.size();
    }

    /**
     * 根据position获取item
     */
    public T getItem(int position) {
        return data.get(position);
    }

    /**
     * 获取当前已被选中的item数量
     */
    public int getSelectedCount() {
        return selectedPosition.size();
    }

    /**
     * 装载列表数据
     *
     * @param data              列表数据
     * @param isPreClearOldData 是否先清空老数据再装载新数据
     */
    public void completeLoad(List<T> data, boolean isPreClearOldData) {
        if ((data == null || data.size() == 0) && isPreClearOldData) {
            this.data.clear();
            notifyDataSetChanged();
            return;
        } else if (data == null) {
            return;
        }
        if (isPreClearOldData) {
            this.data.clear();
        }
        this.addData(data);
        notifyDataSetChanged();
    }

    /**
     * 装载列表数据
     *
     * @param data              单个数据
     * @param isPreClearOldData 是否清空老列表数据
     */
    @SuppressLint("NotifyDataSetChanged")
    public void completeLoadOne(T data, boolean isPreClearOldData) {
        if ((data == null) && isPreClearOldData) {
            this.data.clear();
            notifyDataSetChanged();
            return;
        } else if (data == null) {
            return;
        }
        if (isPreClearOldData) {
            this.data.clear();
        }
        this.addData(data);
        notifyDataSetChanged();
    }

    /**
     * 设置item选中监听事件
     */
    public void setOnSelectedItemListener(OnSelectedItemListener<T> onSelectedItemListener) {
        this.onSelectedItemListener = onSelectedItemListener;
    }

    /**
     * 设置item点击监听事件
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置最大可以选中的数量（-1无限制）
     */
    public void setMaxSelectedCount(int maxSelectedCount) {
        this.maxSelectedCount = maxSelectedCount;
    }

    /**
     * 获取已选中的item列表数据
     */
    public List<T> getSelectedItemList() {
        return new ArrayList<>(selectedPosition.values());
    }

    /**
     * 获取第一个被选中的item
     */
    public T getOneSelectedItem() {
        return selectedPosition.size() > 0 ? selectedPosition.valueAt(0) : null;
    }

    /**
     * 清除已经选择的item
     */
    public void clearCheckedItem() {
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            Object object = flowLayout.getChildAt(i);
            if (object instanceof TagView) {
                ((TagView) object).setChecked(false);
            }
        }
        selectedPosition.clear();
    }

    /**
     * 通知数据发生改变刷新UI
     */
    public void notifyDataSetChanged() {
        flowLayout.removeAllViews();

        for (int i = 0, size = this.getCount(); i < size; i++) {
            final T itemInfo = this.getItem(i);
            final TagView tagView = this.getTagView(getContext());
            final int finalPos = i;
            tagView.setPosition(i);

            //处理选中事件
            tagView.getCompoundButton().setOnCheckedChangeListener((buttonView, isChecked)
                    -> handleOnCheckedChangeListener(isChecked, finalPos, tagView, itemInfo));

            //处理点击事件
            tagView.setOnClickListener(v -> handleOnClickListener(tagView));

            //设置数据到UI
            this.convert(tagView, itemInfo);

            //添加tagView到FlowLayout
            flowLayout.addView(tagView);
        }

        //重新排列视图
//        flowLayout.relayoutToCompress();
    }

    /**
     * 处理选项选择变化事件
     *
     * @param isChecked 是否被选中
     * @param position  选中位置
     * @param tagView   tagView
     * @param itemValue item数据
     */
    private void handleOnCheckedChangeListener(boolean isChecked, int position, TagView tagView, T itemValue) {
        if (!isChecked) {
            selectedPosition.remove(position);
            tagView.performChangeCheckCallback();
        } else if (maxSelectedCount == 1) {
            //单选
            if (lastCheckedTagView != null) {
                selectedPosition.remove(lastCheckedTagView.getPosition());
            }
            selectedPosition.put(position, itemValue);
            lastCheckedTagView = tagView;
            this.invokeOnSelectedListener(tagView);
            tagView.performChangeCheckCallback();
        } else if (maxSelectedCount == -1 || selectedPosition.size() < maxSelectedCount) {
            selectedPosition.put(position, itemValue);
            this.invokeOnSelectedListener(tagView);
            tagView.performChangeCheckCallback();
        }
    }

    /**
     * 处理点击事件
     *
     * @param tagView tagView
     */
    private void handleOnClickListener(TagView tagView) {
        this.invokeOnItemClickListener(tagView);
        if (tagView.isChecked()) {
            if (maxSelectedCount == 1 && selectedPosition.size() == 1) {
                return;
            }
            tagView.setChecked(false);
        } else if (maxSelectedCount == 1) {
            //单选
            if (lastCheckedTagView != null) {
                lastCheckedTagView.setChecked(false);
            }
            tagView.setChecked(true);
        } else if (maxSelectedCount == -1 || selectedPosition.size() < maxSelectedCount) {
            if (lastCheckedTagView != null) {
                lastCheckedTagView.setChecked(false);
            }
            tagView.setChecked(true);
        }
    }

    /**
     * 调用item选中监听事件回调
     */
    private void invokeOnSelectedListener(TagView tagView) {
        if (onSelectedItemListener != null) {
            onSelectedItemListener.onSelected(selectedPosition.get(tagView.getPosition()));
        }
    }

    /**
     * 调用item点击事件回调
     */
    private void invokeOnItemClickListener(TagView tagView) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(this.getItem(tagView.getPosition()), tagView);
        }
    }

    public Context getContext() {
        return context;
    }

    /**
     * item选中监听事件
     */
    public interface OnSelectedItemListener<T> {
        void onSelected(T item);
    }

    /**
     * item点击监听事件
     */
    public interface OnItemClickListener<T> {
        void onItemClick(T item, View view);
    }

    /**
     * 获取itemView
     */
    protected abstract TagView getTagView(Context context);

    /**
     * 设置视图显示数据
     *
     * @param itemInfo item数据
     */
    protected abstract void convert(TagView tagView, T itemInfo);
}
