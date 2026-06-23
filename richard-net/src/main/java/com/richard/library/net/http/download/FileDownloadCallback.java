package com.richard.library.net.http.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.richard.library.context.util.FileUtil;
import com.richard.library.net.http.dict.ResponseCode;
import com.richard.library.net.http.request.HttpError;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Callback;
import okhttp3.ResponseBody;

/**
 * <pre>
 * Description : 下载文件回调
 * Author : admin-richard
 * Date : 2019-11-22 13:27
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-11-22 13:27     admin-richard         new file.
 * </pre>
 */
public abstract class FileDownloadCallback implements Callback, ProgressCallback {

    private final File saveFile;
    private final SaveFileThread saveFileThread = new SaveFileThread();

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 2:
                    Long[] data = (Long[]) msg.obj;
                    onDownloadProgress(data[0], data[1]);
                    break;
                case 1:
                    onSuccess(saveFile);
                    break;
                case 0:
                    onFailure(msg.obj.toString(), String.valueOf(msg.arg1));
                    break;
            }
        }
    };

    public FileDownloadCallback(File saveFile) {
        this.saveFile = saveFile;
    }

    @Override
    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
        if (response.code() != 200) {
            handler.sendMessage(handler.obtainMessage(0, response.code(), 0, HttpError.convertErrorMessage(response.code())));
            return;
        }

        if (response.body() == null) {
            handler.sendMessage(handler.obtainMessage(0, -2, 0, ResponseCode.BODY_NULL.getMessage()));
            return;
        }

        try {
            if (saveFileThread.isAlive()) {
                saveFileThread.interrupt();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        saveFileThread.setResponseBody(response.body(), saveFile, handler);
        saveFileThread.start();
    }

    @Override
    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
        if (e instanceof SocketTimeoutException) {
            handler.sendMessage(handler.obtainMessage(0, -6, 0, ResponseCode.REQUEST_TIME_OUT.getMessage()));
            return;
        }
        handler.sendMessage(handler.obtainMessage(0, -3, 0, e.getMessage()));
    }

    /**
     * 保存文件线程
     */
    private static class SaveFileThread extends Thread {

        private ResponseBody responseBody;
        private File saveFile;
        private Handler handler;

        public void setResponseBody(ResponseBody responseBody, File saveFile, Handler handler) {
            this.responseBody = responseBody;
            this.saveFile = saveFile;
            this.handler = handler;
        }


        @Override
        public void run() {
            try {
                FileUtil.saveFile(responseBody.byteStream(), saveFile);
                handler.sendEmptyMessage(1);
            } catch (Throwable t) {
                handler.sendMessage(handler.obtainMessage(0, t.getMessage()));
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
            }
        }
    }

    /**
     * 下载文件进度回调（该方法在子线程中执行）
     *
     * @param total        总大小
     * @param downloadSize 已下载文件大小
     */
    public void onProgress(long total, long downloadSize) {
        handler.sendMessage(handler.obtainMessage(2, new Long[]{total, downloadSize}));
    }

    /**
     * 下载文件进度回调（该方法在UI线程中执行）
     *
     * @param total        总大小
     * @param downloadSize 已下载文件大小
     */
    public void onDownloadProgress(long total, long downloadSize) {
    }

    /**
     * 请求数据成功时的回调
     *
     * @param responseFile 响应数据文件
     */
    public abstract void onSuccess(File responseFile);

    /**
     * 获取数据失败时回调
     *
     * @param message 消息描述
     * @param code    可能为null
     */
    public abstract void onFailure(String message, String code);

}
