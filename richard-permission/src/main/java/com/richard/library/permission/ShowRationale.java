package com.richard.library.permission;

import permissions.dispatcher.PermissionRequest;

/**
 * <pre>
 * Description : 提示用户获取权限的原因
 * Author : xiejiao
 * Date : 2022/10/10 23:00
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/10/10 23:00      xiejiao         new file.
 * </pre>
 */
@FunctionalInterface
public interface ShowRationale {

    /**
     * 提示用户获取权限的原因
     * @param request 权限请求对象
     */
    void showRationale(PermissionRequest request);

}
