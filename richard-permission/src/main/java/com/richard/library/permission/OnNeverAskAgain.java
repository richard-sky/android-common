package com.richard.library.permission;

/**
 * <pre>
 * Description : 用户选择了不再提示申请权限时回调
 * Author : xiejiao
 * Date : 2022/10/10 23:04
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/10/10 23:04      xiejiao         new file.
 * </pre>
 */
@FunctionalInterface
public interface OnNeverAskAgain {

    /**
     * 用户选择了不再提示申请权限时回调
     */
    void onNeverAskAgain();

}
