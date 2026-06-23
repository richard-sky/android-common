package com.richard.dev.common;

import com.richard.library.basic.provider.FileDownloadProvider;
import com.richard.library.context.util.FileUtil;
import com.richard.library.context.util.ThreadUtil;
import com.richard.library.net.http.download.ProgressCallback;
import com.richard.library.net.http.request.Requester;

import java.io.File;
import java.util.concurrent.Future;

/**
 * @author: Richard
 * @createDate: 2023/4/10 10:48
 * @version: 1.0
 * @description: android APP下载器提供者
 */
public class APKFileDownLoadProvider extends FileDownloadProvider {

    private Future<?> downloadTask;

    @Override
    public void startDownload(String downloadLink) {
        downloadTask = ThreadUtil.getCachedPool().submit(() -> {
            try {
                File apkFile = new File(FileUtil.getCacheDir() + "/" + System.currentTimeMillis() + ".apk");
                File responseFile = Requester.create()
                        .url(downloadLink)
                        .get()
                        .requestFile(apkFile, (ProgressCallback) (total, downloadSize) -> {
                            callback.onDownloadProgress(total, downloadSize);
                        });
                callback.onSuccess(responseFile);
            } catch (Throwable e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage(), "-1");
            }
        });
    }

    @Override
    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
    }
}
