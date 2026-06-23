package com.richard.library.basic.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.richard.library.basic.R;
import com.richard.library.context.util.DateUtil;
import com.richard.library.basic.widget.EmptyView;
import com.richard.library.basic.widget.list.WrapContentLinearLayoutManager;
import com.richard.library.basic.widget.refresh.horizontal.SmartRefreshHorizontal;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

/**
 * <pre>
 * Description :下拉刷新基类(支持任何内容View)
 * Author : admin-richard
 * Date : 2017/10/17 14:29
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/10/17 14:29     admin-richard         new file.
 * </pre>
 * <p>
 * //设置滚动边界决策(解决横向滑动加载数据时冲突问题)
 * binding.refreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDecider() {
 *
 * @Override public boolean canRefresh(View content) {
 * return !binding.srvView.canScrollHorizontally(-1);
 * }
 * @Override public boolean canLoadMore(View content) {
 * return !binding.srvView.canScrollHorizontally(1);
 * }
 * });
 */
public class BasicHorizontalRefreshLayout extends SmartRefreshHorizontal {

    //是否已设置EmptyView
    private boolean isSetEmptyView;

    private Adapter widgetAdapter;
    private RecyclerView.Adapter<?> recyclerViewAdapter;


    public BasicHorizontalRefreshLayout(Context context) {
        super(context);
        init();
    }

    public BasicHorizontalRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        //header和footer
        if (getRefreshHeader() == null) {
            ClassicsHeader header = new ClassicsHeader(getContext());
            header.setTextSizeTitle(13);
            header.setAccentColor(getResources().getColor(R.color.text));
            header.setTimeFormat(new DateFormat() {
                @NonNull
                @Override
                public StringBuffer format(@NonNull Date date, @NonNull StringBuffer toAppendTo, @NonNull FieldPosition fieldPosition) {
                    return toAppendTo.append(DateUtil.formatDate("yyyy-MM-dd HH:mm:ss", date));
                }

                @Nullable
                @Override
                public Date parse(@NonNull String source, @NonNull ParsePosition pos) {
                    return null;
                }
            });
            this.setRefreshHeader(header);
        }

