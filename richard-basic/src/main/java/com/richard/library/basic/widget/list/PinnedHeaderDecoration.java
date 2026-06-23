package com.richard.library.basic.widget.list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * Description : 悬浮固定header ItemDecoration
 * Author : admin-richard
 * Date : 2017/9/22 14:29
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/9/22 14:29     admin-richard         new file.
 * </pre>
 */
public class PinnedHeaderDecoration extends DividerDecoration {

    private int mHeaderPosition = -1;
    private int mPinnedHeaderTop;
    private boolean mIsAdapterDataChanged;
    private final RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            mIsAdapterDataChanged = true;
        }
    };
    private Rect mClipBounds;
    private View mPinnedHeaderView;
    private RecyclerView.Adapter mAdapter;
    private int viewType;
    //需要置顶显示的标签
    private final List<Integer> pinnedTypeHeader = new ArrayList<>();

    public PinnedHeaderDecoration(int horizonSpan, int verticalSpan, int color, boolean showFirstLine, boolean showLastLine) {
        super(horizonSpan, verticalSpan, color, showFirstLine, showLastLine);
    }

    public PinnedHeaderDecoration(int horizonSpan, int verticalSpan, boolean showFirstLine, boolean showLastLine, Drawable divider) {
        super(horizonSpan, verticalSpan, showFirstLine, showLastLine, divider);
    }

    /**
     * 计算偏移量
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getAdapter() == null) {
            return;
        }

        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (itemPosition < 0) {
            return;
        }

        if (isPinnedViewType(parent.getAdapter().getItemViewType(itemPosition))) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int horizonBound = horizonSpan / 2;
        int verticalBound = verticalSpan / 2;
        if (layoutManager instanceof GridLayoutManager || layoutManager instanceof StaggeredGridLayoutManager) {
            outRect.set(horizonBound, verticalBound, horizonBound, verticalBound);
        } else if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            outRect.set(0, horizonBound, 0, horizonBound);
        } else {
            outRect.set(verticalBound, 0, verticalBound, 0);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        boolean isVertical = getOrientation(parent) == LinearLayoutManager.VERTICAL;
        //检测标签，并将标签强制固定在顶部
        createPinnedHeader(parent, isVertical);

        if (mPinnedHeaderView != null && pinnedTypeHeader.contains(viewType)) {
            int headerEndAt = isVertical ? mPinnedHeaderView.getTop() + mPinnedHeaderView.getHeight()
                    : mPinnedHeaderView.getLeft() + mPinnedHeaderView.getWidth();

            // 根据坐标查找view，headEnd + 1找到的就是mPinnedHeaderView底部下面的view
            View v = isVertical ? parent.findChildViewUnder(canvas.getWidth() / 2F, headerEndAt + 1)
                    : parent.findChildViewUnder(headerEndAt + 1, canvas.getHeight() / 2F);

            if (v != null && isHeaderView(parent, v)) {
                // 如果是标签的话，缓存的标签就要同步跟此标签移动
                mPinnedHeaderTop = isVertical ? v.getTop() - mPinnedHeaderView.getHeight()
                        : v.getLeft() - mPinnedHeaderView.getWidth();
            } else {
                mPinnedHeaderTop = 0;
            }

            mClipBounds = canvas.getClipBounds();

            if (isVertical) {
                mClipBounds.top = mPinnedHeaderTop + mPinnedHeaderView.getHeight();
            } else {
                mClipBounds.left = mPinnedHeaderTop + mPinnedHeaderView.getWidth();
            }

            canvas.clipRect(mClipBounds);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        boolean isVertical = getOrientation(parent) == LinearLayoutManager.VERTICAL;
        if (mPinnedHeaderView != null && pinnedTypeHeader.contains(viewType)) {
            canvas.save();

            if (isVertical) {
                mClipBounds.top = 0;
            } else {
                mClipBounds.left = 0;
            }

            canvas.clipRect(mClipBounds, Region.Op.UNION);

            if (isVertical) {
                canvas.translate(0, mPinnedHeaderTop);
            } else {
                canvas.translate(mPinnedHeaderTop, 0);
            }

            mPinnedHeaderView.draw(canvas);

            canvas.restore();

        }
    }

    /**
     * 创建悬浮固定header
     *
     * @param parent     列表控件
     * @param isVertical 是否属于纵向滑动，否则属于横向滑动
     */
    private void createPinnedHeader(RecyclerView parent, boolean isVertical) {
        updatePinnedHeader(parent);

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null || layoutManager.getChildCount() <= 0) {
            return;
        }
        //获取第一个可见item的position
        int firstVisiblePosition = this.findFirstVisiblePosition(layoutManager);
        int headerPosition = this.findPinnedHeaderPosition(parent, firstVisiblePosition);

        if (headerPosition >= 0 && mHeaderPosition != headerPosition) {

            mHeaderPosition = headerPosition;
            //获取标签类型
            viewType = mAdapter.getItemViewType(headerPosition);
            //手动调用创建标签
            RecyclerView.ViewHolder pinnedViewHolder = mAdapter.createViewHolder(parent, viewType);
            mAdapter.bindViewHolder(pinnedViewHolder, headerPosition);
            mPinnedHeaderView = pinnedViewHolder.itemView;

            ViewGroup.LayoutParams layoutParams = mPinnedHeaderView.getLayoutParams();
            if (layoutParams == null) {
                // 标签默认宽度占满parent
                if (isVertical) {
                    layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                } else {
                    layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }
                mPinnedHeaderView.setLayoutParams(layoutParams);
            }

            // 测量高度
            int specMode;
            int specSize;
            int maxSize;
            if (isVertical) {
                specMode = View.MeasureSpec.getMode(layoutParams.height);
                specSize = View.MeasureSpec.getSize(layoutParams.height);
                maxSize = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
            } else {
                specMode = View.MeasureSpec.getMode(layoutParams.width);
                specSize = View.MeasureSpec.getSize(layoutParams.width);
                maxSize = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();
            }

            if (specMode == View.MeasureSpec.UNSPECIFIED) {
                specMode = View.MeasureSpec.EXACTLY;
            }

            if (specSize > maxSize) {
                specSize = maxSize;
            }

            int widthMeasureSpec;
            int heightMeasureSpec;
            if (isVertical) {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(specSize, specMode);
            } else {
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.EXACTLY);
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(specSize, specMode);
            }
            mPinnedHeaderView.measure(widthMeasureSpec, heightMeasureSpec);

            // 位置强制布局在顶部
            mPinnedHeaderView.layout(0, 0, mPinnedHeaderView.getMeasuredWidth(), mPinnedHeaderView.getMeasuredHeight());
        }
    }

    /**
     * 找出第一个可见的Item的位置
     */
    private int findFirstVisiblePosition(RecyclerView.LayoutManager layoutManager) {
        int firstVisiblePosition = 0;
        if (layoutManager instanceof GridLayoutManager) {
            firstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(into);
            firstVisiblePosition = Integer.MAX_VALUE;
            for (int pos : into) {
                firstVisiblePosition = Math.min(pos, firstVisiblePosition);
            }
        }
        return firstVisiblePosition;
    }

    /**
     * 从传入位置递减找出标签的位置
     */
    private int findPinnedHeaderPosition(RecyclerView parent, int fromPosition) {
        if (fromPosition > mAdapter.getItemCount() || fromPosition < 0) {
            return -1;
        }

        for (int position = fromPosition; position >= 0; position--) {
            final int viewType = mAdapter.getItemViewType(position);
            if (isPinnedViewType(viewType)) {
                return position;
            }
        }

        return -1;
    }

    /**
     * 设置需要置顶显示的标签
     */
    public void setPinnedTypeHeader(Integer... pinnedTypeHeader) {
        this.pinnedTypeHeader.clear();
        this.pinnedTypeHeader.addAll(Arrays.asList(pinnedTypeHeader));
    }

    /**
     * 设置需要置顶显示的标签
     */
    public void setPinnedTypeHeader(List<Integer> pinnedTypeHeader) {
        this.pinnedTypeHeader.clear();
        this.pinnedTypeHeader.addAll(pinnedTypeHeader);
    }

    private boolean isPinnedViewType(int viewType) {
        return pinnedTypeHeader.contains(viewType);
    }

    private boolean isHeaderView(RecyclerView parent, View v) {
        int position = parent.getChildAdapterPosition(v);
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }

        return isPinnedViewType(mAdapter.getItemViewType(position));
    }

    private void updatePinnedHeader(RecyclerView parent) {
        RecyclerView.Adapter<?> adapter = parent.getAdapter();
        if (mAdapter != adapter || mIsAdapterDataChanged) {
            resetPinnedHeader();
            if (mAdapter != null) {
                mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
            }

            mAdapter = adapter;
            if (mAdapter != null) {
                mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
            }
        }
    }

    private void resetPinnedHeader() {
        mHeaderPosition = -1;
        mPinnedHeaderView = null;
    }

    /**
     * 使用Builder构造
     */
    public static class Builder extends DividerDecoration.Builder {

        public Builder(Context context) {
            super(context);
        }

        public PinnedHeaderDecoration build() {
            if (dividerDrawable != null) {
                return new PinnedHeaderDecoration(mHorizonSpan, mVerticalSpan, mShowFirstLine, mShowLastLine, dividerDrawable);
            }
            return new PinnedHeaderDecoration(mHorizonSpan, mVerticalSpan, mColor, mShowFirstLine, mShowLastLine);
        }
    }
}
