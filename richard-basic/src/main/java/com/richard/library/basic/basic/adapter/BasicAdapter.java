package com.richard.library.basic.basic.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.richard.library.basic.basic.adapter.listener.OnItemClickListener;
import com.richard.library.basic.basic.adapter.listener.OnItemLongClickListener;
import com.richard.library.basic.util.FastClickCheck;
import com.richard.library.basic.widget.list.RecyclerViewHeaderFooter;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Description : BaseRecyclerViewAdapter
 * Author : admin-richard
 * Date : 2015/4/28 14:40
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2015/4/28 14:40      admin-richard         new file.
 * </pre>
 */
public abstract class BasicAdapter<T> extends RecyclerView.Adapter<BasicViewHolder> {

    private Context context;
    private RecyclerViewHeaderFooter recyclerViewHeaderFooter;
    private List<T> data = new ArrayList<>();
    private ItemTouchHelperCallback itemTouchHelperCallback;
    protected OnItemClickListener<T> onItemClickListener;
    protected OnItemLongClickListener<T> onItemLongClickListener;
    private boolean enabledDragSort;//是否启用拖动排序
    private boolean enabledSlideDelete;//是否启用侧滑删除
    private List<Integer> pinnedHeaderType;//不为null并且不为empty代表启用列可以跨行占满显示（主要是配合RecyclerView pinnedHeader显示）
    private int layoutId;
    private View layoutView;

    public BasicAdapter() {

    }

    public BasicAdapter(List<T> data) {
        super();
        this.completeLoad(data, true);
    }

    public BasicAdapter(@LayoutRes int layoutId, List<T> data) {
        super();
        this.layoutId = layoutId;
        this.completeLoad(data, true);
    }

    public BasicAdapter(View layoutView, List<T> data) {
        super();
        this.layoutView = layoutView;
        this.completeLoad(data, true);
    }

    /**
     * 添加itemViewType
     */
    public void addItemViewType(int itemViewType) {
        if (pinnedHeaderType == null) {
            pinnedHeaderType = new ArrayList<>();
        }
        pinnedHeaderType.add(itemViewType);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public BasicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }

