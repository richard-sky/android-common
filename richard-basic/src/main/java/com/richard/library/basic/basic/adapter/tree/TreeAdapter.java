package com.richard.library.basic.basic.adapter.tree;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.collection.ArrayMap;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.adapter.BasicAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.context.util.DensityUtilKt;
import com.richard.library.context.util.ObjectUtilKt;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Richard
 * @createDate: 2024/6/17 9:41
 * @version: 1.0
 * @description: 树形列表数据adapter
 */
public class TreeAdapter extends BasicAdapter<TreeNode> {

    //已选择项 key:节点id、value：节点信息
    private final ArrayMap<String, TreeNode> selectedMap = new ArrayMap<>();
    //是否处于多选模式
    private boolean isMulSelect = false;
    //item项边距
    private final int itemMargin = DensityUtilKt.dp2px(24);
    //item项点击事件
    private Callback callback;
    //是否增量数据模式
    private final boolean isIncrementMode;

    /**
     * 构造方法
     * @param isIncrementMode 是否增量数据模式(true:增量数据模式(每次展开树时就加载新的数据添加子节点，折叠后会移除子节点数据)、false: 全量数据模式(每次展开的子节点数据都是已缓存的数据来展示，折叠也不会移除子节点数据))
     */
    public TreeAdapter(boolean isIncrementMode){
        this.isIncrementMode = isIncrementMode;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_basic_tree;
    }

    @Override
    protected void convert(BasicViewHolder holder, TreeNode itemInfo, int position) {
        LinearLayout.LayoutParams expandLayoutParams = (LinearLayout.LayoutParams) holder.getView(R.id.iv_expand).getLayoutParams();
        expandLayoutParams.leftMargin = itemMargin * itemInfo.getTreeDepth();
        holder.getView(R.id.iv_expand).setLayoutParams(expandLayoutParams);

        holder.setText(R.id.tv_text, itemInfo.getName());
        holder.setImageResource(R.id.iv_expand, itemInfo.isOpen() ? R.mipmap.ic_tree_open : R.mipmap.ic_tree_fold);
        holder.setVisibility(R.id.iv_expand, itemInfo.getItemType() == TreeNode.ITEM_TYPE_PARENT ? View.VISIBLE : View.INVISIBLE);

        if (isMulSelect) {
            holder.setVisibility(R.id.iv_image, View.VISIBLE);
            holder.setImageResource(R.id.iv_image, selectedMap.containsKey(itemInfo.getId()) ? R.mipmap.ic_tree_checked : R.mipmap.ic_tree_uncheck);
        } else {
            holder.setVisibility(R.id.iv_image, View.GONE);
            holder.getView(R.id.tv_text).setSelected(selectedMap.containsKey(itemInfo.getId()));
        }

        //折叠控件点击事件
        holder.setOnClickListener(R.id.iv_expand, v -> {
            if (itemInfo.isOpen()) {
                itemInfo.setOpen(false);
                this.onHideChildren(itemInfo, position);
            } else {
                itemInfo.setOpen(true);
                notifyItemChanged(position);
            }
            if (callback == null) {
                return;
            }
            if (itemInfo.isOpen()) {
                if(!isIncrementMode){
                    addChildNode(itemInfo,itemInfo.getChildren());
                }
                callback.onOpen(itemInfo, position);
            } else {
                callback.onFold(itemInfo, position);
            }
        });

        //节点名称布局点击事件
        holder.setOnClickListener(R.id.content_check, v -> {
            if (isMulSelect) {
                boolean isChecked;
                if (selectedMap.containsKey(itemInfo.getId())) {
                    selectedMap.remove(itemInfo.getId());
                    isChecked = false;
                } else {
                    selectedMap.put(itemInfo.getId(), itemInfo);
                    isChecked = true;
                }
                this.handleParentState(itemInfo, position);
                this.handleChildState(itemInfo, isChecked);
                notifyItemRangeChanged(0, getItemCount());
            } else {
                selectedMap.clear();
                selectedMap.put(itemInfo.getId(), itemInfo);
                notifyItemRangeChanged(0, getItemCount());
                if (callback != null) {
                    callback.onItemClick(itemInfo, position);
                }
            }
        });
    }

    /**
     * 设置回调
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 设置是否为多选模式
     */
    public void setMulSelect(boolean mulSelect) {
        isMulSelect = mulSelect;
    }

