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
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
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
 */
public class BasicRefreshLayout extends SmartRefreshLayout {

    //是否已设置EmptyView
    private boolean isSetEmptyView;

    private Adapter widgetAdapter;
    private RecyclerView.Adapter<?> recyclerViewAdapter;


    public BasicRefreshLayout(Context context) {
        super(context);
        init();
    }

    public BasicRefreshLayout(Context context, AttributeSet attrs) {
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

//------------------------------------ClassicsHeader设置属性-------------------------------------------------

//         ClassicsHeader.REFRESH_HEADER_PULLDOWN = getString(R.string.header_pulldown);//"下拉可以刷新";
//         ClassicsHeader.REFRESH_HEADER_REFRESHING = getString(R.string.header_refreshing);//"正在刷新...";
//         ClassicsHeader.REFRESH_HEADER_LOADING = getString(R.string.header_loading);//"正在加载...";
//         ClassicsHeader.REFRESH_HEADER_RELEASE = getString(R.string.header_release);//"释放立即刷新";
//         ClassicsHeader.REFRESH_HEADER_FINISH = getString(R.string.header_finish);//"刷新完成";
//         ClassicsHeader.REFRESH_HEADER_FAILED = getString(R.string.header_failed);//"刷新失败";
//         ClassicsHeader.REFRESH_HEADER_SECONDARY = getString(R.string.header_secondary);//"释放进入二楼";
//         ClassicsHeader.REFRESH_HEADER_LASTTIME = getString(R.string.header_lasttime);//"上次更新 M-d HH:mm";
//         ClassicsHeader.REFRESH_HEADER_LASTTIME = getString(R.string.header_lasttime);//"'Last update' M-d HH:mm"
//         //下面示例中的值等于默认值
//         ClassicsHeader header = (ClassicsHeader)findViewById(R.id.header);
//         header.setAccentColor(android.R.color.white);//设置强调颜色
//         header.setPrimaryColor(R.color.colorPrimary);//设置主题颜色
//         header.setTextSizeTitle(16);//设置标题文字大小（sp单位）
//         header.setTextSizeTitle(16, TypedValue.COMPLEX_UNIT_SP);//同上（1.1.0版本删除）
//         header.setTextSizeTime(10);//设置时间文字大小（sp单位）
//         header.setTextSizeTime(10, TypedValue.COMPLEX_UNIT_SP);//同上（1.1.0版本删除）
//         header.setTextTimeMarginTop(10);//设置时间文字的上边距（dp单位）
//         header.setTextTimeMarginTopPx(10);//同上-像素单位（1.1.0版本删除）
//         header.setEnableLastTime(true);//是否显示时间
//         header.setFinishDuration(500);//设置刷新完成显示的停留时间（设为0可以关闭停留功能）
//         header.setDrawableSize(20);//同时设置箭头和图片的大小（dp单位）
//         header.setDrawableArrowSize(20);//设置箭头的大小（dp单位）
//         header.setDrawableProgressSize(20);//设置图片的大小（dp单位）
//         header.setDrawableMarginRight(20);//设置图片和箭头和文字的间距（dp单位）
//         header.setDrawableSizePx(20);//同上-像素单位
//         header.setDrawableArrowSizePx(20);//同上-像素单位（1.1.0版本删除）
//         header.setDrawableProgressSizePx(20);//同上-像素单位（1.1.0版本删除）
//         header.setDrawableMarginRightPx(20);//同上-像素单位（1.1.0版本删除）
//         header.setArrowBitmap(bitmap);//设置箭头位图（1.1.0版本删除）
//         header.setArrowDrawable(drawable);//设置箭头图片
//         header.setArrowResource(R.drawable.ic_arrow);//设置箭头资源
//         header.setProgressBitmap(bitmap);//设置图片位图（1.1.0版本删除）
//         header.setProgressDrawable(drawable);//设置图片
//         header.setProgressResource(R.drawable.ic_progress);//设置图片资源
//         header.setTimeFormat(new DynamicTimeFormat("上次更新 %s"));//设置时间格式化（时间会自动更新）
//         header.setLastUpdateText("上次更新 3秒前");//手动更新时间文字设置（将不会自动更新时间）
//         header.setSpinnerStyle(SpinnerStyle.Translate);//设置移动样式（不支持：MatchLayout）


//------------------------------------ClassicsFooter设置属性-------------------------------------------------
//        ClassicsFooter.REFRESH_FOOTER_PULLING = getString(R.string.footer_pulling);//"上拉加载更多";
//        ClassicsFooter.REFRESH_FOOTER_RELEASE = getString(R.string.footer_release);//"释放立即加载";
//        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getString(R.string.footer_refreshing);//"正在刷新...";
//        ClassicsFooter.REFRESH_FOOTER_LOADING = getString(R.string.footer_loading);//"正在加载...";
//        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.footer_finish);//"加载完成";
//        ClassicsFooter.REFRESH_FOOTER_FAILED = getString(R.string.footer_failed);//"加载失败";
//        ClassicsFooter.REFRESH_FOOTER_NOTHING = getString(R.string.footer_nothing);//"没有更多数据了";
//
//        //下面示例中的值等于默认值
//        ClassicsFooter footer = (ClassicsFooter)findViewById(R.id.footer);
//        footer.setAccentColor(android.R.color.white);//设置强调颜色
//        footer.setPrimaryColor(R.color.colorPrimary);//设置主题颜色
//        footer.setTextSizeTitle(16);//设置标题文字大小（sp单位）
//        footer.setTextSizeTitle(16, TypedValue.COMPLEX_UNIT_SP);//同上
//        footer.setFinishDuration(500);//设置刷新完成显示的停留时间
//        footer.setDrawableSize(20);//同时设置箭头和图片的大小（dp单位）
//        footer.setDrawableArrowSize(20);//设置箭头的大小（dp单位）
//        footer.setDrawableProgressSize(20);//设置图片的大小（dp单位）
//        footer.setDrawableMarginRight(20);//设置图片和箭头和文字的间距（dp单位）
//        footer.setDrawableSizePx(20);//同上-像素单位（1.1.0版本删除）
//        footer.setDrawableArrowSizePx(20);//同上-像素单位（1.1.0版本删除）
//        footer.setDrawableProgressSizePx(20);//同上-像素单位（1.1.0版本删除）
//        footer.setDrawableMarginRightPx(20);//同上-像素单位（1.1.0版本删除）
//        footer.setArrowBitmap(bitmap);//设置箭头位图（1.1.0版本删除）
//        footer.setArrowDrawable(drawable);//设置箭头图片
//        footer.setArrowResource(R.drawable.ic_arrow);//设置箭头资源
//        footer.setProgressBitmap(bitmap);//设置图片位图（1.1.0版本删除）
//        footer.setProgressDrawable(drawable);//设置图片
//        footer.setProgressResource(R.drawable.ic_progress);//设置图片资源
//        footer.setSpinnerStyle(SpinnerStyle.Translate);//设置移动样式（不支持：MatchLayout）
