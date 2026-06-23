package com.richard.library.net.ftp.common;

/**
 * @author: Richard
 * @createDate: 2026/6/8 10:42
 * @version: 1.0
 * @description: 文件传输进度监听器
 */
public interface TransferProgressListener {

    /**
     * 文件传输进度(主线程回调)
     *
     * @param filename    文件名
     * @param transferred 已传输字节数
     * @param total       总字节数
     */
    void onProgress(String filename, long transferred, long total);


    /**
     * 取消传输(主线程回调)
     */
    void onCancel(String fileName);

    /**
     * 文件传输完成(主线程回调)
     *
     * @param filename 文件名
     */
    void onComplete(String filename);

    /**
     * 文件传输错误(主线程回调)
     *
     * @param filename 文件名
     * @param error    错误信息
     */
    void onError(String filename, String error);

}
