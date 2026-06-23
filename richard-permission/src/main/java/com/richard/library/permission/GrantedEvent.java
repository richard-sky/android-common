package com.richard.library.permission;

import java.util.List;

/**
 * @author: Richard
 * @createDate: 2026/6/1 11:57
 * @version: 1.0
 * @description: none
 */
@FunctionalInterface
public interface GrantedEvent extends PEvent {

    @Override
    default void run() {

    }

    /**
     * 当存在已授权的权限的时候回调
     *
     * @param grantedPermissions 已授权的权限
     * @param deniedPermissions  未授权的权限
     * @param isAllGranted       是否已全部授权
     */
    void onGranted(List<String> grantedPermissions, List<String> deniedPermissions, boolean isAllGranted);

}
