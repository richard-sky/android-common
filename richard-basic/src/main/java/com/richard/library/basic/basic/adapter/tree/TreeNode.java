package com.richard.library.basic.basic.adapter.tree;

import java.util.List;

/**
 * @author: Richard
 * @createDate: 2024/6/17 9:40
 * @version: 1.0
 * @description: 树形节点
 */
public class TreeNode implements Comparable<TreeNode> {

    public static final int ITEM_TYPE_PARENT = 0;
    public static final int ITEM_TYPE_CHILD = 1;
    private int itemType;// 显示类型

    private String id;
    private String name;
    private int treeDepth = 0;// 路径的深度
    private boolean open;// 是否展开
    private List<TreeNode> children;
    private Object data;

    public TreeNode(int itemType, String name, String id, int treeDepth, Object data, List<TreeNode> children) {
        super();
        this.itemType = itemType;
        this.id = id;
        this.name = name;
        this.treeDepth = treeDepth;
        this.children = children;
        this.data = data;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTreeDepth() {
        return treeDepth;
    }

    public void setTreeDepth(int treeDepth) {
        this.treeDepth = treeDepth;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public int compareTo(TreeNode another) {
        return this.getName().compareTo(another.getName());
    }

}