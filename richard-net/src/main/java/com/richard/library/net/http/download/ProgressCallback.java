package com.richard.library.net.http.download;

/**
 * @author: Richard
 * @createDate: 2026/5/15 15:49
 * @version: 1.0
 * @description: 文件下载进度回调
 */
public interface ProgressCallback {

    /**
     * 计算百分比
     *
     * @param total        总大小字节
     * @param downloadSize 已下载文件大小字节
     */
    default int getProgress(long total, long downloadSize) {
        return (int) (downloadSize * 1.0 / total * 100.0);
    }

    /**
     * 下载进度回调
     *
     * @param total        总大小字节
     * @param downloadSize 已下载文件大小字节
     */
    void onProgress(long total, long downloadSize);

}
