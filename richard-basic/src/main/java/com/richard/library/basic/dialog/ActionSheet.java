package com.richard.library.basic.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.adapter.BasicAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.basic.basic.adapter.listener.OnItemClickListener;
import com.richard.library.basic.util.DrawableUtil;
import com.richard.library.basic.widget.list.SRecyclerView;
import com.richard.library.context.util.DensityUtilKt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * Description : 底部菜单
 * Author : admin-richard
 * Date : 2015/11/07 14:15
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2015/11/07 14:15      admin-richard         new file.
 * </pre>
 */
public class ActionSheet extends Dialog {

    public ActionSheet(Context context, int themeResId) {
        super(context, themeResId);
    }


    public static class Builder<T> {
        private List<T> menuItems;
        private final Context context;
        private String titleText;
        private boolean isBigTitle;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder<T> setTitle(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder<T> setBigTitle(boolean isBigTitle) {
            this.isBigTitle = isBigTitle;
            return this;
        }

        public Builder<T> setMenuItems(List<T> menuItems) {
            this.menuItems = menuItems;
            return this;
        }

        public Builder<T> addMenuItem(T menuItem) {
            if (this.menuItems == null) {
                this.menuItems = new ArrayList<>();
            }
            Collections.addAll(this.menuItems, menuItem);
            return this;
        }

        public ActionSheet create(final Callback<T> callback) {
            final ActionSheet actionSheet = new ActionSheet(context, R.style.action_sheet_style);
            actionSheet.setCanceledOnTouchOutside(true);

            //初始化contentView
            int padding = DensityUtilKt.dp2px(7, context);
            LinearLayout contentView = new LinearLayout(context);
            contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            contentView.setOrientation(LinearLayout.VERTICAL);
            contentView.setPadding(padding, padding, padding, padding);

            //菜单标题
            float radius = DensityUtilKt.dp2px(10, context);
            TextView titleTextView = new TextView(context);
            titleTextView.setGravity(Gravity.CENTER);
            titleTextView.setTextColor(context.getResources().getColor(R.color.text));
            titleTextView.setText(titleText);
            titleTextView.setVisibility(TextUtils.isEmpty(titleText) ? View.GONE : View.VISIBLE);
            titleTextView.getPaint().setFakeBoldText(true);

            int margin = DensityUtilKt.dp2px(10, context);
            titleTextView.setPadding(
                    titleTextView.getPaddingLeft()
                    , margin
                    , titleTextView.getPaddingRight()
                    , margin
            );

            if (isBigTitle) {
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else {
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }

            titleTextView.setBackground(DrawableUtil.generatorGradientDrawable(
                    context.getResources().getColor(R.color.bg)
                    , radius
                    , radius
                    , 0
                    , 0
            ));

            //初始化标题下面的灰色线条
            View titleLine = new View(context);
            titleLine.setVisibility(TextUtils.isEmpty(titleText) ? View.GONE : View.VISIBLE);
            titleLine.setBackgroundColor(ContextCompat.getColor(context, R.color.light_line));
            titleLine.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtilKt.dp2px(0.4F)));

            //初始化ListView
            SRecyclerView recyclerView = new SRecyclerView(context);
            recyclerView.setHasFixedSize(true);
            LinearLayout.LayoutParams listViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            listViewLayoutParams.weight = 1;
            recyclerView.setLayoutParams(listViewLayoutParams);
            recyclerView.setDividerColor(context.getResources().getColor(R.color.light_line));
            recyclerView.setDividerSize(context.getResources().getDimensionPixelSize(R.dimen.line_size));
            recyclerView.setDividerShowLast(false);
            recyclerView.setDividerShowFirst(false);
            recyclerView.notifyAttrChanged();

            //初始化TextView 取消按钮
            TextView cancelBtn = new TextView(context);
            cancelBtn.setGravity(Gravity.CENTER);
            cancelBtn.setText("取消");
            cancelBtn.setTextColor(ContextCompat.getColor(context, R.color.red));
            cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            cancelBtn.getPaint().setFakeBoldText(true);
            cancelBtn.setBackground(DrawableUtil.generatorGradientDrawable(context.getResources().getColor(R.color.bg), DensityUtilKt.dp2px(10, context)));

            LinearLayout.LayoutParams cancelBtnLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtilKt.dp2px(45, context));
            cancelBtnLayoutParams.setMargins(0, DensityUtilKt.dp2px(8, context), 0, 0);
            cancelBtn.setLayoutParams(cancelBtnLayoutParams);

