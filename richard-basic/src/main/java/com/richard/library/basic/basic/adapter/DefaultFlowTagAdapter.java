package com.richard.library.basic.basic.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.richard.library.basic.widget.flowlayout.FlowLayout;
import com.richard.library.basic.widget.flowlayout.TagView;
import com.richard.library.basic.R;

/**
 * <pre>
 * Description : 默认TagAdapter实现
 * Author : admin-richard
 * Date : 2019-06-20 10:33
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-20 10:33      admin-richard         new file.
 * </pre>
 */
public class DefaultFlowTagAdapter<T> extends BasicTagAdapter<T> {

    public DefaultFlowTagAdapter(@NonNull FlowLayout flowLayout) {
        super(flowLayout);
    }

    @Override
    protected TagView getTagView(Context context) {
        return (TagView) LayoutInflater.from(context).inflate(R.layout.item_flow_tag, null);
    }

    @Override
    protected void convert(TagView tagView, T itemInfo) {
        tagView.setText(itemInfo != null ? String.valueOf(itemInfo) : "");
    }
}
