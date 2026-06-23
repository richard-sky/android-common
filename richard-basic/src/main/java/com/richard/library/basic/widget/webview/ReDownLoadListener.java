package com.richard.library.basic.widget.webview;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.webkit.DownloadListener;

import com.richard.library.context.util.DeviceUtil;


/**
 * <pre>
 * Description : WebView 下载监听
 * Author : admin-richard
 * Date : 2018/7/2 11:34
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/7/2 11:34     admin-richard         new file.
 * </pre>
 */
public class ReDownLoadListener implements DownloadListener {

    private Context mContext;

    public ReDownLoadListener(Context context) {
        this.mContext = context;
    }

    /**
     * 下载文件
     */
    private ProgressDialog progressDialog;
    private AsyncTask<Void, Integer, Long> updateTask;

    @Override
    public void onDownloadStart(String url, String s1, String s2, String s3, long l) {
        DeviceUtil.toWebBrowser(url);

       /* //初始化下载进度框
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(mContext.getResources().getString(R.string.loading_str));

        final File mApkFile = new File(mContext.getExternalCacheDir(), System.currentTimeMillis() + ".apk");
        UpdateUtil.clean(mContext);
        updateTask = new UpdateDownloader(new IDownloadAgent() {

            @Override
            public void setError(UpdateError error) {
                Looper.prepare();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, error.getErrorMessage(), Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }

            @Override
            public void onFinish() {
                progressDialog.dismiss();
                UpdateUtil.install(mContext, mApkFile, false);
            }
        }, mContext, url, mApkFile).execute();

        //设置按键点击事件
        progressDialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                if (!updateTask.isCancelled()) {
                    updateTask.cancel(false);
                }
                return true;
            } else {
                return false;
            }
        });*/
    }

    /**
     * 销毁对象
     */
    public void onDestroy() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel(false);
            updateTask = null;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
