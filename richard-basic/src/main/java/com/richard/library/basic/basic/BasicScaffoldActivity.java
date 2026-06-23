package com.richard.library.basic.basic;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.richard.library.basic.R;
import com.richard.library.basic.util.FragmentUtil;
import com.richard.library.basic.widget.NavigationBar;
import com.richard.library.basic.widget.PlaceHolderView;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.ObjectUtilKt;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Description : Activity基类(带顶部标题栏、返回按钮)(仅限传统xml布局，非compose布局)
 * Author : admin-richard
 * Date : 2019-05-10 17:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-10 17:57      admin-richard         new file.
 * </pre>
 */
public abstract class BasicScaffoldActivity extends BasicActivity {

    protected NavigationBar navigationbar;
    private PlaceHolderView mPlaceHolderView;
    private List<FragmentUtil.FragmentNode> fragmentNodeList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initNavigation();
    }

    @Override
    public void setContentView(int layoutResID) {
        this.setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View contentView) {
        LinearLayout rootView = new LinearLayout(getContext());
        rootView.setId(R.id.basic_content_root);
        rootView.setOrientation(LinearLayout.VERTICAL);

        navigationbar = new NavigationBar(getContext());
        navigationbar.setTitle(ObjectUtilKt.toString(getTitle()));
        navigationbar.setVisibility(View.GONE);
        rootView.addView(navigationbar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , AppContext.getDimensionPixelSize(R.dimen.navigation_bar_height)
        ));

        //更新子View大小
        if (contentView != null) {
            ViewGroup.LayoutParams childLayoutParams = contentView.getLayoutParams();
            if (childLayoutParams == null) {
                rootView.addView(contentView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT
                ));
            } else {
                childLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                childLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                rootView.addView(contentView);
            }
        }

        super.setContentView(rootView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    @Override
    protected void onDestroy() {
        navigationbar = null;
        mPlaceHolderView = null;
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (fragmentNodeList == null) {
            fragmentNodeList = new ArrayList<>();
        } else {
            fragmentNodeList.clear();
        }
        List<FragmentUtil.FragmentNode> fragments = FragmentUtil.getAllFragments(getSupportFragmentManager(), fragmentNodeList);
        if (fragments.isEmpty()) {
            return super.onKeyUp(keyCode, event);
        }

        for (FragmentUtil.FragmentNode node : fragments) {
            if (this.handleKeyUpEvent(node, keyCode, event)) {
                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * 处理按键事件
     */
    private boolean handleKeyUpEvent(FragmentUtil.FragmentNode node, int keyCode, KeyEvent event) {
        if (node == null) {
            return false;
        }

        if (node.getFragment() instanceof BasicScaffoldFragment basicFragment) {
            if (basicFragment.isUserVisible() && basicFragment.onKeyUp(keyCode, event)) {
                return true;
            }
        }

        if (node.getFragment() instanceof BasicDialogFragment basicDialogFragment) {
            if (basicDialogFragment.isUserVisible() && basicDialogFragment.onKeyUp(keyCode, event)) {
                return true;
            }
        }

        if (node.getNext() == null || node.getNext().isEmpty()) {
            return false;
        }

        for (FragmentUtil.FragmentNode childNode : node.getNext()) {
            if (this.handleKeyUpEvent(childNode, keyCode, event)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 初始化导航条
     */
    private void initNavigation() {
        if (navigationbar == null) {
            return;
        }
        navigationbar.setLeftImageViewShow(true);
        navigationbar.setLeftImageViewClickListener((v) -> onBackPressed());
    }

    /**
     * 设置视图内容占位目标view
     */
    protected void setPlaceHolderTarget(View targetView) {
        if (mPlaceHolderView != null) {
            return;
        }
        mPlaceHolderView = new PlaceHolderView(getContext(), targetView);
    }


    /**
     * 获取内容占位图
     */
    public PlaceHolderView getPlaceHolderView() {
        //默认contentView为操纵View
        if (mPlaceHolderView == null) {
            this.setPlaceHolderTarget(findViewById(R.id.basic_content_root));
        }
        return mPlaceHolderView;
    }
}
