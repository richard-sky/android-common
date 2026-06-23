package com.richard.library.basic.basic.adapter;

import android.annotation.SuppressLint;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;

import com.richard.library.context.util.DensityUtilKt;
import com.richard.library.basic.widget.flowlayout.TagView;
import com.richard.library.basic.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Description : 默认的标签选择列表适配
 * Author : admin-richard
 * Date : 2019-06-20 10:50
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-20 10:50      admin-richard         new file.
 * </pre>
 */
public class DefaultTagSelectAdapter<T> extends BasicAdapter<T> {

    //key:item下标、value:item项
    private final SimpleArrayMap<Integer, T> currentSelectItem = new SimpleArrayMap<>();
    private int maxSelectCount = 1;//支持的最大选择的数量（默认最大选择1）
    private int lastSelectedPosition = -1;//上一次选择的item位置
    private float itemTextSize;//item项字体大小

    //item项上下左右内边距
    private int itemPaddingTop;
    private int itemPaddingBottom;
    private int itemPaddingLeft;
    private int itemPaddingRight;

    //item项宽高
    private int itemWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    private int itemHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    //是否至少必选一个
    private boolean isRequiredSelectedOne;
    private Callback<T> mCallback;
    private boolean isEnable = true;//是否可用

    //避免频繁创建对象，在调用getSelectedItem方法时不必每次都创建该对象
    private final List<T> selectedItemResultList = new ArrayList<>();


    public DefaultTagSelectAdapter() {
        int padding = DensityUtilKt.dp2px(10);
        this.itemPaddingTop = padding;
        this.itemPaddingBottom = padding;
        this.itemPaddingLeft = padding;
        this.itemPaddingRight = padding;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_select_tag;
    }

    @Override
    protected void convert(BasicViewHolder holder, T itemInfo, int position) {
        //item项内容自适应设置
        ViewGroup.LayoutParams lp = holder.getRootView().getLayoutParams();
        if (itemWidth != lp.width) {
            lp.width = itemWidth;
            holder.getRootView().setLayoutParams(lp);
        }

        if (itemHeight != lp.height) {
            lp.height = itemHeight;
            holder.getRootView().setLayoutParams(lp);
        }

        TagView tagView = holder.getView(R.id.tag_view);
        tagView.setEnabled(isEnable);
        ViewGroup.LayoutParams buttonLp = tagView.getCompoundButton().getLayoutParams();
        switch (itemWidth) {
            case ViewGroup.LayoutParams.MATCH_PARENT:
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                if (buttonLp.width != itemWidth) {
                    buttonLp.width = itemWidth;
                    tagView.getCompoundButton().setLayoutParams(buttonLp);
                }
                break;
        }

        //item项布局内边距设置
        if (itemPaddingLeft >= 0 && itemPaddingLeft != tagView.getCompoundButton().getPaddingLeft()
                || itemPaddingTop >= 0 && itemPaddingTop != tagView.getCompoundButton().getPaddingTop()
                || itemPaddingRight >= 0 && itemPaddingRight != tagView.getCompoundButton().getPaddingRight()
                || itemPaddingBottom >= 0 && itemPaddingBottom != tagView.getCompoundButton().getPaddingBottom()
        ) {
            tagView.getCompoundButton().setPadding(
                    itemPaddingLeft >= 0 ? itemPaddingLeft : tagView.getCompoundButton().getPaddingLeft()
                    , itemPaddingTop >= 0 ? itemPaddingTop : tagView.getCompoundButton().getPaddingTop()
                    , itemPaddingRight >= 0 ? itemPaddingRight : tagView.getCompoundButton().getPaddingRight()
                    , itemPaddingBottom >= 0 ? itemPaddingBottom : tagView.getCompoundButton().getPaddingBottom()
            );
        }

        if (itemTextSize > 0) {
            tagView.getCompoundButton().setTextSize(TypedValue.COMPLEX_UNIT_SP, itemTextSize);
        }
        tagView.setChecked(currentSelectItem.containsKey(position));
        tagView.getCompoundButton().setText(this.getItemValue(itemInfo));
        holder.setOnClickListener(R.id.tag_view, v -> handleCheckItem(
                position
                , itemInfo
                , tagView.isChecked()
                , true
        ));
    }