        if (getRefreshFooter() == null) {
            ClassicsFooter footer = new ClassicsFooter(getContext());
            footer.setDrawableProgressSize(20);
            footer.setAccentColor(getResources().getColor(R.color.text));
            footer.setTextSizeTitle(13);
            this.setRefreshFooter(footer);
        }
    }

    private void init() {
        if (sRefreshInitializer != null) {
            return;
        }

        //this.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
        this.setDragRate(1f);//显示下拉高度/手指真实下拉高度=阻尼效果
        this.setReboundDuration(300);//回弹动画时长（毫秒）

//        this.setHeaderHeight(100);//Header标准高度（显示下拉高度>=标准高度 触发刷新）
//        this.setFooterHeight(100);//Footer标准高度（显示上拉高度>=标准高度 触发加载）

        this.setHeaderMaxDragRate(1.6F);//最大显示下拉高度/Header标准高度
        this.setFooterMaxDragRate(1.6F);//最大显示下拉高度/Footer标准高度
        this.setHeaderTriggerRate(1);//触发刷新距离 与 HeaderHeight 的比率1.0.4
        this.setFooterTriggerRate(1);//触发加载距离 与 FooterHeight 的比率1.0.4

        this.setEnableRefresh(false);//是否启用下拉刷新功能
        this.setEnableLoadMore(false);//是否启用上拉加载功能
        this.setEnableAutoLoadMore(true);//是否启用列表惯性滑动到底部时自动加载更多
        this.setEnablePureScrollMode(true);//是否启用纯滚动模式
        this.setEnableNestedScroll(false);//是否启用嵌套滚动
        this.setEnableOverScrollBounce(true);//是否启用越界回弹
        this.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        this.setEnableHeaderTranslationContent(true);//是否下拉Header的时候向下平移列表或者内容
        this.setEnableFooterTranslationContent(true);//是否上拉Footer的时候向上平移列表或者内容
        this.setEnableLoadMoreWhenContentNotFull(true);//是否在列表不满一页时候开启上拉加载功能
        this.setEnableFooterFollowWhenNoMoreData(false);//是否在全部加载结束之后Footer跟随内容1.0.4
        this.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4

        this.setEnableScrollContentWhenRefreshed(true);//是否在刷新完成时滚动列表显示新的内容 1.0.5
        this.setDisableContentWhenRefresh(false);//是否在刷新的时候禁止列表的操作
        this.setDisableContentWhenLoading(false);//是否在加载的时候禁止列表的操作

//        this.setRefreshHeader(new ClassicsHeader(getContext()));//设置Header
//        this.setRefreshFooter(new ClassicsFooter(getContext()));//设置Footer
//        this.setRefreshContent(new View(getContext()));//设置刷新Content（用于非xml布局代替addView）1.0.4

//        this.autoRefresh();//自动刷新
//        this.autoLoadMore();//自动加载
//        this.finishRefresh();//结束刷新
//        this.finishLoadMore();//结束加载
//        this.finishRefresh(3000);//延迟3000毫秒后结束刷新
//        this.finishLoadMore(3000);//延迟3000毫秒后结束加载
//        this.finishRefresh(false);//结束刷新（刷新失败）
//        this.finishLoadMore(false);//结束加载（加载失败）
//        this.finishLoadMoreWithNoMoreData();//完成加载并标记没有更多数据 1.0.4
//        this.closeHeaderOrFooter();//关闭正在打开状态的 Header 或者 Footer（1.1.0）
//        this.resetNoMoreData();//恢复没有更多数据的原始状态 1.0.4（1.1.0删除）
    }

    /**
     * 设置刷新内容view
     */
    public RefreshLayout setRefreshContent(@NonNull View content, View emptyView, int width, int height) {
        if (content instanceof RecyclerView
                || content instanceof AdapterView
        ) {
            isSetEmptyView = true;
            return super.setRefreshContent(new RefreshEmptyView(getContext(), content, emptyView), width, height);
        }
        return super.setRefreshContent(content, width, height);
    }

    @Override
    public void onFinishInflate() {
        if (!isSetEmptyView) {
            View refreshContentView = null;
            View emptyView = null;

            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView instanceof RefreshHeader
                        || childView instanceof RefreshFooter
                ) {
                    continue;
                }

                if (childView instanceof RecyclerView
                        || childView instanceof AdapterView
                ) {
                    refreshContentView = childView;
                }

                if (childView instanceof EmptyView) {
                    emptyView = childView;
                }
            }

            if (refreshContentView != null && emptyView != null) {
                ((ViewGroup) refreshContentView.getParent()).removeView(refreshContentView);
                ((ViewGroup) emptyView.getParent()).removeView(emptyView);
                setRefreshContent(new RefreshEmptyView(getContext(), refreshContentView, emptyView));
            }
        }

        super.onFinishInflate();
    }


    /**
     * 初始化设置adapter
     */
    private void initAdapter(RecyclerView.LayoutManager layoutManager) {
        View refreshContentView = mRefreshContent.getView();
        View contentView = null;

        if (refreshContentView instanceof RefreshEmptyView) {
            contentView = ((RefreshEmptyView) refreshContentView).getContentView();
        } else {
            contentView = refreshContentView;
        }

        if (contentView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) contentView;
            if (layoutManager != null) {
                recyclerView.setLayoutManager(layoutManager);
            }
            recyclerView.setAdapter(recyclerViewAdapter);
        } else if (contentView instanceof AdapterView) {
            ((AdapterView) contentView).setAdapter(widgetAdapter);
        } else {
            throw new RuntimeException("设置Adapter错误，请检查内容体View(refreshContent)是否为对应的RecyclerView或者AdapterView类型");
        }
    }

    /**
     * 设置android.widget.Adapter
     * 仅限refreshContent为AdapterView有效
     */
    public void setAdapter(Adapter adapter) {
        this.widgetAdapter = adapter;
        this.initAdapter(null);
    }

    /**
     * 设置RecyclerView.Adapter
     * 仅限refreshContent为RecyclerView有效
     *
     * @param adapter 数据adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.recyclerViewAdapter = adapter;
        this.initAdapter(null);
    }

    /**
     * 设置RecyclerView.Adapter
     * 仅限refreshContent为RecyclerView有效
     *
     * @param adapter       数据adapter
     * @param layoutManager 布局管理器 如果为null，则会默认一个竖向列表布局管理器
     */
    public void setAdapter(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        this.recyclerViewAdapter = adapter;
        this.initAdapter(
                layoutManager == null
                        ? new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
                        : layoutManager
        );
    }


    /**
     * 获取内容体view
     */
    public <T extends View> T getTargetView() {
        View contentView = mRefreshContent.getView();
        if (!(contentView instanceof RefreshEmptyView)) {
            return (T) contentView;
        }

        return (T) ((RefreshEmptyView) contentView).getContentView();
    }

    /**
     * 设置下拉刷新和上拉加载监听
     */
    @Override
    public RefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener onRefreshLoadMoreListener) {
        this.setEnableRefresh(onRefreshLoadMoreListener != null);
        this.setEnableLoadMore(onRefreshLoadMoreListener != null);
        this.setEnablePureScrollMode(onRefreshLoadMoreListener == null);
        return super.setOnRefreshLoadMoreListener(onRefreshLoadMoreListener);
    }

    @Override
    public RefreshLayout setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.setEnableRefresh(onRefreshListener != null);
        this.setEnableLoadMore(false);
        this.setEnablePureScrollMode(false);
        return super.setOnRefreshListener(onRefreshListener);
    }

    @Override
    public RefreshLayout setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.setEnableRefresh(false);
        this.setEnableLoadMore(onLoadMoreListener != null);
        this.setEnablePureScrollMode(false);
        return super.setOnLoadMoreListener(onLoadMoreListener);
    }

    @Override
    public boolean autoRefresh() {
        return super.autoRefresh();
    }

    @Override
    public boolean autoLoadMore() {
        return super.autoLoadMore();
    }

    /**
     * 是否正处于数据加载中
     */
    public boolean isLoadingData() {
        return super.isRefreshing() || super.isLoading();
    }

    /**
     * 加载结束
     */
    public void completeLoading() {
        if (isRefreshing()) {
            super.finishRefresh();
        }
        if (isLoading()) {
            super.finishLoadMore();
        }
    }
}
