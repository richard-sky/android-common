package com.richard.dev.common.encrpyt;

import androidx.annotation.NonNull;

import com.richard.dev.common.BuildConfig;
import com.richard.library.context.util.ObjectUtilKt;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @author: Administrator
 * @createDate: 2022/3/16 15:33
 * @version: 1.0
 * @description: 数据加解密处理
 */
public class EncryptionDecryptionInterceptor implements Interceptor {

    public static String secret = "";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (!BuildConfig.DEBUG && ObjectUtilKt.isNotEmpty(secret)) {
            String key = secret;

            //对请求数据加密
            Request request = chain.request();
            request = encrypt(request, key);

            //对响应数据解密
            Response response = chain.proceed(request);
            // TODO: 2022/3/16 判断当前响应数据是否需要解密
            response = decrypt(response, key);
            return response;
        }

        return chain.proceed(chain.request());
    }


    /**
     * 加密
     *
     * @param request 加密请求体
     * @param key     加密key
     * @return 加密之后的请求体
     */
    private Request encrypt(Request request, String key) throws IOException {
        //获取请求body，只有@Body 参数的requestBody 才不会为 null
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            Buffer buffer = new okio.Buffer();
            requestBody.writeTo(buffer);

            Charset charset = StandardCharsets.UTF_8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }

            if (charset == null) {
                return request;
            }

            //首先DES再Base64
            byte[] encryptByteArray = DESUtil.encrypt(Base64Util.encrypt(buffer.readString(charset)), key);
            if (encryptByteArray == null) {
                return request;
            }

            String encryptStr = Base64Util.encrypt(encryptByteArray);
            RequestBody body = MultipartBody.create(contentType, encryptStr);

            request = request.newBuilder()
                    .post(body)
                    .build();
        }

        return request;
    }


    /**
     * 解密
     *
     * @param response 响应体
     * @param key      解密key
     * @return 解密之后的响应体
     */
    private Response decrypt(Response response, String key) throws IOException {
        if (response.isSuccessful()) {
            //the response data
            ResponseBody body = response.body();

            if (body == null) {
                return response;
            }

            BufferedSource source = body.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.

            Buffer buffer = source.getBuffer();
            Charset charset = Charset.defaultCharset();
            MediaType contentType = body.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }

            //首先base64解密再DES解密
            String bodyString = null;
            if (charset != null) {
                bodyString = Base64Util.decrypt(DESUtil.decrypt(Base64Util.decrypt2Byte(buffer.clone().readString(charset)), key));
            }
            ResponseBody responseBody = null;
            if (bodyString != null) {
                responseBody = ResponseBody.create(contentType, bodyString);
            }
            response = response.newBuilder().body(responseBody).build();
        }
        return response;
    }
}