            //添加子view到contentView
            contentView.addView(titleTextView);
            contentView.addView(titleLine);
            contentView.addView(recyclerView);
            contentView.addView(cancelBtn);

            //初始化dialog window
            Window window = actionSheet.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.bottom_show_animation);
            window.getDecorView().setPadding(0, window.getDecorView().getPaddingTop(), 0, window.getDecorView().getPaddingBottom());
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);


            MenuListAdapter<T> menuListAdapter = new MenuListAdapter<T>(
                    !TextUtils.isEmpty(titleText), menuItems, callback);
            recyclerView.setAdapter(menuListAdapter);


            menuListAdapter.setOnItemClickListener(new OnItemClickListener<T>() {
                @Override
                public void onItemClick(T itemInfo, int position) {
                    if (callback != null) {
                        callback.onItemClickIndex(actionSheet, itemInfo, position);
                    }
                }
            });

            cancelBtn.setOnClickListener(view -> actionSheet.dismiss());

            actionSheet.setContentView(contentView);
            return actionSheet;
        }


        public interface Callback<T> {

            default String getItemText(T itemInfo, int position) {
                return String.valueOf(itemInfo);
            }

            void onItemClickIndex(DialogInterface dialog, T itemInfo, int index);
        }
    }


    static class MenuListAdapter<T> extends BasicAdapter<T> {

        private final int padding;
        private final int radius;
        private final Builder.Callback<T> callback;
        private final boolean isShowTitle;

        public MenuListAdapter(boolean isShowTitle, List<T> menuItemList, Builder.Callback<T> callback) {
            this.isShowTitle = isShowTitle;
            this.callback = callback;
            this.padding = DensityUtilKt.dp2px(10);
            this.radius = DensityUtilKt.dp2px(10);

            super.completeLoad(menuItemList, true);
        }

        @Override
        protected View getItemLayoutView(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setId(R.id.text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
            textView.setPadding(padding, padding, padding, padding);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtilKt.dp2px(50)));
            return textView;
        }

        @Override
        protected void convert(BasicViewHolder holder, T itemInfo, int position) {
            if (getItemCount() == 1 && !isShowTitle) {
                holder.setBackgroundDrawable(R.id.text, getRoundDrawable());
            } else if (position == 0 && !isShowTitle) {
                holder.setBackgroundDrawable(R.id.text, getTopDrawable());
            } else if (position == getItemCount() - 1) {
                holder.setBackgroundDrawable(R.id.text, getBottomDrawable());
            } else {
                holder.setBackgroundDrawable(R.id.text, getCenterDrawable());
            }

            if (callback != null) {
                holder.setText(R.id.text, callback.getItemText(getData().get(position), position));
            } else {
                holder.setText(R.id.text, String.valueOf(getData().get(position)));
            }
        }

        private Drawable getRoundDrawable() {
            return DrawableUtil.generatorSelector(
                    android.R.attr.state_pressed
                    , ContextCompat.getColor(getContext(), R.color.content_item_un_press_bg)
                    , ContextCompat.getColor(getContext(), R.color.content_item_pressed_bg)
                    , radius
                    , radius
                    , radius
                    , radius
            );
        }

        private Drawable getTopDrawable() {
            return DrawableUtil.generatorSelector(
                    android.R.attr.state_pressed
                    , ContextCompat.getColor(getContext(), R.color.content_item_un_press_bg)
                    , ContextCompat.getColor(getContext(), R.color.content_item_pressed_bg)
                    , radius
                    , radius
                    , 0
                    , 0
            );
        }

        public Drawable getCenterDrawable() {
            return DrawableUtil.generatorSelector(
                    android.R.attr.state_pressed
                    , ContextCompat.getColor(getContext(), R.color.content_item_un_press_bg)
                    , ContextCompat.getColor(getContext(), R.color.content_item_pressed_bg)
                    , 0
            );
        }

        public Drawable getBottomDrawable() {
            return DrawableUtil.generatorSelector(
                    android.R.attr.state_pressed
                    , ContextCompat.getColor(getContext(), R.color.content_item_un_press_bg)
                    , ContextCompat.getColor(getContext(), R.color.content_item_pressed_bg)
                    , 0
                    , 0
                    , radius
                    , radius
            );
        }
    }

}
