package com.richard.library.basic.basic;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.AnyRes;
import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.adapter.BasicAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.basic.basic.dict.Direction;
import com.richard.library.basic.util.MeasuredUtil;
import com.richard.library.basic.util.ViewUtil;
import com.richard.library.basic.widget.ShadowLayout;
import com.richard.library.basic.widget.list.SRecyclerView;
import com.richard.library.basic.widget.list.WrapContentLinearLayoutManager;
import com.richard.library.context.util.DensityUtilKt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Description : 菜单列表
 * Author : admin-richard
 * Date : 2019-09-25 14:36
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-09-25 14:36      admin-richard         new file.
 * </pre>
 */
public class BasicPopupMenu extends BasicPopupWindow {

    private SRecyclerView menuListView;
    private View itemLayoutView;
    private int itemLayoutId;
    private ShadowLayout shadowLayout;
    private MenuItemConvert mMenuItemConvert;
    private MenuAdapter mMenuAdapter;


    public BasicPopupMenu(Context context) {
        super(context);
    }

    @Override
    public void initLayoutView() {
        int padding = DensityUtilKt.dp2px(8, getContext());
        shadowLayout = new ShadowLayout(getContext());
        shadowLayout.setRadius(getContext().getResources().getDimensionPixelSize(R.dimen.radius_value));
        shadowLayout.setShadowColor(ContextCompat.getColor(getContext(), R.color.shadow));
        shadowLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.popupmenu_button_un_press_bg));
        shadowLayout.setPadding(padding, padding, padding, padding);

        menuListView = new SRecyclerView(getContext());
        menuListView.setLayoutManager(new WrapContentLinearLayoutManager(getContext()));
        menuListView.setHasFixedSize(true);
        menuListView.setDividerColor(ContextCompat.getColor(getContext(), R.color.light_line));
        menuListView.setDividerSize(getContext().getResources().getDimensionPixelSize(R.dimen.line_size));
        menuListView.setDividerShowFirst(false);
        menuListView.setDividerShowLast(false);
        menuListView.notifyAttrChanged();

        shadowLayout.addView(menuListView);

        super.setContentView(shadowLayout);
        super.setSize(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        shadowLayout.notifyUpdateAttrs();

        mMenuAdapter = new MenuAdapter(getContext());
        menuListView.setAdapter(mMenuAdapter);
    }

    @Override
    public void initData() {

    }

    @Override
    public void bindListener() {

    }

    /**
     * 设置菜单item布局ID
     *
     * @param itemLayoutId 布局ID
     */
    public BasicPopupMenu setMenuItemLayout(int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
        return this;
    }


    /**
     * 设置菜单item布局view
     *
     * @param itemLayoutView 布局view
     */
    public BasicPopupMenu setMenuItemLayout(View itemLayoutView) {
        this.itemLayoutView = itemLayoutView;
        return this;
    }

    /**
     * 设置菜单item布局显示信息转换器
     *
     * @param menuItemConvert 转换器
     */
    public BasicPopupMenu setMenuItemConvert(MenuItemConvert menuItemConvert) {
        mMenuItemConvert = menuItemConvert;
        return this;
    }

    /**
     * 添加菜单项列表
     *
     * @param menuItems 菜单项列表
     */
    public BasicPopupMenu addMenuItemText(String[] menuItems) {
        MenuItem menuItemInfo;
        for (String item : menuItems) {
            menuItemInfo = new MenuItem(item, item);
            this.mMenuAdapter.getData().add(menuItemInfo);
        }
        return this;
    }

    /**
     * 添加菜单项列表
     *
     * @param menuItems 菜单项列表
     */
    public BasicPopupMenu addMenuItemText(List<String> menuItems) {
        MenuItem menuItemInfo;
        for (String item : menuItems) {
            menuItemInfo = new MenuItem(item, item);
            this.mMenuAdapter.getData().add(menuItemInfo);
        }
        return this;
    }

    /**
     * 添加菜单项列表
     *
     * @param menuItemList 菜单项列表
     */
    public BasicPopupMenu addMenuItem(List<MenuItem> menuItemList) {
        this.mMenuAdapter.getData().addAll(menuItemList);
        return this;
    }

    /**
     * 添加菜单项
     *
     * @param menuItem 菜单项
     */
    public BasicPopupMenu addMenuItem(MenuItem menuItem) {
        this.mMenuAdapter.getData().add(menuItem);
        return this;
    }

    /**
     * 添加一个主菜单项
     *
     * @param itemTitle 菜单项标题
     */
    public BasicPopupMenu addMenuItem(String itemTitle) {
        this.addMenuItem(null, itemTitle, null);
        return this;
    }

    /**
     * 添加一个主菜单项
     *
     * @param itemIcon  菜单项icon
     * @param itemTitle 菜单项标题
     */
    public BasicPopupMenu addMenuItem(@AnyRes Integer itemIcon, String itemTitle) {
        this.addMenuItem(itemIcon == null ? 0 : itemIcon, itemTitle, null);
        return this;
    }

    /**
     * 添加一个主菜单项
     *
     * @param itemIcon  菜单项icon
     * @param itemTitle 菜单项标题
     */
    public BasicPopupMenu addMenuItem(@AnyRes Integer itemIcon, String itemTitle, Object data) {
        this.mMenuAdapter.getData().add(new MenuItem(itemIcon == null ? 0 : itemIcon, itemTitle, data));
        return this;
    }

    /**
     * 添加一个二级菜单项
     *
     * @param itemTitle 菜单项标题
     */
    public BasicPopupMenu addSubMenuItem(String itemTitle) {
        return this.addSubMenuItem(null, itemTitle);
    }

    /**
     * 添加一个二级菜单项
     *
     * @param itemIcon  菜单项icon
     * @param itemTitle 菜单项标题
     */
    public BasicPopupMenu addSubMenuItem(@AnyRes Integer itemIcon, String itemTitle) {
        if (this.mMenuAdapter.getData() == null || this.mMenuAdapter.getData().isEmpty()) {
            throw new RuntimeException("No menu level 1 is set");
        }

        this.mMenuAdapter.getData()
                .get(this.mMenuAdapter.getItemCount() - 1)
                .getChildMenuItemList()
                .add(new MenuItem(itemIcon == null ? 0 : itemIcon, itemTitle));

        return this;
    }

    /**
     * 添加一个二级菜单项
     *
     * @param itemIcon    菜单项icon
     * @param itemTitle   菜单项标题
     * @param tooltipText 菜单项小标题
     */
    public BasicPopupMenu addSubMenuItem(@AnyRes Integer itemIcon, String itemTitle, String tooltipText) {
        List<MenuItem> data = this.mMenuAdapter.getData();
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("No menu level 1 is set");
        }

        data.get(this.mMenuAdapter.getItemCount() - 1)
                .getChildMenuItemList()
                .add(new MenuItem(itemIcon == null ? 0 : itemIcon, itemTitle, tooltipText));

        return this;
    }

    /**
     * 设置三角形指示方向
     *
     * @param direction 指示方向
     * @param offset    偏移量
     */
    public BasicPopupMenu setTriangleDirection(@ShadowLayout.Direction int direction, int offset) {
        shadowLayout.setDirection(direction);
        shadowLayout.setTriangleOffset(offset);
        return this;
    }

    /**
     * 设置菜单项点击监听事件
     */
    public BasicPopupMenu setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mMenuAdapter.setOnMenuItemClickListener(onMenuItemClickListener);
        return this;
    }

    /**
     * 是否含有菜单列表
     */
    public boolean isHasMenuList() {
        return mMenuAdapter.getItemCount() > 0;
    }

    /**
     * 获取菜单列表控件
     */
    public SRecyclerView getMenuListView() {
        return menuListView;
    }

    /**
     * 获取阴影layout（可以对阴影效果参数进行设置）
     */
    public ShadowLayout getShadowLayout() {
        return shadowLayout;
    }

    /**
     * 显示子级菜单
     *
     * @param subMenuItemList 子级菜单列表
     */
    public BasicPopupMenu getSubPopupMenu(int widthDP, List<MenuItem> subMenuItemList) {
        BasicPopupMenu basePopupMenu = new BasicPopupMenu(getContext());
        basePopupMenu.addMenuItem(subMenuItemList);
        basePopupMenu.setSize(widthDP, ViewGroup.LayoutParams.WRAP_CONTENT);
        basePopupMenu.setTouchOutDismiss(true);

        return basePopupMenu;
    }

    /**
     * 通知菜单列表数据发生改变
     */
    public void notifyDataSetChanged() {
        mMenuAdapter.notifyDataSetChanged();
    }

    /**
     * 清空菜单列表数据
     */
    public void clearMenuItemData() {
        mMenuAdapter.getData().clear();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        this.notifyDataSetChanged();
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        this.notifyDataSetChanged();
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void showAtDirection(View anchor, Direction direction, int xOffset, int yOffset) {
        this.notifyDataSetChanged();
        super.setHeight(MeasuredUtil.getMeasuredHeight(getContentView()));
        super.update();
        super.showAtDirection(anchor, direction, xOffset, yOffset);
    }

    /**
     * 菜单项信息转换器
     */
    public interface MenuItemConvert {
        void convert(BasicViewHolder holder, MenuItem itemInfo, int position);
    }

    /**
     * 菜单项列表适配
     */
    private class MenuAdapter extends BasicAdapter<MenuItem> {

        private OnMenuItemClickListener onMenuItemClickListener;
        private final int selectedTextColor;
        private final int unSelectTextColor;

        public MenuAdapter(Context context) {
            this.selectedTextColor = ContextCompat.getColor(context, R.color.main);
            this.unSelectTextColor = ContextCompat.getColor(context, R.color.text);
            if (itemLayoutView == null && itemLayoutId == 0) {
                itemLayoutId = R.layout.item_popup_menu;
            }
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return itemLayoutId;
        }

        @Override
        protected View getItemLayoutView(ViewGroup parent, int viewType) {
            return itemLayoutView;
        }

        @Override
        protected void convert(BasicViewHolder holder, MenuItem itemInfo, int position) {
            if (itemLayoutId == R.layout.item_popup_menu) {
                if (mMenuItemConvert != null) {
                    mMenuItemConvert.convert(holder, itemInfo, position);
                } else {
                    holder.setVisibility(R.id.iv_menu_item_icon,
                            itemInfo.getItemIcon() == 0 ? View.GONE : View.VISIBLE);
                    holder.setVisibility(R.id.tv_menu_item_tooltip_text,
                            TextUtils.isEmpty(itemInfo.getTooltipText()) ? View.GONE : View.VISIBLE);

                    List<MenuItem> childMenuItemList = itemInfo.getChildMenuItemList();
                    holder.setVisibility(R.id.iv_menu_item_sub_flag,
                            childMenuItemList == null || childMenuItemList.isEmpty() ? View.GONE : View.VISIBLE);

                    if (itemInfo.getItemIcon() != 0) {
                        holder.setImageResource(R.id.iv_menu_item_icon, itemInfo.getItemIcon());
                    }

                    holder.setTextColor(R.id.tv_menu_item_title,
                            itemInfo.isSelected()
                                    ? selectedTextColor
                                    : unSelectTextColor
                    );
                    holder.setText(R.id.tv_menu_item_title, itemInfo.getItemTitle());
                    holder.setText(R.id.tv_menu_item_tooltip_text, itemInfo.getTooltipText());

                    holder.getRootView().setOnClickListener(v -> {
                        if (childMenuItemList != null && !childMenuItemList.isEmpty()) {
                            int[] loc = ViewUtil.getLocOnScreen(v);
                            getSubPopupMenu((int) DensityUtilKt.px2dp(getWidth()), itemInfo.getChildMenuItemList())
                                    .showAtLocation(getRootAnchor(), Gravity.NO_GRAVITY, loc[0] + getWidth(), loc[1]);
                        } else if (onMenuItemClickListener != null) {
                            onMenuItemClickListener.onMenuItemClick(BasicPopupMenu.this, itemInfo, position);
                        }
                    });
                }
            } else if (mMenuItemConvert != null) {
                mMenuItemConvert.convert(holder, itemInfo, position);
                holder.getRootView().setOnClickListener(v -> {
                    if (onMenuItemClickListener != null) {
                        onMenuItemClickListener.onMenuItemClick(BasicPopupMenu.this, itemInfo, position);
                    }
                });
            }
        }

        public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
            this.onMenuItemClickListener = onMenuItemClickListener;
        }

    }


    /**
     * 菜单项信息
     */
    public static class MenuItem implements Serializable {

        private static final long serialVersionUID = 6946634431121907258L;

        private String parentMenuId;//父级菜单项id

        private String menuId;//菜单id

        private int itemIcon;//菜单项icon

        private String itemTitle;//菜单项标题

        private String tooltipText;//菜单项提示文本

        private boolean isSelected;//是否已选中

        private Object data;//菜单项附加数据

        private final List<MenuItem> childMenuItemList = new ArrayList<>();//子级菜单项列表

        public MenuItem() {
        }

        public MenuItem(String itemTitle) {
            this.itemTitle = itemTitle;
        }

        public MenuItem(String itemTitle, Object data) {
            this.itemTitle = itemTitle;
            this.data = data;
        }

        public MenuItem(int itemIcon, String itemTitle) {
            this.itemIcon = itemIcon;
            this.itemTitle = itemTitle;
        }

        public MenuItem(int itemIcon, String itemTitle, String tooltipText, Object data) {
            this.itemIcon = itemIcon;
            this.itemTitle = itemTitle;
            this.tooltipText = tooltipText;
            this.data = data;
        }

        public MenuItem(int itemIcon, String itemTitle, Object data) {
            this.itemIcon = itemIcon;
            this.itemTitle = itemTitle;
            this.data = data;
        }

        public String getParentMenuId() {
            return parentMenuId;
        }

        public void setParentMenuId(String parentMenuId) {
            this.parentMenuId = parentMenuId;
        }

        public String getMenuId() {
            return menuId;
        }

        public void setMenuId(String menuId) {
            this.menuId = menuId;
        }

        public int getItemIcon() {
            return itemIcon;
        }

        public void setItemIcon(int itemIcon) {
            this.itemIcon = itemIcon;
        }

        public String getItemTitle() {
            return itemTitle;
        }

        public void setItemTitle(String itemTitle) {
            this.itemTitle = itemTitle;
        }

        public String getTooltipText() {
            return tooltipText;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public void setTooltipText(String tooltipText) {
            this.tooltipText = tooltipText;
        }

        public List<MenuItem> getChildMenuItemList() {
            return childMenuItemList;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }


    /**
     * 菜单项点击监听事件
     */
    public interface OnMenuItemClickListener {
        void onMenuItemClick(PopupWindow popupWindow, MenuItem menuItem, int position);
    }
}
