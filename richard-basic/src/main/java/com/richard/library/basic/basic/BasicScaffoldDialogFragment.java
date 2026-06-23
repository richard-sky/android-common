package com.richard.library.basic.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.basic.widget.NavigationBar;
import com.richard.library.basic.widget.PlaceHolderView;
import com.richard.library.context.AppContext;

/**
 * <pre>
 * Description : BaseDialogFragment基类
 * Author : admin-richard
 * Date : 2019-05-10 17:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-10 17:57      admin-richard         new file.
 * </pre>
 */
public abstract class BasicScaffoldDialogFragment extends BasicDialogFragment {

    protected NavigationBar navigationbar;
    //布局文件ID
    private int layoutId;
    //inflate布局内容视图
    private View inflateView;
    //占位视图
    private PlaceHolderView placeHolderView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView != null) {
            return contentView;
        }

        this.initLayoutView();

        if (inflateView == null) {
            inflateView = inflater.inflate(layoutId, null);
        }

        LinearLayout localRootView = new LinearLayout(getContext());
        localRootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT
        ));
        localRootView.setId(R.id.basic_content_root);
        localRootView.setOrientation(LinearLayout.VERTICAL);

        navigationbar = new NavigationBar(getContext());
        navigationbar.setVisibility(View.GONE);
        localRootView.addView(navigationbar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , AppContext.getDimensionPixelSize(R.dimen.navigation_bar_height)
        ));

        //更新子View大小
        if (inflateView != null) {
            ViewGroup.LayoutParams childLayoutParams = inflateView.getLayoutParams();
            if (childLayoutParams == null) {
                localRootView.addView(inflateView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT
                ));
            } else {
                childLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                childLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                localRootView.addView(inflateView);
            }
        }

        return contentView = localRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.initNavigation();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        navigationbar = null;
        inflateView = null;
        placeHolderView = null;
        super.onDestroyView();
    }

    /**
     * 初始化导航条
     */
    private void initNavigation() {
        if (navigationbar == null) {
            return;
        }

        if (getDialog() != null) {
            navigationbar.setVisibility(View.VISIBLE);
            navigationbar.setRadius(
                    getResources().getDimensionPixelSize(R.dimen.radius_value)
                    , getResources().getDimensionPixelSize(R.dimen.radius_value)
                    , 0
                    , 0
            );

            navigationbar.setXBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navigation_bar_backcolor));
        }

        navigationbar.setRightImageView(R.mipmap.icon_close);
        navigationbar.setRightImageViewShow(true);
        navigationbar.setRightImageViewClickListener((v) -> {
            if (getDialog() != null) {
                dismissAllowingStateLoss();
            } else if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    /**
     * 启动内容占位图
     */
    protected void setPlaceHolderTarget(View targetView) {
        if (placeHolderView != null) {
            return;
        }
        placeHolderView = new PlaceHolderView(getContext(), targetView);
    }

    /**
     * 获取内容占位图
     */
    protected PlaceHolderView getPlaceHolderView() {
        //默认contentView为操纵View
        if (placeHolderView == null) {
            this.setPlaceHolderTarget(findViewById(R.id.basic_content_root));
        }
        return placeHolderView;
    }

    @Override
    protected void setContentView(View contentView) {
        this.inflateView = contentView;
    }

    protected void setContentView(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }
}
