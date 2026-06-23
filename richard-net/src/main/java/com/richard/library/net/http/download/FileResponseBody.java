package com.richard.library.net.http.download;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


/**
 * <pre>
 * Description : 下载文件进度监听
 * Author : admin-richard
 * Date : 2019-11-22 12:51
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-11-22 12:51     admin-richard         new file.
 * </pre>
 */
public final class FileResponseBody extends ResponseBody {

    /**
     * 实际请求体
     */
    private final ResponseBody mResponseBody;

    /**
     * 下载回调接口
     */
    private final ProgressCallback progressCallback;

    /**
     * BufferedSource
     */
    private BufferedSource mBufferedSource;

    public FileResponseBody(ResponseBody responseBody, ProgressCallback progressCallback) {
        super();
        this.mResponseBody = responseBody;
        this.progressCallback = progressCallback;
    }

    @NonNull
    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    /**
     * 回调进度接口
     *
     * @return Source
     */
    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                progressCallback.onProgress(mResponseBody.contentLength(), totalBytesRead);
                return bytesRead;
            }
        };
    }
}
