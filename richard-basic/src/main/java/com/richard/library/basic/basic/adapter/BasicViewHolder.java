package com.richard.library.basic.basic.adapter;

import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <pre>
 * Description : BaseViewHolder
 * Author : admin-richard
 * Date : 2015/4/28 14:40
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2015/4/28 14:40      admin-richard         new file.
 * </pre>
 */
public class BasicViewHolder extends RecyclerView.ViewHolder {

    /**
     * 是否属于用户动作触发控件事件（比如解决列表控件中的addTextChangedListener事件导致EditText的值错乱问题）
     */
    boolean isUserTriggerEvent = false;

    /**
     * View存储
     */
    private final SparseArray<View> views = new SparseArray<>();

    /**
     * item 布局View
     */
    private final View rootView;

    /**
     * ViewDataBinding
     */
    private ViewDataBinding binding;


    public BasicViewHolder(View rootView) {
        super(rootView);
        this.rootView = rootView;
    }

    /**
     * ViewDataBinding rootView
     */
    public void bind() {
        this.binding = DataBindingUtil.bind(rootView);
        if (this.binding != null) {
            this.binding.executePendingBindings();
        }
    }

    /**
     * 获取binding
     */
    @SuppressWarnings("all")
    public <T> T getBinding() {
        return (T) binding;
    }

    /**
     * 是否属于用户动作行为触发控件的事件
     */
    public boolean isUserTriggerEvent() {
        return isUserTriggerEvent;
    }

    /**
     * 通过ViewId获取控件
     */
    @SuppressWarnings("all")
    public <T extends View> T getView(@IdRes int id) {
        View view = views.get(id);

        if (view == null) {
            view = rootView.findViewById(id);
            views.put(id, view);
        }

        return (T) view;
    }


    /**
     * 获取item根视图
     */
    public View getRootView() {
        return rootView;
    }

    public ImageView getImageView(@IdRes int id) {
        return getView(id);
    }

    public TextView getTextView(@IdRes int id) {
        return getView(id);
    }

    public CheckBox getCheckBox(@IdRes int id) {
        return getView(id);
    }

    public AbsListView getAbsListView(@IdRes int id) {
        return getView(id);
    }

    public ViewGroup getViewGroup(@IdRes int id) {
        return getView(id);
    }

    public CompoundButton getCompoundButton(@IdRes int id) {
        return getView(id);
    }

    public BasicViewHolder setText(@IdRes int id, String content) {
        getTextView(id).setText(content);
        return this;
    }

    public BasicViewHolder setTextColor(@IdRes int id, int color) {
        getTextView(id).setTextColor(color);
        return this;
    }

    public BasicViewHolder setImageResource(@IdRes int id, int resourceId) {
        getImageView(id).setImageResource(resourceId);
        return this;
    }

    public BasicViewHolder setGone(@IdRes int id) {
        this.setVisibility(id, View.GONE);
        return this;
    }

    public BasicViewHolder setVisible(@IdRes int id) {
        this.setVisibility(id, View.VISIBLE);
        return this;
    }

    public BasicViewHolder setVisibility(@IdRes int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }

    public BasicViewHolder setBackgroundColor(@IdRes int id, int color) {
        getView(id).setBackgroundColor(color);
        return this;
    }

    public BasicViewHolder setBackgroundDrawable(@IdRes int id, Drawable drawable) {
        getView(id).setBackground(drawable);
        return this;
    }

    public BasicViewHolder setBackgroundResource(@IdRes int id, int resourceId) {
        getView(id).setBackgroundResource(resourceId);
        return this;
    }

    public BasicViewHolder setEnabled(@IdRes int id, boolean enabled) {
        getView(id).setEnabled(enabled);
        return this;
    }

    public BasicViewHolder setOnClickListener(@IdRes int id, View.OnClickListener listener) {
        getView(id).setOnClickListener(listener);
        return this;
    }
}
