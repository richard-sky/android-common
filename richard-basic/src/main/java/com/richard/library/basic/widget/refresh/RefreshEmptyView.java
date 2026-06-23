package com.richard.library.basic.widget.refresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.richard.library.basic.widget.EmptyView;
import com.richard.library.basic.widget.list.SRecyclerView;

/**
 * <pre>
 * Description :下拉刷新或者上拉加载空数据占位图
 * Author : admin-richard
 * Date : 2017/6/16 15:25
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/6/16 15:25     admin-richard         new file.
 * </pre>
 */
public class RefreshEmptyView extends RelativeLayout {

    private View contentView;
    private View emptyView;

    public RefreshEmptyView(Context context) {
        super(context);
        this.initView();
    }

    public RefreshEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public RefreshEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RefreshEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView();
    }

    public RefreshEmptyView(Context context, View contentView,View emptyView) {
        super(context);
        this.contentView = contentView;
        this.emptyView = emptyView;
        this.initView();
    }

    public RefreshEmptyView(Context context, View contentView) {
        super(context);
        this.contentView = contentView;
        this.initView();
    }

    private void initView() {
        if(emptyView == null){
            emptyView = new EmptyView(getContext());
        }
        if (this.contentView != null) {
            if (contentView instanceof SRecyclerView) {
                ((SRecyclerView) contentView).setEmptyView(emptyView);
            } else if (contentView instanceof AdapterView) {
                ((AdapterView) contentView).setEmptyView(emptyView);
            } else {
                Log.d("emptyView", "EmptyView 只支持 SRecyclerView 和 ListView");
            }

            this.addView(contentView);
        }
        this.addView(emptyView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    public View getContentView() {
        return contentView;
    }
}
