package com.richard.library.net.http.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * @author: Richard
 * @createDate: 2025/8/29 16:25
 * @version: 1.0
 * @description: 自定义请求体
 */
public class XRequestBody extends RequestBody {

    private final RequestBody originalBody;
    private final boolean isOneShot;

    public XRequestBody(RequestBody originalBody) {
        this.originalBody = originalBody;
        this.isOneShot = super.isOneShot();
    }

    public XRequestBody(RequestBody originalBody, boolean isOneShot) {
        this.originalBody = originalBody;
        this.isOneShot = isOneShot;
    }

    @Override
    public boolean isOneShot() {
        return this.isOneShot;
    }

    @Override
    public boolean isDuplex() {
        return super.isDuplex();
    }

    @Override
    public long contentLength() throws IOException {
        return this.originalBody.contentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return this.originalBody.contentType();
    }

    @Override
    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {
        this.originalBody.writeTo(bufferedSink);
    }
}
