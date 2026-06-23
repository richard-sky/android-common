package com.richard.library.basic.provider;

import java.io.File;

/**
 * <pre>
 * Description : 文件下载回调
 * Author : xiejiao
 * Date : 2022/11/28 11:11
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/11/28 11:11      xiejiao         new file.
 * </pre>
 */
public interface FileDownloadCallback {

    /**
     * 计算百分比
     *
     * @param total        总大小
     * @param downloadSize 已下载文件大小
     */
    default int getProgress(long total, long downloadSize) {
        return (int) (downloadSize * 1.0 / total * 100.0);
    }

    /**
     * 当前下载进度更新时回调
     *
     * @param total        总字节大小
     * @param downloadSize 已下载字节大小
     */
    void onDownloadProgress(long total, long downloadSize);

    /**
     * 下载文件成功
     */
    void onSuccess(File file);

    /**
     * 下载文件失败
     *
     * @param message 错误消息
     * @param code    错误代码
     */
    void onFailure(String message, String code);

}