        View itemLayoutView = this.getItemLayoutView(parent, viewType);
        if (itemLayoutView != null) {
            return new BasicViewHolder(itemLayoutView);
        }
        return new BasicViewHolder(LayoutInflater.from(context).inflate(getItemLayoutId(viewType), parent, false));
    }


    @Override
    public void onBindViewHolder(BasicViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.isUserTriggerEvent = false;
        final T itemInfo = data.get(position);
        this.convert(holder, itemInfo, position);

        if (onItemClickListener != null) {
            holder.getRootView().setOnClickListener(view -> {
                FastClickCheck.check(view);
                onItemClickListener.onItemClick(itemInfo, position);
            });
        }

        if (onItemLongClickListener != null) {
            holder.getRootView().setOnLongClickListener(v -> {
                onItemLongClickListener.onItemLongClick(itemInfo, position);
                return true;
            });
        }

        holder.isUserTriggerEvent = true;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        if (recyclerView instanceof RecyclerViewHeaderFooter) {
            recyclerViewHeaderFooter = (RecyclerViewHeaderFooter) recyclerView;
        }

        //启用拖动排序
        if (enabledDragSort) {
            this.initItemTouchHelper(recyclerView);
            itemTouchHelperCallback.setDragSort(true);
        }

        //启用侧滑删除
        if (enabledSlideDelete) {
            this.initItemTouchHelper(recyclerView);
            itemTouchHelperCallback.setSlideDelete(true);
        }

        //启用列可以跨行显示
        if (pinnedHeaderType != null && !pinnedHeaderType.isEmpty()) {
            // 如果是网格布局，这里处理标签的布局占满一行
            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                final GridLayoutManager.SpanSizeLookup oldSizeLookup = gridLayoutManager.getSpanSizeLookup();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (pinnedHeaderType.contains(getItemViewType(position))) {
                            return gridLayoutManager.getSpanCount();
                        }
                        if (oldSizeLookup != null) {
                            return oldSizeLookup.getSpanSize(position);
                        }
                        return 1;
                    }
                });
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BasicViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        //启用列可以跨行显示
        if (pinnedHeaderType != null && !pinnedHeaderType.isEmpty()) {
            // 如果是瀑布流布局，这里处理标签的布局占满一行
            final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                final StaggeredGridLayoutManager.LayoutParams slp = (StaggeredGridLayoutManager.LayoutParams) lp;
                slp.setFullSpan(pinnedHeaderType.contains(getItemViewType(holder.getLayoutPosition())));
            }
        }
    }

    /**
     * 初始化ItemTouchHelper
     *
     * @param recyclerView 启用的RecyclerView
     */
    private void initItemTouchHelper(RecyclerView recyclerView) {
        if (itemTouchHelperCallback == null) {
            itemTouchHelperCallback = new ItemTouchHelperCallback(this, data);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    /**
     * 获取PinnedHeaderType列表
     */
    public List<Integer> getPinnedHeaderType() {
        return pinnedHeaderType;
    }

    public Context getContext() {
        return context;
    }

    public List<T> getData() {
        return data;
    }

    public void addData(T dataItem) {
        this.data.add(dataItem);
    }

    public void addData(List<T> dataList) {
        this.data.addAll(dataList);
    }

    /**
     * 直接设置数据源list
     *
     * @param data 数据源
     */
    public void setData(List<T> data) {
        if (data == null) {
            return;
        }
        this.data = data;
    }

    /**
     * 装载列表数据
     */
    public void completeLoad(List<T> data) {
        this.completeLoad(data, true);
    }

    /**
     * 装载列表数据
     *
     * @param data               数据列表
     * @param isClearOldListData 是否清空老列表数据
     */
    @SuppressLint("NotifyDataSetChanged")
    public void completeLoad(List<T> data, boolean isClearOldListData) {
        if ((data == null || data.isEmpty()) && isClearOldListData) {
            this.data.clear();
            super.notifyDataSetChanged();
            return;
        } else if (data == null) {
            return;
        }

        if (isClearOldListData) {
            this.data.clear();
            this.addData(data);
            this.notifyDataSetChanged();
        } else {
            int positionStart = recyclerViewHeaderFooter != null
                    ? recyclerViewHeaderFooter.getHeadersCount() + getItemCount()
                    : getItemCount();
            this.addData(data);
            this.notifyItemRangeInserted(positionStart, data.size());
        }
    }

    /**
     * 装载列表数据
     *
     * @param data               单个数据
     * @param isClearOldListData 是否清空老列表数据
     */
    @SuppressLint("NotifyDataSetChanged")
    public void completeLoadOne(T data, boolean isClearOldListData) {
        if ((data == null) && isClearOldListData) {
            this.data.clear();
            notifyDataSetChanged();
            return;
        } else if (data == null) {
            return;
        }
        if (isClearOldListData) {
            this.data.clear();
            this.addData(data);
            this.notifyDataSetChanged();
        } else {
            int positionStart = recyclerViewHeaderFooter != null
                    ? recyclerViewHeaderFooter.getHeadersCount() + getItemCount()
                    : getItemCount();
            this.addData(data);
            this.notifyItemInserted(positionStart + 1);
        }
    }

    /**
     * 设置Item点击事件回调
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置Item长按事件回调
     */
    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 设置是否启用拖动排序
     */
    public void setEnabledDragSort(boolean enabledDragSort) {
        this.enabledDragSort = enabledDragSort;
        if (itemTouchHelperCallback != null) {
            itemTouchHelperCallback.setDragSort(this.enabledDragSort);
        }
    }

    /**
     * 设置是否启用侧滑删除
     */
    public void setEnabledSlideDelete(boolean enabledSlideDelete) {
        this.enabledSlideDelete = enabledSlideDelete;
        if (itemTouchHelperCallback != null) {
            itemTouchHelperCallback.setSlideDelete(this.enabledSlideDelete);
        }
    }

    protected int getItemLayoutId(int viewType) {
        return layoutId;
    }

    protected View getItemLayoutView(ViewGroup parent, int viewType) {
        return layoutView;
    }

    protected abstract void convert(BasicViewHolder holder, T itemInfo, int position);
}
