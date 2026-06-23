package com.richard.library.net.ftp.common;

/**
 * @author: Richard
 * @createDate: 2026/6/8 10:46
 * @version: 1.0
 * @description: 输入或输出流进度监听
 */
public interface StreamProgressListener {

    /**
     * 进度更新
     *
     * @param filename    文件名
     * @param transferred 已传输字节数
     * @param totalSize   总字节数
     */
    void onProgress(String filename, long transferred, long totalSize);

}
