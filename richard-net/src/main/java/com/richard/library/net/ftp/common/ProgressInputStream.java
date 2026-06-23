package com.richard.library.net.ftp.common;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author: Richard
 * @createDate: 2026/6/8 10:37
 * @version: 1.0
 * @description: 带进度的输入流
 */
public class ProgressInputStream extends InputStream {
    private final InputStream inputStream;
    private final String filename;
    private final long totalSize;
    private long transferred = 0;
    private long lastUpdateTime = 0;
    private long updateProgressInterval = 200;  //更新进度频率，单位毫秒
    private StreamProgressListener listener;    //进度监听器
    private CancelChecker cancelChecker;        //取消检查器

    public ProgressInputStream(InputStream inputStream, String filename, long totalSize) {
        this.inputStream = inputStream;
        this.filename = filename;
        this.totalSize = totalSize;
    }

    /**
     * 设置更新进度频率，单位毫秒
     */
    public void setUpdateProgressInterval(long updateProgressInterval) {
        this.updateProgressInterval = updateProgressInterval;
    }

    /**
     * 设置进度监听器
     */
    public void setListener(StreamProgressListener listener) {
        this.listener = listener;
    }

    /**
     * 设置取消检查器
     */
    public void setCancelChecker(CancelChecker cancelChecker) {
        this.cancelChecker = cancelChecker;
    }

    @Override
    public int read() throws IOException {
        if (cancelChecker != null && cancelChecker.isCanceled()) {
            throw new IOException("Transfer canceled");
        }

        int data = inputStream.read();
        if (data != -1) {
            transferred++;
            updateProgress();
        }
        return data;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (cancelChecker != null && cancelChecker.isCanceled()) {
            throw new IOException("Transfer canceled");
        }

        int bytesRead = inputStream.read(b);
        if (bytesRead != -1) {
            transferred += bytesRead;
            updateProgress();
        }
        return bytesRead;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (cancelChecker != null && cancelChecker.isCanceled()) {
            throw new IOException("Transfer canceled");
        }

        int bytesRead = inputStream.read(b, off, len);
        if (bytesRead != -1) {
            transferred += bytesRead;
            updateProgress();
        }
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    private void updateProgress() {
        if (listener != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime >= updateProgressInterval) {
                lastUpdateTime = currentTime;
                listener.onProgress(filename, transferred, totalSize);
            }
        }
    }
}

