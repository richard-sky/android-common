package com.richard.library.basic.widget.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.richard.library.basic.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * Description : 可添加header和footer、分割线，可设置emptyView的RecyclerView
 * Author : admin-richard
 * Date : 2017/9/22 14:29
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/9/22 14:29     admin-richard         new file.
 * </pre>
 * <p>
 * 注：调用了属性设置方法，最后调用notifyAttrChanged方法才能生效
 */
public class SRecyclerView extends RecyclerView implements RecyclerViewHeaderFooter {

    //headers viewType，取值较大，避免跟数据区域的viewType重复，如有重复则需调整
    private static final int VIEW_TYPE_HEADER_INIT = 100001;

    //footers viewType
    private static final int VIEW_TYPE_FOOTER_INIT = 200001;

    //空数据时占位图
    private View emptyView;

    //当没有更多数据时，是否显示底部到底的视图
    public boolean isShowStateNoMoreView = false;

    //header和footer存储
    private final SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private final SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    //其它
    private ItemDecoration currentItemDecoration;
    private WrapAdapter mWrapAdapter;
    private final AdapterDataObserver mAdapterDataObserver = new DataObserver(this);
    private List<OnScrollListener> onScrollListenerList;

    //属性定义
    private int dividerColor = ContextCompat.getColor(getContext(), R.color.transparent);
    private int dividerHorizontalSize = 0;
    private int dividerVerticalSize = 0;
    private boolean dividerShowFirst = false;
    private boolean dividerShowLast = true;
    private Drawable dividerDrawable = null;
    private int srvType = 0;
    private int srvOrientation = RecyclerView.VERTICAL;
    private int srvColumn = 1;
    private boolean defaultAnimatorOpen = true;
    private boolean enabledPinnedHeader = false;
    private boolean disEnableEvent = false;


    public SRecyclerView(Context context) {
        this(context, null);
    }

