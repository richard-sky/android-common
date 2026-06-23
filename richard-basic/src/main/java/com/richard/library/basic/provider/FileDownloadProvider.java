package com.richard.library.basic.provider;

/**
 * <pre>
 * Description : 文件下载器提供者
 * Author : xiejiao
 * Date : 2022/11/28 11:25
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/11/28 11:25      xiejiao         new file.
 * </pre>
 */
public abstract class FileDownloadProvider {

    protected FileDownloadCallback callback;

    /**
     * provider关联下载回调
     *
     * @param callback 下载回调
     */
    public void attach(FileDownloadCallback callback) {
        this.callback = callback;
    }

    /**
     * 开始下载
     *
     * @param downloadLink 下载链接
     */
    public abstract void startDownload(String downloadLink);

    /**
     * 取消下载
     */
    public abstract void cancelDownload();

}
