package com.richard.library.basic.basic.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.List;

/**
 * <pre>
 * Description : BaseRecyclerViewAdapter
 * Author : admin-richard
 * Date : 2022/4/1 14:00
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/4/1 14:00      admin-richard         new file.
 * </pre>
 */
public abstract class BasicBindingAdapter<T> extends BasicAdapter<T> {

    public BasicBindingAdapter() {
        super();
    }

    public BasicBindingAdapter(List<T> data) {
        super(data);
    }

    public BasicBindingAdapter(@LayoutRes int layoutId, List<T> data) {
        super(layoutId, data);
    }

    public BasicBindingAdapter(View layoutView, List<T> data) {
        super(layoutView, data);
    }

    @NonNull
    @Override
    public BasicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BasicViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        viewHolder.bind();
        return viewHolder;
    }

    @Override
    protected final View getItemLayoutView(ViewGroup parent, int viewType) {
        return super.getItemLayoutView(parent, viewType);
    }
}
