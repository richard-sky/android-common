package com.richard.library.net.http.request.upload;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;


/**
 * <pre>
 * Description : 文件上传RequestBody
 * Author : admin-richard
 * Date : 2019-05-20 19:52
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-20 19:52     admin-richard         new file.
 * </pre>
 */
public final class FileRequestBody extends RequestBody {


    /**
     * 实际请求体
     */
    private final RequestBody requestBody;
    /**
     * 上传回调接口
     */
    private final UploadFileProgressListener uploadFileProgressListener;
    /**
     * 包装完成的BufferedSink
     */
    private BufferedSink bufferedSink;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    if (uploadFileProgressListener != null) {
                        Long[] data = (Long[]) msg.obj;
                        uploadFileProgressListener.onUploadFileProgress(data[0], data[1]);
                    }
                    break;
            }
        }
    };


    public FileRequestBody(RequestBody requestBody, UploadFileProgressListener uploadFileProgressListener) {
        super();
        this.requestBody = requestBody;
        this.uploadFileProgressListener = uploadFileProgressListener;
    }


    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调
                if (uploadFileProgressListener != null) {
                    handler.sendMessage(handler.obtainMessage(1, new Long[]{contentLength, bytesWritten}));
                }
            }
        };
    }
}
