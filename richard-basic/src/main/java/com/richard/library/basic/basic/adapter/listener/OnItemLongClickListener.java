package com.richard.library.basic.basic.adapter.listener;

/**
 * <pre>
 * Description : item长按事件
 * Author : admin-richard
 * Date : 2022/8/16 10:13
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/8/16 10:13      admin-richard         new file.
 * </pre>
 */
@FunctionalInterface
public interface OnItemLongClickListener<T> {

    void onItemLongClick(T itemInfo, int position);

}
