package com.richard.library.net.http.request;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * @author: Richard
 * @createDate: 2024/9/6 16:31
 * @version: 1.0
 * @description: http网络请求日志记录拦截器
 */
public class LoggerInterceptor implements Interceptor {

    private final LogCallback callback;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS ", Locale.getDefault());

    /**
     * 日志记录标识header key
     */
    public static final String KEY_LOG_FLAG = "Log-Flag";

    /**
     * 忽略记录请求body日志
     */
    public static final String LOG_IGNORE_REQ_BODY = "ignore_req_body";

    /**
     * 忽略记录响应body日志
     */
    public static final String LOG_IGNORE_RES_BODY = "ignore_res_body";

    /**
     * 日志文件记录忽略等级
     */
    public interface IgnoreLevel {
        /**
         * 不忽略
         */
        int None = 0;

        /**
         * 全部忽略(不记录日志)
         */
        int All = 1;

        /**
         * 仅忽略请求body日志
         */
        int Request_Body = 2;

        /**
         * 仅忽略响应body日志
         */
        int Response_Body = 3;

        /**
         * 忽略请求和响应body日志
         */
        int All_Body = 4;
    }

    /**
     * 构造方法
     */
    LoggerInterceptor(@NonNull LogCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        //日志忽略等级
        int ignoreLevel = IgnoreLevel.All;

        //是否忽略日志记录
        String flag = request.header(KEY_LOG_FLAG);

        //移除日志标识header
        if (flag != null) {
            request = request.newBuilder()
                    .headers(request.headers().newBuilder().removeAll(KEY_LOG_FLAG).build())
                    .build();
            if (flag.equals(LOG_IGNORE_REQ_BODY)) {
                ignoreLevel = IgnoreLevel.Request_Body;
            } else {
                ignoreLevel = IgnoreLevel.Response_Body;
            }
        } else {
            try {
                ignoreLevel = callback.ignoreLevel(request, request.tag(ParamsTag.class));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (ignoreLevel == IgnoreLevel.All) {
            return chain.proceed(request);
        }

        //--------收集请求数据日志
        String requestTime = dateFormat.format(new Date());
        RequestBody requestBody = request.body();
        StringBuilder logBuilder = new StringBuilder();

        Buffer buffer = new Buffer();
        MediaType contentType = null;
        Charset charset = null;
        boolean isLogRequestBody = true;

        if (requestBody != null) {
            contentType = requestBody.contentType();
        }

        if (contentType != null) {
            charset = contentType.charset(StandardCharsets.UTF_8);
            isLogRequestBody = !("multipart".equalsIgnoreCase(contentType.type()) && "form-data".equalsIgnoreCase(contentType.subtype()));
        }

        if (isLogRequestBody) {
            isLogRequestBody = ignoreLevel == IgnoreLevel.None || ignoreLevel == IgnoreLevel.Response_Body;
        }

        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }

        if (isLogRequestBody && requestBody != null) {
            requestBody.writeTo(buffer);
        }

        //请求参数报文
        String requestBodyContent = "";
        if (isLogRequestBody) {
            try {
                requestBodyContent = buffer.readString(charset);
                requestBodyContent = URLDecoder.decode(requestBodyContent, "UTF-8");
            } catch (Throwable ignored) {

            }
        }

        String requestParams = isLogRequestBody
                ? requestBodyContent
                : "未记录请求body数据";

        logBuilder.append(String.format("\n请求信息 | %s: ", requestTime)).append(String.format("Request{method=%s, url=%s}", request.method(), request.url())).append("\n");
        boolean isLogHeader = false;
        try {
            isLogHeader = callback.isLogHeader();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        StringBuilder requestHeaderBuilder = null;
        if (isLogHeader) {
            requestHeaderBuilder = new StringBuilder();
            requestHeaderBuilder.append(String.format("请求头部 | %s: \n", requestTime));
            if (contentType != null && request.headers().size() > 0) {
                requestHeaderBuilder.append("Content-Type: ".concat(contentType.toString())).append(request.headers()).append("\n");
            } else if (contentType != null) {
                requestHeaderBuilder.append("Content-Type: ".concat(contentType.toString())).append("\n");
            } else if (request.headers().size() > 0) {
                requestHeaderBuilder.append(request.headers()).append("\n");
            }

            if (requestBody != null) {
                requestHeaderBuilder.append("Content-Length: ".concat(String.valueOf(requestBody.contentLength()))).append("\n");
            }

            logBuilder.append(requestHeaderBuilder);
        }
        logBuilder.append(String.format("请求数据 | %s: ", requestTime)).append(requestParams);
        try {
            callback.onRequestLog(request, logBuilder.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }


        //--------收集响应数据日志
        String responseTime;
        long t1 = System.nanoTime();
        long t2;
        Response response;
        try {
            response = chain.proceed(chain.request());
            t2 = System.nanoTime();
            responseTime = dateFormat.format(new Date());
        } catch (Throwable e) {
            t2 = System.nanoTime();
            responseTime = dateFormat.format(new Date());
            logBuilder.append(String.format(
                    Locale.getDefault()
                    , "\n请求失败 | %s: {time=%.1fms, url=%s , error=%s}"
                    , responseTime
                    , (t2 - t1) / 1e6d
                    , request.url()
                    , e
            ));
            try {
                callback.log(request, logBuilder.toString());
            } catch (Throwable th) {
                th.printStackTrace();
            }
            throw e;
        }
        logBuilder.setLength(0);

        boolean isLogResponseBody = ignoreLevel == IgnoreLevel.None || ignoreLevel == IgnoreLevel.Request_Body;

        MediaType mediaType = response.body() == null ? null : response.body().contentType();
        String content = isLogResponseBody
                ? response.body() == null ? "" : response.body().string()
                : "未记录响应body数据";

        logBuilder.append(String.format("\n请求信息 | %s: ", requestTime)).append(String.format(
                        Locale.getDefault()
                        , "Request{method=%s, code=%s, time=%.1fms, isRedirect=%s, url=%s}"
                        , request.method()
                        , response.code()
                        , (t2 - t1) / 1e6d
                        , response.isRedirect()
                        , response.request().url()
                ))
                .append("\n")
                .append(String.format("请求数据 | %s: ", requestTime)).append(requestParams).append("\n");
        if (isLogHeader) {
            logBuilder.append(requestHeaderBuilder != null ? requestHeaderBuilder : "");
            logBuilder.append(String.format("响应头部 | %s: \n", responseTime)).append(response.headers());
        }
        logBuilder.append(String.format("响应数据 | %s: ", responseTime)).append(content);
        try {
            callback.log(request, logBuilder.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (!isLogResponseBody || mediaType == null) {
            return response.newBuilder()
                    .build();
        }

        return response.newBuilder()
                .body(ResponseBody.create(content, mediaType))
                .build();
    }
}
