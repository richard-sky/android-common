package com.richard.library.basic.basic.adapter.listener;

/**
 * <pre>
 * Description : 列表Item点击事件通用接口
 * Author : admin-richard
 * Date : 2022/8/29 16:26
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/8/29 16:26      admin-richard         new file.
 * </pre>
 */
@FunctionalInterface
public interface OnItemClickListener<T> {

    void onItemClick(T itemInfo, int position);

}
