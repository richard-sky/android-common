package com.richard.library.basic.basic.adapter;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * Description : RecyclerView 长按拖动排序、侧滑删除
 * Author : admin-richard
 * Date : 2021-05-11 13:52
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021-05-11 13:52      admin-richard         new file.
 * </pre>
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final RecyclerView.Adapter<?> adapter;
    private List<?> data;
    public static final float ALPHA_FULL = 1.0f;
    private boolean isDragSort = false;//是否可长按拖动排序
    private boolean isSlideDelete = false;//是否可侧滑删除

    public ItemTouchHelperCallback(RecyclerView.Adapter<?> adapter, List<?> data) {
        this.adapter = adapter;
        this.data = data;
    }

    public ItemTouchHelperCallback(RecyclerView.Adapter<?> adapter) {
        this.adapter = adapter;
    }

    /**
     * 设置数据
     */
    public void setData(List<?> data) {
        this.data = data;
    }

    /**
     * 设置是否启用长按拖动排序
     */
    public void setDragSort(boolean dragSort) {
        isDragSort = dragSort;
    }

    /**
     * 设置是否启用侧滑删除
     */
    public void setSlideDelete(boolean slideDelete) {
        isSlideDelete = slideDelete;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return isDragSort;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isSlideDelete;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // Set movement flags based on the layout manager
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        if (source.getItemViewType() != target.getItemViewType()) {
            return false;
        }

        //得到当拖拽的viewHolder的Position
        int fromPosition = source.getBindingAdapterPosition();
        //拿到当前拖拽到的item的viewHolder
        int toPosition = target.getBindingAdapterPosition();

        Collections.swap(data, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        // Notify the adapter of the dismissal
        int position = viewHolder.getBindingAdapterPosition();
        data.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onChildDraw(Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setAlpha(0.7F);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(ALPHA_FULL);
    }
}