    public SRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(attrs);
    }


    private void init(AttributeSet attrs) {
        if (attrs == null) {
            this.notifyAttrChanged();
            this.setDefaultAnimatorOpen(defaultAnimatorOpen);
            return;
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SRecyclerView);
        dividerColor = typedArray.getColor(R.styleable.SRecyclerView_srv_divider_color, ContextCompat.getColor(getContext(), R.color.transparent));

        //若设置srv_divider_size值，则以该值为准
        int srv_divider_size = typedArray.getDimensionPixelSize(R.styleable.SRecyclerView_srv_divider_size, 0);
        if (srv_divider_size > 0) {
            dividerHorizontalSize = srv_divider_size;
            dividerVerticalSize = srv_divider_size;
        } else {
            dividerHorizontalSize = typedArray.getDimensionPixelSize(R.styleable.SRecyclerView_srv_divider_horizontal_size, 0);
            dividerVerticalSize = typedArray.getDimensionPixelSize(R.styleable.SRecyclerView_srv_divider_vertical_size, 0);
        }

        dividerDrawable = typedArray.getDrawable(R.styleable.SRecyclerView_srv_divider_drawable);
        dividerShowFirst = typedArray.getBoolean(R.styleable.SRecyclerView_srv_divider_show_first, false);
        dividerShowLast = typedArray.getBoolean(R.styleable.SRecyclerView_srv_divider_show_last, true);
        srvType = typedArray.getInt(R.styleable.SRecyclerView_srv_type, 0);
        srvOrientation = typedArray.getInt(R.styleable.SRecyclerView_srv_orientation, RecyclerView.VERTICAL);
        srvColumn = typedArray.getInt(R.styleable.SRecyclerView_srv_column, 1);
        defaultAnimatorOpen = typedArray.getBoolean(R.styleable.SRecyclerView_srv_default_animator_open, true);
        enabledPinnedHeader = typedArray.getBoolean(R.styleable.SRecyclerView_srv_enabled_pinned_header, false);
        disEnableEvent = typedArray.getBoolean(R.styleable.SRecyclerView_srv_disEnableEvent, false);
        super.setHasFixedSize(typedArray.getBoolean(R.styleable.SRecyclerView_srv_hasFixedSize, false));

        typedArray.recycle();
        this.notifyAttrChanged();
        this.setDefaultAnimatorOpen(defaultAnimatorOpen);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (disEnableEvent) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (onScrollListenerList != null) {
            for (OnScrollListener item : onScrollListenerList) {
                this.removeOnScrollListener(item);
            }
            onScrollListenerList.clear();
            onScrollListenerList = null;
        }
        super.onDetachedFromWindow();
    }

    /**
     * 设置滑动监听事件
     */
    public void addXScrollEvent(OnScrollListener onScrollListener) {
        if (onScrollListener == null) {
            return;
        }
        if (onScrollListenerList == null) {
            onScrollListenerList = new ArrayList<>();
        }
        onScrollListenerList.add(onScrollListener);
        this.addOnScrollListener(onScrollListener);
    }

    /**
     * 添加头部视图
     */
    public View addHeaderView(@LayoutRes int layoutId, ViewGroup.LayoutParams layoutParams) {
        View headerView = LayoutInflater.from(getContext()).inflate(layoutId, null);
        return this.addHeaderView(headerView, layoutParams);
    }

    /**
     * 添加头部视图
     */
    public View addHeaderView(@NonNull View headerView) {
        return this.addHeaderView(headerView, null);
    }

    /**
     * 添加头部视图
     */
    @SuppressLint("NotifyDataSetChanged")
    public View addHeaderView(@NonNull View headerView, ViewGroup.LayoutParams layoutParams) {
        if (layoutParams != null) {
            headerView.setLayoutParams(layoutParams);
        }
        mHeaderViews.put(VIEW_TYPE_HEADER_INIT + mHeaderViews.size(), headerView);
        if (mWrapAdapter != null && mWrapAdapter.getInnerAdapter() != null) {
            mWrapAdapter.getInnerAdapter().notifyDataSetChanged();
//            mWrapAdapter.getInnerAdapter().notifyItemInserted(mHeaderViews.size());
        }
        return headerView;
    }

    /**
     * 添加头部视图
     */
    public View addFooterView(@LayoutRes int layoutId, ViewGroup.LayoutParams layoutParams) {
        View footerView = LayoutInflater.from(getContext()).inflate(layoutId, null);
        return this.addFooterView(footerView, layoutParams);
    }

    /**
     * 添加头部视图
     */
    public View addFooterView(@NonNull View headerView) {
        return this.addFooterView(headerView, null);
    }

    /**
     * 添加底部视图
     */
    @SuppressLint("NotifyDataSetChanged")
    public View addFooterView(@NonNull View footerView, ViewGroup.LayoutParams layoutParams) {
        if (layoutParams != null) {
            footerView.setLayoutParams(layoutParams);
        }

        mFooterViews.put(VIEW_TYPE_FOOTER_INIT + mFooterViews.size(), footerView);
        if (mWrapAdapter != null && mWrapAdapter.getInnerAdapter() != null) {
            // 最后一个position = mWrapAdapter.getItemCount() - 1，
            // 新增一个FooterView的position = mWrapAdapter.getItemCount()
            mWrapAdapter.getInnerAdapter().notifyDataSetChanged();
//            mWrapAdapter.getInnerAdapter().notifyItemInserted(mWrapAdapter.getItemCount());
        }

        return footerView;
    }

    /**
     * 移除头部视图
     */
    @SuppressLint("NotifyDataSetChanged")
    public void removeHeaderView(@NonNull View headerView) {
        for (int i = 0; i < mHeaderViews.size(); i++) {
            if (headerView.equals(mHeaderViews.valueAt(i))) {
                mHeaderViews.removeAt(i);
                if (mWrapAdapter != null && mWrapAdapter.getInnerAdapter() != null) {
                    mWrapAdapter.getInnerAdapter().notifyDataSetChanged();
//                mWrapAdapter.getInnerAdapter().notifyItemRemoved(i);
                }
                break;
            }
        }
    }

    /**
     * 获取item 数量（含header和footer）
     */
    public int getItemCount() {
        if (getAdapter() == null) {
            return this.getHeadersCount() + this.getFootersCount();
        }
        return getAdapter().getItemCount() + this.getHeadersCount() + this.getFootersCount();
    }

    /**
     * 设置是否禁用列表内全部事件
     */
    public void setDisEnableEvent(boolean disEnableEvent) {
        this.disEnableEvent = disEnableEvent;
    }

    /**
     * 滑动到底部
     */
    public void smoothScrollToBottom() {
        super.smoothScrollToPosition(Math.max(this.getItemCount() - 1, 0));
    }

    /**
     * 滑动到顶部
     */
    public void smoothScrollToTop() {
        super.smoothScrollToPosition(0);
    }

    /**
     * 滚动到指定位置
     *
     * @param position 元素下标位置
     * @param offset   偏移量像素
     */
    public void scrollToPositionWithOffset(int position, int offset) {
        if (getLayoutManager() == null) {
            return;
        }
        if (getLayoutManager() instanceof LinearLayoutManager) {
            ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(position, offset);
        }
        if (getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager) getLayoutManager()).scrollToPositionWithOffset(position, offset);
        }
        if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) getLayoutManager()).scrollToPositionWithOffset(position, offset);
        }
    }

    /**
     * 设置分割线颜色
     */
    public void setDividerColor(@ColorInt int color) {
        this.dividerColor = color;
    }

    /**
     * 设置分割线大小
     *
     * @param size 单位：px
     */
    public void setDividerSize(int size) {
        this.dividerVerticalSize = size;
        this.dividerHorizontalSize = size;
    }

    /**
     * 设置纵向分割线大小
     */
    public void setVerticalDividerSize(int size) {
        this.dividerVerticalSize = size;
    }

    /**
     * 设置横向分割线大小
     */
    public void setHorizontalDividerSize(int size) {
        this.dividerHorizontalSize = size;
    }

    /**
     * 设置分割drawable
     */
    public void setDividerDrawable(Drawable drawable) {
        this.dividerDrawable = drawable;
    }

    /**
     * 设置布局类型
     *
     * @param layoutType 0：LinearLayoutManager、1:GridLayoutManager、2:StaggerLayoutManager
     */
    public void setLayoutType(int layoutType) {
        this.srvType = layoutType;
    }

    /**
     * 设置滑动方向
     *
     * @param orientation 0：横向滑动、1：竖向滑动
     */
    public void setScrollDirection(int orientation) {
        this.srvOrientation = orientation;
    }

    /**
     * 设置第一个item前面是否显示分隔线
     */
    public void setDividerShowFirst(boolean dividerShowFirst) {
        this.dividerShowFirst = dividerShowFirst;
    }

    /**
     * 设置最后一个item后面是否显示分割线
     */
    public void setDividerShowLast(boolean dividerShowLast) {
        this.dividerShowLast = dividerShowLast;
    }

    /**
     * 设置显示列数
     *
     * @param column 仅srv_type为grid，stagger时有效
     */
    public void setColumn(int column) {
        this.srvColumn = column;
    }

    /**
     * 获取当前显示列数
     */
    public int getColumn() {
        return this.srvColumn;
    }

    /**
     * 设置是否开启默认动画
     */
    public void setDefaultAnimatorOpen(boolean isOpen) {
        this.defaultAnimatorOpen = isOpen;
        ItemAnimator itemAnimator = this.getItemAnimator();
        if (itemAnimator != null) {
            this.getItemAnimator().setAddDuration(isOpen ? 120 : 0);
            this.getItemAnimator().setChangeDuration(isOpen ? 250 : 0);
            this.getItemAnimator().setMoveDuration(isOpen ? 250 : 0);
            this.getItemAnimator().setRemoveDuration(isOpen ? 120 : 0);
            ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(isOpen);
        }
    }

    /**
     * 通知设置的相关属性生效
     */
    public void notifyAttrChanged() {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        switch (srvType) {
            case 1:
                //表格布局
                if (layoutManager instanceof GridLayoutManager) {
                    ((GridLayoutManager) getLayoutManager()).setSpanCount(srvColumn);
                    ((GridLayoutManager) getLayoutManager()).setOrientation(srvOrientation);
                    break;
                }
                layoutManager = new GridLayoutManager(getContext(), srvColumn, srvOrientation, false);
                break;
            case 2:
                //瀑布流布局
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    ((StaggeredGridLayoutManager) getLayoutManager()).setSpanCount(srvColumn);
                    ((StaggeredGridLayoutManager) getLayoutManager()).setOrientation(srvOrientation);
                    break;
                }
                layoutManager = new StaggeredGridLayoutManager(srvColumn, srvOrientation);
                break;
            default:
                //线性布局
                if (layoutManager instanceof LinearLayoutManager
                        && (layoutManager.getClass() == LinearLayoutManager.class
                        || layoutManager.getClass() == WrapContentLinearLayoutManager.class)) {
                    ((LinearLayoutManager) getLayoutManager()).setOrientation(srvOrientation);
                    break;
                }
                layoutManager = new WrapContentLinearLayoutManager(getContext(), srvOrientation, false);
        }

        this.setLayoutManager(layoutManager);

        if (enabledPinnedHeader || dividerDrawable != null || dividerVerticalSize > 0 || dividerHorizontalSize > 0) {
            DividerDecoration.Builder itemDecoration;

            if (enabledPinnedHeader) {
                itemDecoration = new PinnedHeaderDecoration.Builder(getContext());
            } else {
                itemDecoration = new DividerDecoration.Builder(getContext());
            }

            if (layoutManager instanceof GridLayoutManager
                    || layoutManager instanceof StaggeredGridLayoutManager) {
                itemDecoration.setVerticalSpan((float) dividerVerticalSize);
                itemDecoration.setHorizontalSpan((float) dividerHorizontalSize);
            } else {
                if (srvOrientation == RecyclerView.HORIZONTAL) {
                    itemDecoration.setVerticalSpan((float) dividerVerticalSize);
                } else {
                    itemDecoration.setHorizontalSpan((float) dividerHorizontalSize);
                }
            }

            itemDecoration.setColor(dividerColor);
            itemDecoration.setDividerDrawable(dividerDrawable);
            itemDecoration.setShowFirstLine(dividerShowFirst);
            itemDecoration.setShowLastLine(dividerShowLast);

            if (currentItemDecoration != null) {
                super.removeItemDecoration(currentItemDecoration);
            }
            currentItemDecoration = itemDecoration.build();
            super.addItemDecoration(currentItemDecoration);
        }
    }

    /**
     * 移除底部视图
     */
    @SuppressLint("NotifyDataSetChanged")
    public void removeFooterView(@NonNull View footerView) {
        for (int i = 0; i < mFooterViews.size(); i++) {
            if (footerView.equals(mFooterViews.valueAt(i))) {
                mFooterViews.removeAt(i);
                if (mWrapAdapter != null && mWrapAdapter.getInnerAdapter() != null) {
                    mWrapAdapter.getInnerAdapter().notifyDataSetChanged();
//                mWrapAdapter.getInnerAdapter().notifyItemRemoved(mHeaderViews.size() + mWrapAdapter.getInnerItemCount() + i);
                }
            }
        }
    }

    /**
     * 设置悬浮固定显示headerViewType
     */
    public void setPinnedHeaderType(Integer... pinnedHeaderType) {
        this.setPinnedHeaderType(Arrays.asList(pinnedHeaderType));
    }

    /**
     * 设置悬浮固定显示headerViewType
     */
    public void setPinnedHeaderType(List<Integer> pinnedHeaderType) {
        if (currentItemDecoration == null || !(currentItemDecoration instanceof PinnedHeaderDecoration)) {
            throw new RuntimeException("未启用SRecyclerView PinnedHeader功能");
        }
        if (pinnedHeaderType == null || pinnedHeaderType.isEmpty()) {
            return;
        }
        ((PinnedHeaderDecoration) currentItemDecoration).setPinnedTypeHeader(pinnedHeaderType);
    }

    /**
     * 获取头部视图数量
     */
    @Override
    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    /**
     * 获取底部视图数量
     */
    @Override
    public int getFootersCount() {
        return mFooterViews.size();
    }

    /**
     * 设置空数据时占位图
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * 当没有更多数据时，是否显示底部到底的视图
     */
    public void setShowStateNoMoreView(boolean showStateNoMoreView) {
        isShowStateNoMoreView = showStateNoMoreView;
    }

    /**
     * 出发点击上一个itemView
     *
     * @param currentSelectedPosition 当前处于选中状态的item位置
     */
    public void performClickPreviousItem(int currentSelectedPosition) {
        if (currentSelectedPosition < 0) {
            currentSelectedPosition = 1;
        }

        Adapter<?> adapter = getAdapter();
        if (adapter == null || getLayoutManager() == null) {
            return;
        }

        if (currentSelectedPosition <= 0) {
            return;
        }

        //找到第一个完全可见item位置
        int firstVisiblePosition = -1;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            firstVisiblePosition = ((GridLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        } else {
            return;
        }

        int position = currentSelectedPosition - 1;
        if (firstVisiblePosition > position) {
            this.scrollToPosition(position);
        }

        View itemView = getLayoutManager().findViewByPosition(position);
        if (itemView != null) {
            itemView.performClick();
        } else {
            this.postDelayed(() -> {
                View itemView2 = getLayoutManager().findViewByPosition(position);
                if (itemView2 != null) {
                    itemView2.performClick();
                }
            }, 20);
        }
    }

    /**
     * 出发点击下一个itemView
     *
     * @param currentSelectedPosition 当前处于选中状态的item位置
     */
    public void performClickNextItem(int currentSelectedPosition) {
        if (currentSelectedPosition < 0) {
            currentSelectedPosition = -1;
        }

        Adapter<?> adapter = getAdapter();
        if (adapter == null || getLayoutManager() == null) {
            return;
        }

        if (currentSelectedPosition >= adapter.getItemCount() - 1) {
            return;
        }

        //找到第一个完全可见item位置
        int lastVisiblePosition = -1;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            lastVisiblePosition = ((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            lastVisiblePosition = ((GridLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();
        } else {
            return;
        }

        int position = currentSelectedPosition + 1;
        if (lastVisiblePosition < position) {
            this.scrollToPosition(position);
        }

        View itemView = getLayoutManager().findViewByPosition(position);
        if (itemView != null) {
            itemView.performClick();
        } else {
            this.postDelayed(() -> {
                View itemView2 = getLayoutManager().findViewByPosition(position);
                if (itemView2 != null) {
                    itemView2.performClick();
                }
            }, 20);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            super.setAdapter(null);
            if (mWrapAdapter != null && mWrapAdapter.mInnerAdapter != null) {
                mWrapAdapter.mInnerAdapter = null;
            }
            return;
        }

        if (mWrapAdapter != null && mWrapAdapter.getInnerAdapter() != null) {
            mWrapAdapter.getInnerAdapter().unregisterAdapterDataObserver(mAdapterDataObserver);
//            mWrapAdapter.mInnerAdapter.onDetachedFromRecyclerView(this);
        }

        mWrapAdapter = new WrapAdapter();
        mWrapAdapter.setInnerAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mAdapterDataObserver);
        mAdapterDataObserver.onChanged();
    }

    @Override
    public Adapter getAdapter() {
        if (mWrapAdapter != null && mWrapAdapter.getInnerAdapter() != null) {
            return mWrapAdapter.getInnerAdapter();
        }
        return super.getAdapter();
    }

    /**
     * wrap header、footer、loadMore
     */
    private class WrapAdapter extends Adapter<ViewHolder> {

        private Adapter mInnerAdapter;

        private class WrapViewHolder extends ViewHolder {
            public WrapViewHolder(View itemView) {
                super(itemView);
            }
        }

        public void setInnerAdapter(Adapter innerAdapter) {
            this.mInnerAdapter = innerAdapter;
        }

        public Adapter getInnerAdapter() {
            return mInnerAdapter;
        }

        public int getInnerItemCount() {
            if (mInnerAdapter == null) {
                return 0;
            }
            return mInnerAdapter.getItemCount();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mHeaderViews.get(viewType) != null) {
                return new WrapViewHolder(mHeaderViews.get(viewType));
            }
            if (mFooterViews.get(viewType) != null) {
                return new WrapViewHolder(mFooterViews.get(viewType));
            }

            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position) || isFooter(position)) {
                return;
            }
            //noinspection unchecked
            mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
        }

        @Override
        public int getItemCount() {
            // 如果不是加载更多，那就可能有多个FooterView
            return getHeadersCount() + getInnerItemCount() + getFootersCount();
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeader(position)) {
                return mHeaderViews.keyAt(position);
            }
            if (isFooter(position)) {
                return mFooterViews.keyAt(position - getHeadersCount() - getInnerItemCount());
            }
            return mInnerAdapter.getItemViewType(position - getHeadersCount());
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return isHeader(position) || isFooter(position) ? gridLayoutManager.getSpanCount() : 1;
                    }
                });
            }
            mInnerAdapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            if (!(holder instanceof WrapViewHolder)) {
                mInnerAdapter.onViewAttachedToWindow(holder);
                return;
            }
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            int position = holder.getLayoutPosition();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams && (isHeader(position) || isFooter(position))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mInnerAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            //noinspection unchecked
            mInnerAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            //noinspection unchecked
            mInnerAdapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            //noinspection unchecked
            return mInnerAdapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mInnerAdapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mInnerAdapter.registerAdapterDataObserver(observer);
        }

    }

    public boolean isHeader(int position) {
        return position < getHeadersCount();
    }

    public boolean isFooter(int position) {
        if (mWrapAdapter != null) {
            return getFootersCount() > 0 && position >= getHeadersCount() + mWrapAdapter.getInnerItemCount();
        }

        if (getAdapter() != null) {
            return getFootersCount() > 0 && position >= getHeadersCount() + getAdapter().getItemCount();
        }

        return getFootersCount() > 0 && position >= getHeadersCount();
    }


    private void notifyEmptyView() {
        Adapter<?> adapter = getAdapter();
        if (adapter != null && emptyView != null) {
            if (adapter.getItemCount() == 0) {
                if (getVisibility() == VISIBLE) {
                    emptyView.setVisibility(View.VISIBLE);
                    setVisibility(GONE);
                }
            } else {
                if (getVisibility() == GONE) {
                    emptyView.setVisibility(View.GONE);
                    setVisibility(VISIBLE);
                }
            }
        }
    }


    private static class DataObserver extends AdapterDataObserver {

        private final SRecyclerView mSRecyclerView;

        public DataObserver(SRecyclerView sRecyclerView) {
            this.mSRecyclerView = sRecyclerView;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onChanged() {
            mSRecyclerView.notifyEmptyView();
            if (mSRecyclerView.mWrapAdapter != null) {
                mSRecyclerView.mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mSRecyclerView.notifyEmptyView();
            if (mSRecyclerView.mWrapAdapter != null) {
                mSRecyclerView.mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mSRecyclerView.notifyEmptyView();
            if (mSRecyclerView.mWrapAdapter != null) {
                mSRecyclerView.mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mSRecyclerView.notifyEmptyView();
            if (mSRecyclerView.mWrapAdapter != null) {
                mSRecyclerView.mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mSRecyclerView.notifyEmptyView();
            if (mSRecyclerView.mWrapAdapter != null) {
                mSRecyclerView.mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mSRecyclerView.notifyEmptyView();
            if (mSRecyclerView.mWrapAdapter != null) {
                mSRecyclerView.mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
            }
        }

    }
}