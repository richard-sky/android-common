package com.richard.library.net.ftp.common;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: Richard
 * @createDate: 2026/6/8 10:51
 * @version: 1.0
 * @description: 带进度的输出流
 */
public class ProgressOutputStream extends OutputStream {
    private final OutputStream outputStream;
    private final String filename;
    private final long totalSize;
    private long transferred = 0;
    private long lastUpdateTime = 0;
    private long updateProgressInterval = 200;  //更新进度的间隔时间,默认200毫秒
    private StreamProgressListener listener;    //进度监听器
    private CancelChecker cancelChecker;        //取消检查器

    public ProgressOutputStream(OutputStream outputStream, String filename, long totalSize) {
        this.outputStream = outputStream;
        this.filename = filename;
        this.totalSize = totalSize;
    }

    /**
     * 设置更新进度的间隔时间,单位毫秒
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
    public void write(int b) throws IOException {
        if (cancelChecker != null && cancelChecker.isCanceled()) {
            throw new IOException("Transfer canceled");
        }

        outputStream.write(b);
        transferred++;
        updateProgress();
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (cancelChecker != null && cancelChecker.isCanceled()) {
            throw new IOException("Transfer canceled");
        }

        outputStream.write(b);
        transferred += b.length;
        updateProgress();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (cancelChecker != null && cancelChecker.isCanceled()) {
            throw new IOException("Transfer canceled");
        }

        outputStream.write(b, off, len);
        transferred += len;
        updateProgress();
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
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