    /**
     * 获取item Value
     */
    protected SpannableStringBuilder getItemValue(T itemInfo) {
        return new SpannableStringBuilder(itemInfo != null ? String.valueOf(itemInfo) : "");
    }


    /**
     * 处理选中item
     *
     * @param position   item下标位置
     * @param itemInfo   item数据
     * @param isChecked  原始选中状态
     * @param isCallback 是否回调被选中之后的回调方法
     * @return 是否处理成功
     */
    @SuppressLint("NotifyDataSetChanged")
    protected boolean handleCheckItem(int position, T itemInfo, boolean isChecked, boolean isCallback) {
        if (isRequiredSelectedOne && currentSelectItem.size() == 1 && isChecked) {
            return false;
        }

        if (maxSelectCount > 1 && currentSelectItem.size() >= maxSelectCount && !isChecked) {
            if (mCallback != null && isCallback) {
                mCallback.onSelectedMax();
            }
            return false;
        }

        if (maxSelectCount == 1 && lastSelectedPosition != position) {
            currentSelectItem.remove(lastSelectedPosition);
        }

        if (!isChecked) {
            currentSelectItem.put(position, itemInfo);
        } else {
            currentSelectItem.remove(position);
        }

        if (mCallback != null && isCallback) {
            mCallback.onChangeSelect(this.getSelectedItem());
        }
        lastSelectedPosition = position;

        if (isCallback) {
            super.notifyDataSetChanged();
        }

        return true;
    }

    /**
     * 设置item项View是否自适应内容宽度
     *
     * @param widthWrapContent true:自适应内容的宽度，false：宽度占满父布局的宽度
     */
    public void setWidthWrapContent(boolean widthWrapContent) {
        this.setItemLayoutParams(
                widthWrapContent ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT
                , this.itemHeight
        );
    }

    /**
     * 设置item项布局宽高
     *
     * @param itemWidth  必填 item项宽 ViewGroup.LayoutParams.WRAP_CONTENT | ViewGroup.LayoutParams.MATCH_PARENT | 具体宽度值px
     * @param itemHeight 必填 item项高 ViewGroup.LayoutParams.WRAP_CONTENT | ViewGroup.LayoutParams.MATCH_PARENT | 具体高度值px
     */
    public void setItemLayoutParams(int itemWidth, int itemHeight) {
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;

        if (itemWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            this.itemPaddingLeft = 0;
            this.itemPaddingRight = 0;
        }

        if (itemHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            this.itemPaddingTop = 0;
            this.itemPaddingBottom = 0;
        }
    }

    /**
     * 设置item项上内边距
     *
     * @param itemPaddingTop 单位：px
     */
    public void setItemPaddingTop(int itemPaddingTop) {
        this.itemPaddingTop = itemPaddingTop;
    }

    /**
     * 设置item项下内边距
     *
     * @param itemPaddingBottom 单位：px
     */
    public void setItemPaddingBottom(int itemPaddingBottom) {
        this.itemPaddingBottom = itemPaddingBottom;
    }

    /**
     * 设置item项左内边距
     *
     * @param itemPaddingLeft 单位：px
     */
    public void setItemPaddingLeft(int itemPaddingLeft) {
        this.itemPaddingLeft = itemPaddingLeft;
    }

    /**
     * 设置item项右内边距
     *
     * @param itemPaddingRight 单位：px
     */
    public void setItemPaddingRight(int itemPaddingRight) {
        this.itemPaddingRight = itemPaddingRight;
    }

    /**
     * 设置item项字体大小
     *
     * @param itemTextSize 单位：sp
     */
    public void setItemTextSize(float itemTextSize) {
        this.itemTextSize = itemTextSize;
    }

