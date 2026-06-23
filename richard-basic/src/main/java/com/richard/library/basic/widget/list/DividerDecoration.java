package com.richard.library.basic.widget.list;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * <pre>
 * Description : RecyclerView 分隔线
 * Author : admin-richard
 * Date : 2017/9/22 14:29
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/9/22 14:29     admin-richard         new file.
 * 2.0         2025/10/15          fixed                修复第一行有整行布局时第二行上边距消失问题
 * 2.1         2025/10/15          fixed                修复整行布局的列边距问题
 * </pre>
 */
public class DividerDecoration extends RecyclerView.ItemDecoration {

    protected final Drawable divider;
    protected final boolean showFirstLine;
    protected final boolean showLastLine;
    protected final int horizonSpan;
    protected final int verticalSpan;

    public DividerDecoration(int horizonSpan, int verticalSpan, int color, boolean showFirstLine, boolean showLastLine) {
        this.horizonSpan = horizonSpan;
        this.showFirstLine = showFirstLine;
        this.showLastLine = showLastLine;
        this.verticalSpan = verticalSpan;
        divider = new ColorDrawable(color);
    }

    public DividerDecoration(int horizonSpan, int verticalSpan, boolean showFirstLine, boolean showLastLine, Drawable divider) {
        if (horizonSpan <= 0) {
            horizonSpan = divider.getIntrinsicHeight();
        }

        if (verticalSpan <= 0) {
            verticalSpan = divider.getIntrinsicWidth();
        }

        this.horizonSpan = horizonSpan;
        this.showFirstLine = showFirstLine;
        this.showLastLine = showLastLine;
        this.verticalSpan = verticalSpan;
        this.divider = divider;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            //最后一行底部横线不绘制
            if (isLastRaw(parent, i, getSpanCount(parent), childCount) && !showLastLine) {
                continue;
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + horizonSpan;

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            // 修复：使用实际列索引判断是否为最后一列
            if (isLastColumn(parent, child)) {
                continue;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin + horizonSpan;
            final int left = child.getRight() + params.rightMargin;
            int right = left + verticalSpan;

            //满足条件(最后一行 && 不绘制) 将vertical多出的一部分去掉;
            if (i == childCount - 1) {
                right -= verticalSpan;
            }
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    /**
     * 判断是否为最后一列
     */
    private boolean isLastColumn(RecyclerView parent, View child) {
        int position = parent.getChildAdapterPosition(child);
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }

        int spanCount = getSpanCount(parent);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) child.getLayoutParams();
            int spanIndex = params.getSpanIndex();
            int spanSize = params.getSpanSize();
            // 如果当前项跨越的列数加上起始列索引等于总列数，说明是最后一列
            return spanIndex + spanSize == spanCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) child.getLayoutParams();
            int spanIndex = params.getSpanIndex();
            // 对于瀑布流布局，检查是否为最后一列
            return spanIndex == spanCount - 1;
        } else {
            // 线性布局管理器，使用原来的判断逻辑
            return (position + 1) % spanCount == 0;
        }
    }

    /**
     * 计算偏移量 - 修复第一行有整行布局时第二行上边距问题及整行布局的列边距问题
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (itemPosition < 0) {
            super.getItemOffsets(outRect, view, parent, state);
            return;
        }

        int orientation = getOrientation(parent);
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        // 修复：使用实际列索引而不是简单的位置取模
        int column = getColumnIndex(parent, view, itemPosition, spanCount);
        int spanSize = getSpanSize(parent, view, itemPosition, spanCount);

        int bottom;

        int left;
        int right;

        // 修复：判断是否为整行布局（spanSize等于总列数）
        boolean isFullSpan = (spanSize == spanCount);

        if (isFullSpan) {
            // 整行布局：左右边距为0，确保撑满整行
            left = 0;
            right = 0;
        } else {
            // 非整行布局：按照原来的方式计算列边距
            left = column * verticalSpan / spanCount;
            right = verticalSpan - (column + 1) * verticalSpan / spanCount;
        }

        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            if (showLastLine) {
                bottom = horizonSpan;
            } else {
                bottom = 0;
            }
        } else {
            bottom = horizonSpan;
        }

        // 修复：使用准确的第一行判断方法替代简单的位置比较
        boolean isFirstRow = isFirstRow(parent, itemPosition, spanCount);

        if (orientation == LinearLayoutManager.VERTICAL) {
            if (showFirstLine && isFirstRow) {
                outRect.set(left, horizonSpan, right, bottom);
            } else {
                outRect.set(left, 0, right, bottom);
            }
        } else {
            if (showFirstLine && isFirstRow) {
                outRect.set(verticalSpan, 0, verticalSpan, bottom);
            } else {
                outRect.set(verticalSpan / 2, 0, verticalSpan / 2, bottom);
            }
        }
    }

    /**
     * 获取实际列索引
     */
    private int getColumnIndex(RecyclerView parent, View view, int itemPosition, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            return params.getSpanIndex();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            return params.getSpanIndex();
        } else {
            // 对于线性布局管理器，回退到使用位置取模
            return itemPosition % spanCount;
        }
    }

    /**
     * 新增：获取当前item的span大小
     */
    private int getSpanSize(RecyclerView parent, View view, int itemPosition, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            return params.getSpanSize();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            // 瀑布流布局通常每个item占一列
            return 1;
        } else {
            // 线性布局管理器，每个item占满整行
            return spanCount;
        }
    }

    /**
     * 新增：准确判断是否为第一行
     */
    private boolean isFirstRow(RecyclerView parent, int itemPosition, int spanCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridManager = (GridLayoutManager) layoutManager;
            // 使用getSpanGroupIndex准确获取行索引，0表示第一行
            int rowIndex = gridManager.getSpanSizeLookup().getSpanGroupIndex(itemPosition, spanCount);
            return rowIndex == 0;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            // 瀑布流布局：位置小于列数的属于第一行
            return itemPosition < spanCount;
        } else {
            // 线性布局：只有position为0才是第一行
            return itemPosition == 0;
        }
    }

    /**
     * 获取方向
     */
    protected int getOrientation(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).getOrientation();
        }
        return LinearLayoutManager.VERTICAL;
    }

    /**
     * 获取列数
     */
    protected int getSpanCount(RecyclerView parent) {
        // 列数
        int mSpanCount = 1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            mSpanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            mSpanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
                mSpanCount = layoutManager.getItemCount();
            }
        }
        return mSpanCount;
    }

    /**
     * 是否最后一行
     *
     * @param parent     RecyclerView
     * @param pos        当前item的位置
     * @param spanCount  每行显示的item个数
     * @param childCount child个数
     */
    protected boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            return getResult(pos, spanCount, childCount);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // StaggeredGridLayoutManager 且纵向滚动
                return getResult(pos, spanCount, childCount);
            } else {
                // StaggeredGridLayoutManager 且横向滚动
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getResult(int pos, int spanCount, int childCount) {
        int remainCount = childCount % spanCount;//获取余数
        //如果正好最后一行完整;
        if (remainCount == 0) {
            if (pos >= childCount - spanCount) {
                return true; //最后一行全部不绘制;
            }
        } else {
            if (pos >= childCount - childCount % spanCount) {
                return true;
            }
        }
        return false;
    }

    /**
     * 使用Builder构造
     */
    public static class Builder {
        protected final Context mContext;
        protected final Resources mResources;
        protected boolean mShowFirstLine;
        protected boolean mShowLastLine;
        protected int mHorizonSpan;
        protected int mVerticalSpan;
        protected int mColor;
        protected Drawable dividerDrawable;

        public Builder(Context context) {
            mContext = context;
            mResources = context.getResources();
            mShowLastLine = true;
            mHorizonSpan = 0;
            mVerticalSpan = 0;
            mColor = Color.WHITE;
        }

        /**
         * 通过资源文件设置分隔线颜色
         */
        public Builder setColorResource(@ColorRes int resource) {
            setColor(ContextCompat.getColor(mContext, resource));
            return this;
        }

        /**
         * 设置颜色
         */
        public Builder setColor(@ColorInt int color) {
            mColor = color;
            return this;
        }

        /**
         * 设置分割线drawable
         */
        public Builder setDividerDrawable(Drawable dividerDrawable) {
            this.dividerDrawable = dividerDrawable;
            return this;
        }

        /**
         * 通过dp设置垂直间距
         */
        public Builder setVerticalSpan(@DimenRes int vertical) {
            this.mVerticalSpan = mResources.getDimensionPixelSize(vertical);
            return this;
        }

        /**
         * 通过px设置垂直间距
         */
        public Builder setVerticalSpan(float mVertical) {
            this.mVerticalSpan = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mVertical, mResources.getDisplayMetrics());
            return this;
        }

        /**
         * 通过dp设置水平间距
         */
        public Builder setHorizontalSpan(@DimenRes int horizontal) {
            this.mHorizonSpan = mResources.getDimensionPixelSize(horizontal);
            return this;
        }

        /**
         * 通过px设置水平间距
         */
        public Builder setHorizontalSpan(float horizontal) {
            this.mHorizonSpan = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, horizontal, mResources.getDisplayMetrics());
            return this;
        }

        /**
         * 是否第一条顶部显示分割线
         */
        public Builder setShowFirstLine(boolean show) {
            mShowFirstLine = show;
            return this;
        }

        /**
         * 是否最后一条显示分割线
         */
        public Builder setShowLastLine(boolean show) {
            mShowLastLine = show;
            return this;
        }

        public DividerDecoration build() {
            if (dividerDrawable != null) {
                return new DividerDecoration(mHorizonSpan, mVerticalSpan, mShowFirstLine, mShowLastLine, dividerDrawable);
            }
            return new DividerDecoration(mHorizonSpan, mVerticalSpan, mColor, mShowFirstLine, mShowLastLine);
        }
    }
}