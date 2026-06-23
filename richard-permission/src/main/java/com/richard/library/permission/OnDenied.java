package com.richard.library.permission;

/**
 * <pre>
 * Description : 用户拒绝了权限时回调
 * Author : xiejiao
 * Date : 2022/10/10 23:03
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/10/10 23:03      xiejiao         new file.
 * </pre>
 */
@FunctionalInterface
public interface OnDenied {

    /**
     * 用户拒绝了全部权限时回调
     */
    void onDenied();

}
