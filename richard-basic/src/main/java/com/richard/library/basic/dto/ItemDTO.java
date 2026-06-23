package com.richard.library.basic.dto;

import java.io.Serializable;

/**
 * <pre>
 * Description : 通用列表Item
 * Author : admin-richard
 * Date : 2019-06-16 10:30
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-16 10:30      admin-richard         new file.
 * </pre>
 */
public class ItemDTO<T> implements Serializable {

    private static final long serialVersionUID = -139620274612442639L;

    /**
     * item项类型
     */
    private int itemType;

    /**
     * item 名称
     */
    private String name;

    /**
     * 图片url地址
     */
    private String imageURL;

    /**
     * 本地图片资源id
     */
    private int imageResId;

    /**
     * 该item 对于对象数据
     */
    private T data;

    /**
     * 该item项是否处于选中状态
     */
    private boolean isSelected;

    /**
     * 该item项是否处于禁用状态
     */
    private boolean isEnabled = true;

    public ItemDTO(String name, String imageURL) {
        this.name = name;
        this.imageURL = imageURL;
    }

    public ItemDTO(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public ItemDTO(String name, int imageResId, T data) {
        this.name = name;
        this.imageResId = imageResId;
        this.data = data;
    }

    public ItemDTO(String name, String imageURL, T data) {
        this.name = name;
        this.imageURL = imageURL;
        this.data = data;
    }

    public ItemDTO(String name, T data, boolean isEnabled) {
        this.name = name;
        this.data = data;
        this.isEnabled = isEnabled;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
