package com.richard.library.basic.widget.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.Scroller;

import androidx.annotation.Nullable;

/**
 * <pre>
 * Description : 支持侧滑菜单的SRecyclerView
 * Author : other
 * Date : 2022/8/15 14:42
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/8/15 14:42     other         new file.
 * </pre>
 */
public class SlideRecyclerView extends SRecyclerView {
    /**
     * 禁止侧滑模式
     */
    public static int MOD_FORBID = 0;
    /**
     * 从左向右滑出菜单模式
     */
    public static int MOD_LEFT = 1;
    /**
     * 从右向左滑出菜单模式
     */
    public static int MOD_RIGHT = 2;
    /**
     * 左右均可以滑出菜单模式
     */
    public static int MOD_BOTH = 3;
    /**
     * 当前的模式
     */
    private int mode = MOD_FORBID;


    /**
     * 手指按下X的坐标
     */
    private int downY;
    /**
     * 手指按下Y的坐标
     */
    private int downX;

    /**
     * 当前滑动的position
     */
    private int slidePosition;
    /**
     * RecyclerView 的item
     */
    private View itemView;

    /**
     * 左侧菜单的长度
     */
    private int leftLength = 0;
    /**
     * 右侧菜单的长度
     */
    private int rightLength = 0;

    private final Scroller scroller;
    /**
     * 认为是用户滑动的最小距离
     */
    private final int mTouchSlop;


    /**
     * 判断是否可以侧向滑动
     */
    private boolean canMove = false;

    /**
     * 标示是否完成侧滑
     */
    private boolean isSlided = false;

    public SlideRecyclerView(Context context) {
        this(context, null);
    }

    public SlideRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = e.getAction();
        int lastX = (int) e.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (this.mode == MOD_FORBID) {
                    return super.onTouchEvent(e);
                }
                // 如果处于侧滑完成状态，侧滑回去，并直接返回
                if (isSlided) {
                    scrollBack();
                    return false;
                }
                // 假如scroller滚动还没有结束，我们直接返回
                if (!scroller.isFinished()) {
                    return false;
                }
                downX = (int) e.getX();
                downY = (int) e.getY();

                itemView = findChildViewUnder(downX, downY);
                slidePosition = getChildAdapterPosition(itemView);
                // 无效的position, 不做任何处理
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.onTouchEvent(e);
                }
                /*此处根据设置的滑动模式，自动获取左侧或右侧菜单的长度*/
                if (this.mode == MOD_BOTH) {
                    this.leftLength = -itemView.getPaddingLeft();
                    this.rightLength = -itemView.getPaddingRight();
                } else if (this.mode == MOD_LEFT) {
                    this.leftLength = -itemView.getPaddingLeft();
                } else if (this.mode == MOD_RIGHT) {
                    this.rightLength = -itemView.getPaddingRight();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canMove
                        && slidePosition != AdapterView.INVALID_POSITION
                        && (Math.abs(e.getX() - downX) > mTouchSlop && Math.abs(e
                        .getY() - downY) < mTouchSlop)) {
                    int offsetX = downX - lastX;
                    if (offsetX > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)) {
                        /*从右向左滑*/
                        canMove = true;
                    } else if (offsetX < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)) {
                        /*从左向右滑*/
                        canMove = true;
                    } else {
                        canMove = false;
                    }
                    /*此段代码是为了避免我们在侧向滑动时同时触发OnItemClickListener时间*/
                    MotionEvent cancelEvent = MotionEvent.obtain(e);
                    cancelEvent
                            .setAction(MotionEvent.ACTION_CANCEL
                                    | (e.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    onTouchEvent(cancelEvent);
                }
                if (canMove) {
                    /*设置此属性，可以在侧向滑动时，保持ListView不会上下滚动*/
                    requestDisallowInterceptTouchEvent(true);
                    // 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
                    int deltaX = downX - lastX;
                    if (deltaX < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)) {
                        /*向左滑*/
                        if (Math.abs(deltaX) >= leftLength) {
                            deltaX = -leftLength;
                        }
                        itemView.scrollTo(deltaX, 0);
                    } else if (deltaX > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)) {
                        /*向右滑*/
                        if (Math.abs(deltaX) >= rightLength) {
                            deltaX = rightLength;
                        }
                        itemView.scrollTo(deltaX, 0);
                    } else {
                        itemView.scrollTo(0, 0);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canMove) {
                    canMove = false;
                    scrollByDistanceX();
                }
                break;

        }
        return super.onTouchEvent(e);
    }

    private void scrollByDistanceX() {
        if (this.mode == MOD_FORBID) {
            return;
        }
        if (itemView.getScrollX() > 0 && (this.mode == MOD_BOTH || this.mode == MOD_RIGHT)) {
            /*从右向左滑*/
            if (itemView.getScrollX() >= rightLength / 2) {
                scrollLeft();
            } else {
                // 滚回到原始位置
                scrollBack();
            }
        } else if (itemView.getScrollX() < 0 && (this.mode == MOD_BOTH || this.mode == MOD_LEFT)) {
            /*从左向右滑*/
            if (itemView.getScrollX() <= -leftLength / 2) {
                scrollRight();
            } else {
                // 滚回到原始位置
                scrollBack();
            }
        } else {
            // 滚回到原始位置
            scrollBack();
        }
    }

    /**
     * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
     */
    private void scrollRight() {
        isSlided = true;
        final int delta = (leftLength + itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
        postInvalidate(); // 刷新itemView
    }

    /**
     * 向左滑动，根据上面我们知道向左滑动为正值
     */
    private void scrollLeft() {
        isSlided = true;
        final int delta = (rightLength - itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
                Math.abs(delta));
        postInvalidate(); // 刷新itemView

    }

    private void scrollBack() {
        isSlided = false;
        scroller.startScroll(itemView.getScrollX(), 0, -itemView.getScrollX(),
                0, Math.abs(itemView.getScrollX()));
        postInvalidate(); // 刷新itemView
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (scroller.computeScrollOffset()) {
            // 让item根据当前的滚动偏移量进行滚动
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }


    /**
     * 提供给外部调用，用以将侧滑出来的滑回去
     */
    public void slideBack() {
        this.scrollBack();
    }
}