    /**
     * 当折叠子节点时
     */
    @SuppressLint("NotifyDataSetChanged")
    private void onHideChildren(TreeNode treeNode, int position) {
        List<TreeNode> children = treeNode.getChildren();
        if (children == null) {
            notifyItemChanged(position);
            return;
        }

        int removePosition = getPositionByNodeId(treeNode.getId()) + 1;
        int itemCount = getChildrenCount(treeNode, 0);
        for (int i = 0; i < itemCount; i++) {
            getData().remove(removePosition);
        }

        if(isIncrementMode){
            treeNode.setChildren(null);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取子节点数量
     */
    private int getChildrenCount(TreeNode itemInfo, int count) {
        if (ObjectUtilKt.isEmpty(itemInfo.getChildren())) {
            return count;
        }
        for (TreeNode item : itemInfo.getChildren()) {
            count++;
            count = getChildrenCount(item, count);
        }
        return count;
    }


    /**
     * 处理父级节点状态
     */
    private void handleParentState(TreeNode curTreeNode, int position) {
        if (position <= 0) {
            return;
        }

        //当前节点是顶级节点
        if (curTreeNode.getTreeDepth() <= 0) {
            return;
        }

        //处理上级
        List<TreeNode> data = getData();
        while (--position >= 0) {
            TreeNode item = data.get(position);

            if (curTreeNode.getTreeDepth() == item.getTreeDepth()) {
                continue;
            }

            if (item.getChildren() == null || item.getChildren().isEmpty()) {
                continue;
            }

            boolean isAllChecked = true;
            for (TreeNode node : item.getChildren()) {
                if (!selectedMap.containsKey(node.getId())) {
                    isAllChecked = false;
                    break;
                }
            }

            if (isAllChecked) {
                selectedMap.put(item.getId(), item);
            } else {
                selectedMap.remove(item.getId());
            }
        }
    }

    /**
     * 处理子集节点状态
     */
    private void handleChildState(TreeNode curTreeNode, boolean isChecked) {
        if (!isChecked) {
            this.clearChildSelected(curTreeNode);
        }
        if (curTreeNode.getChildren() == null || curTreeNode.getChildren().isEmpty()) {
            return;
        }

        for (TreeNode item : curTreeNode.getChildren()) {
            if (isChecked) {
                selectedMap.put(item.getId(), item);
            } else {
                selectedMap.remove(item.getId());
            }
            this.handleChildState(item, isChecked);
        }
    }

    /**
     * 清除子集选中项
     */
    private void clearChildSelected(TreeNode node) {
        if (selectedMap.isEmpty()) {
            return;
        }

        List<String> ids = new ArrayList<>();
        TreeNode item;
        for (int i = 0; i < selectedMap.size(); i++) {
            item = selectedMap.valueAt(i);
            if (isBelongToChild(item, node)) {
                ids.add(item.getId());
            }
        }

        selectedMap.removeAll(ids);
    }

    /**
     * 根据节点id获取其下标位置
     */
    public int getPositionByNodeId(String id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0, size = getItemCount(); i < size; i++) {
            if (id.equals(getData().get(i).getId())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 添加顶级节点
     *
     * @param children 子节点
     */
    public void addTopNode(List<TreeNode> children) {
        this.addChildNode(null, children);
    }

    /**
     * 添加子节点
     *
     * @param openTreeNode 点击展开的节点（最顶级节点时为null，其它情况必填）
     * @param children     子节点
     */
    public void addChildNode(TreeNode openTreeNode, List<TreeNode> children) {
        if (children == null) {
            return;
        }

        int openTreeNodePosition = openTreeNode == null ? 0 : this.getPositionByNodeId(openTreeNode.getId()) + 1;

        //添加子节点
        getData().addAll(openTreeNodePosition, children);
        if (openTreeNode != null) {
            if (isMulSelect) {
                boolean isChecked = selectedMap.containsKey(openTreeNode.getId());
                if (isChecked) {
                    for (TreeNode item : children) {
                        selectedMap.put(item.getId(), item);
                    }
                }
            }
            openTreeNode.setChildren(children);
        }
        notifyItemRangeInserted(openTreeNodePosition, children.size());
    }

    /**
     * 通知当前点击的节点无子集
     */
    public void notifyEmptyChild(TreeNode treeNode, int position) {
        treeNode.setOpen(false);
        treeNode.setItemType(TreeNode.ITEM_TYPE_CHILD);
        notifyItemChanged(position);
    }

    /**
     * 获取当前点击的一项
     */
    public TreeNode getOneSelectedItem() {
        return selectedMap.size() > 0 ? selectedMap.valueAt(0) : null;
    }

    /**
     * 获取当前选中的顶级节点列表
     */
    public List<TreeNode> getSelectedTopTreeList() {
        ArrayMap<String, TreeNode> resultMap = new ArrayMap<>();
        TreeNode topTreeNode;
        TreeNode item;

        for (int i = 0; i < selectedMap.size(); i++) {
            item = selectedMap.valueAt(i);
            topTreeNode = this.getTopTreeNode(item);
            resultMap.put(topTreeNode.getId(), topTreeNode);

            //当前属于顶级节点时，则立即停止循环,直接返回顶级节点
            if (item.getTreeDepth() <= 0) {
                break;
            }
        }

        return new ArrayList<>(resultMap.values());
    }

    /**
     * 获取顶级节点
     */
    private TreeNode getTopTreeNode(TreeNode curTreeNode) {
        //当前节点是顶级节点
        if (curTreeNode.getTreeDepth() <= 0) {
            return curTreeNode;
        }

        //处理上级
        TreeNode topTreeNode = curTreeNode;
        for (int i = 0; i < selectedMap.size(); i++) {
            TreeNode item = selectedMap.valueAt(i);
            if (this.isBelongToChild(curTreeNode, item) && topTreeNode.getTreeDepth() > item.getTreeDepth()) {
                topTreeNode = item;
            }
        }

        return topTreeNode;
    }

    /**
     * 获取当前已选中的数量
     */
    public int getSelectedCount() {
        return selectedMap.size();
    }

    /**
     * 是否属于该该分类下的子级分类
     */
    protected boolean isBelongToChild(TreeNode treeNode, TreeNode curTreeNode) {
        if (TextUtils.isEmpty(treeNode.getId())) {
            return true;
        }
        return treeNode.getId().startsWith(curTreeNode.getId());
    }

    /**
     * 回调
     */
    public interface Callback {

        /**
         * 当折叠时回调
         */
        default void onFold(TreeNode itemInfo, int position) {

        }

        /**
         * 当展开时回调
         */
        void onOpen(TreeNode itemInfo, int position);

        /**
         * 点击item项回调
         */
        void onItemClick(TreeNode itemInfo, int position);


    }
}
