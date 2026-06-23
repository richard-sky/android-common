package com.richard.library.permission;

/**
 * <pre>
 * Description : 同意了权限，开始执行的事件
 * Author : xiejiao
 * Date : 2022/10/10 22:58
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/10/10 22:58      xiejiao         new file.
 * </pre>
 */
@FunctionalInterface
public interface PEvent {
    /**
     * 同意了权限，开始执行的事件
     */
    void run();

}