    /**
     * 获取已经选中了的item
     */
    public List<T> getSelectedItem() {
        selectedItemResultList.clear();
        int key;
        for (int i = 0; i < currentSelectItem.size(); i++) {
            key = currentSelectItem.keyAt(i);
            selectedItemResultList.add(currentSelectItem.get(key));
        }
        return selectedItemResultList;
    }

    /**
     * 获取一个已选中的item
     */
    public T getOneSelectedItem() {
        if (currentSelectItem.size() <= 0) {
            return null;
        }
        return currentSelectItem.valueAt(0);
    }

    /**
     * 获取第一个已选中的item位置
     */
    public int getFirstSelectedPosition() {
        if (currentSelectItem.size() <= 0) {
            return -1;
        }
        return currentSelectItem.keyAt(0);
    }

    /**
     * 获取已选中项数量
     */
    public int getSelectedCount() {
        return currentSelectItem.size();
    }

    /**
     * 当前下标的item是否被选中
     *
     * @param position 下标
     */
    public boolean isSelectedItem(int position) {
        return currentSelectItem.containsKey(position);
    }


    /**
     * 清空所有已选择的item项，若当前处于必须至少选中一个的情况下，则保留第一项为选中项，其它全部重置为取消选中状态
     *
     * @param isCallback 是否回调被选中之后的回调方法
     */
    @SuppressLint("NotifyDataSetChanged")
    public void clearSelectedItem(boolean isCallback) {
        this.clearAllSelectedFiled();
        if (isRequiredSelectedOne && getItemCount() > 0) {
            lastSelectedPosition = 0;
            currentSelectItem.put(0, getData().get(0));
        }
        this.notifyDataSetChanged();
        if (mCallback != null && isCallback) {
            mCallback.onChangeSelect(this.getSelectedItem());
        }
    }

    /**
     * 清除所有已选择的item记录字段变量存储
     */
    private void clearAllSelectedFiled() {
        currentSelectItem.clear();
        lastSelectedPosition = -1;
    }

    /**
     * 设置被选中项
     */
    public void setSelectedItem(int position) {
        this.setSelectedItem(position, true);
    }

    /**
     * 设置被选中项
     *
     * @param position   设置被选中项下标位置
     * @param isCallback 是否回调选中回调方法
     */
    public void setSelectedItem(int position, boolean isCallback) {
        if (position < 0 || position >= super.getItemCount()) {
            return;
        }

        if (lastSelectedPosition == position) {
            lastSelectedPosition = -1;
        }
        this.handleCheckItem(position, super.getData().get(position), false, isCallback);
    }

    /**
     * 设置被选中项
     *
     * @param selectItemList        设置被选中项对象列表
     * @param isPreClearAllSelected 是否先清除之前所有已选择的项
     * @param isCallback            是否回调选中回调方法
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setXSelectItem(@NonNull List<T> selectItemList, boolean isPreClearAllSelected, boolean isCallback) {
        if (selectItemList == null || selectItemList.isEmpty()) {
            return;
        }

        if (isPreClearAllSelected) {
            this.clearAllSelectedFiled();
        }

        for (T selectItem : selectItemList) {
            this.setSelectedItem(super.getData().indexOf(selectItem), false);
        }
        if (mCallback != null && isCallback) {
            mCallback.onChangeSelect(this.getSelectedItem());
        }
        this.notifyDataSetChanged();
    }


    /**
     * 设置支持的最大选择的数量
     */
    public void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    /**
     * 是否至少必选一个
     */
    public void setRequiredSelectedOne(boolean requiredSelectedOne) {
        isRequiredSelectedOne = requiredSelectedOne;
    }

    /**
     * 是否可用
     */
    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void setCallback(Callback<T> callback) {
        mCallback = callback;
    }

    public interface Callback<T> {

        /**
         * 选择发生变化时回调
         *
         * @param selectedItem 已选中的item项
         */
        void onChangeSelect(List<T> selectedItem);

        /**
         * 选择的数量已达上限
         */
        default void onSelectedMax() {
        }
    }
}
