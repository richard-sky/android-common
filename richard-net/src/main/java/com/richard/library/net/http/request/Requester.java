package com.richard.library.net.http.request;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.TypeReference;
import com.richard.library.context.util.FileUtil;
import com.richard.library.context.util.JsonKt;
import com.richard.library.context.util.NetUtil;
import com.richard.library.context.util.ObjectUtilKt;
import com.richard.library.context.util.StringUtilKt;
import com.richard.library.context.util.URLUtil;
import com.richard.library.net.http.dict.ResponseCode;
import com.richard.library.net.http.download.ProgressCallback;
import com.richard.library.net.http.exception.HttpException;
import com.richard.library.net.http.model.UploadFile;
import com.richard.library.net.http.util.ResponseBodyReader;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * @author: Richard
 * @createDate: 2024/9/3 10:44
 * @version: 1.0
 * @description: 网络请求对象
 */
public class Requester extends Request.Builder {

    private Integer requestType;
    private long timeout;
    private TimeUnit timeoutUnit;
    private RequestBody requestBody;
    private String method = "GET";
    private String url;
    private List<UploadFile> files;
    private Object params;
    private boolean isOneShot = false;
    private Call call;


    public static Requester create() {
        return new Requester();
    }

    /**
     * 设置当前请求body是否为一次性请求，不受okhttp重试机制的影响，始终只会请求一次
     */
    public Requester oneShot(boolean isOneShot) {
        this.isOneShot = isOneShot;
        return this;
    }

    /**
     * 设置当前请求超时时间
     *
     * @param timeout  时间数
     * @param timeUnit 时间数单位
     */
    public Requester timeout(long timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeoutUnit = timeUnit;
        return this;
    }

    /**
     * 取消请求
     */
    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    //-----------------------------------------请求--------------------------------------------------

    /**
     * 生成并返回请求call
     */
    private Call newCall() {
        this.applyRequestBody();
        this.tag(ParamsTag.class, new ParamsTag(params));
        call = RequestClient.get().client().newCall(this.build());
        if (timeout > 0) {
            call.timeout().timeout(timeout, timeoutUnit);
        }
        return call;
    }

    /**
     * 异步请求
     *
     * @param callback 请求回调
     */
    public void request(Callback callback) {
        newCall().enqueue(callback);
    }

    /**
     * 同步请求
     */
    public Response request() {
        try {
            return newCall().execute();
        } catch (ConnectException e) {
            e.printStackTrace();
            throw new HttpException(ResponseCode.NET_ERROR.getMessage(), e);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new HttpException("请稍后再试", e);
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            throw new HttpException(ResponseCode.REQUEST_TIME_OUT.getMessage(), e);
        } catch (IOException e) {
            e.printStackTrace();
            if (!NetUtil.isConnected()) {
                throw new HttpException(ResponseCode.NET_ERROR.getMessage(), e);
            }
            throw new HttpException("请求网络失败或网络不稳定,请检查网络是否正常", e);
        }
    }

    /**
     * 同步请求
     */
    public <T> T request(@NonNull TypeReference<T> type) throws Throwable {
        try (Response response = this.request()) {
            return ResponseBodyReader.read(response, type);
        }
    }

    /**
     * 同步请求
     */
    public <T> T requestXML(@NonNull TypeReference<T> type) throws Throwable {
        try (Response response = this.request()) {
            return ResponseBodyReader.read(response, type, true);
        }
    }

    /**
     * 同步请求
     */
    public File requestFile(@NonNull File saveFile) throws Throwable {
        return requestFile(saveFile, null);
    }

    /**
     * 同步请求
     */
    public File requestFile(@NonNull File saveFile, ProgressCallback callback) throws Throwable {
        if (RequestClient.get().isDefaultLogInterceptor()) {
            this.addHeader(LoggerInterceptor.KEY_LOG_FLAG, LoggerInterceptor.LOG_IGNORE_RES_BODY);
        }

        if (callback != null) {
            this.tag(ProgressCallback.class, callback);
        }

        try (Response response = this.request()) {
            if (response.code() != 200) {
                throw new HttpException(HttpError.convertErrorMessage(response.code()));
            }

            if (response.body() == null) {
                throw new HttpException(ResponseCode.BODY_NULL.getMessage());
            }

            //保存文件
            return FileUtil.saveFile(response.body().byteStream(), saveFile);
        }
    }

    //------------------------------------------请求body构造------------------------------------------

    /**
     * get请求
     */
    public Requester get(Map<String, Object> params) {
        this.requestType = RequestType.Get_Params;
        this.params = params;
        this.method = "GET";
        return this;
    }

    /**
     * post json
     */
    public Requester postJson(Object params) {
        this.requestType = RequestType.Post_Json;
        this.params = params;
        this.method = "POST";
        return this;
    }

    /**
     * post表单请求
     */
    public Requester postForm(Object params) {
        this.requestType = RequestType.Post_Form;
        this.params = params;
        this.method = "POST";
        return this;
    }

    /**
     * post xml
     */
    public Requester postXml(Object params) {
        this.requestType = RequestType.Post_Xml;
        this.params = params;
        this.method = "POST";
        return this;
    }

    /**
     * put请求
     */
    public Requester putJson(Object params) {
        this.requestType = RequestType.Put_Json;
        this.params = params;
        this.method = "PUT";
        return this;
    }

    /**
     * put表单请求
     */
    public Requester putForm(Object params) {
        this.requestType = RequestType.Put_Form;
        this.params = params;
        this.method = "PUT";
        return this;
    }

    /**
     * put xml
     */
    public Requester putXml(Object params) {
        this.requestType = RequestType.Put_Xml;
        this.params = params;
        this.method = "PUT";
        return this;
    }

    /**
     * patch json
     */
    public Requester patchJson(Object params) {
        this.requestType = RequestType.Patch_Json;
        this.params = params;
        this.method = "PATCH";
        return this;
    }

    /**
     * patch表单请求
     */
    public Requester patchForm(Object params) {
        this.requestType = RequestType.Patch_Form;
        this.params = params;
        this.method = "PATCH";
        return this;
    }

    /**
     * patch xml
     */
    public Requester patchXml(Object params) {
        this.requestType = RequestType.Patch_Xml;
        this.params = params;
        this.method = "PATCH";
        return this;
    }

    /**
     * delete json
     */
    public Requester deleteJson(Object params) {
        this.requestType = RequestType.Delete_Json;
        this.params = params;
        this.method = "DELETE";
        return this;
    }

    /**
     * delete请求
     */
    public Requester deleteXml(Object params) {
        this.requestType = RequestType.Delete_Xml;
        this.params = params;
        this.method = "DELETE";
        return this;
    }

    /**
     * delete表单请求
     */
    public Requester deleteForm(Object params) {
        this.requestType = RequestType.Delete_Form;
        this.params = params;
        this.method = "DELETE";
        return this;
    }

    /**
     * 上传文件
     */
    public Requester uploadFile(List<UploadFile> files) {
        this.uploadFile(files, null);
        return this;
    }

    /**
     * 上传文件
     */
    public Requester uploadFile(List<UploadFile> files, Object params) {
        this.requestType = RequestType.Upload_File;
        this.files = files;
        this.params = params;
        this.method = "POST";

        if (RequestClient.get().isDefaultLogInterceptor()) {
            addHeader(LoggerInterceptor.KEY_LOG_FLAG, LoggerInterceptor.LOG_IGNORE_REQ_BODY);
        }
        return this;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 创建请求body
     *
     * @param data         请求参数数据
     * @param isConvertXml 是否转换为xml格式
     */
    private RequestBody createRequestBody(Object data, boolean isConvertXml) {
        String body;
        if (isConvertXml) {
            body = getXmlParams(data);
        } else {
            if (data != null && JsonKt.isEntity(data.getClass())) {
                body = JsonKt.toJson(data);
            } else {
                body = ObjectUtilKt.toString(data);
            }
        }

        if (isConvertXml) {
            return new XRequestBody(RequestBody.create(StringUtilKt.defaultIfEmpty(body, "")
                    , MediaType.parse("application/xml; charset=UTF-8")), isOneShot);
        } else {
            return new XRequestBody(RequestBody.create(StringUtilKt.defaultIfEmpty(body, "")
                    , MediaType.parse("application/json; charset=UTF-8")), isOneShot);
        }
    }

    /**
     * 创建表单请求body
     *
     * @param data 请求参数数据
     */
    private RequestBody createRequestFormBody(Object data) {
        Map<String, Object> map = JsonKt.toJSONObject(data);
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null && !map.isEmpty()) {
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            for (Map.Entry<String, Object> item : entrySet) {
                if (item.getValue() == null) {
                    builder.add(item.getKey(), "");
                    continue;
                }

                if (JsonKt.isEntity(item.getValue().getClass())) {
                    builder.add(item.getKey(), Objects.requireNonNull(JsonKt.toJson(item.getValue())));
                } else {
                    builder.add(item.getKey(), ObjectUtilKt.toString(item.getValue()));
                }
            }
        }

        return new XRequestBody(builder.build(), isOneShot);
    }

    /**
     * 获取xml请求参数
     */
    private String getXmlParams(Object params) {
        if (params == null) {
            return null;
        }

        if (JsonKt.isEntity(params.getClass())) {
            return new JsonToXml.Builder(JsonKt.toJson(params)).build().toString();
        }

        String paramsStr = ObjectUtilKt.toString(params);
        if (JsonKt.isJson(paramsStr)) {
            return new JsonToXml.Builder(paramsStr).build().toString();
        }
        return paramsStr;
    }

    /**
     * 把File对象转化成MultipartBody
     */
    private RequestBody filesToMultipartBody(List<UploadFile> files, Object params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addPart(this.createRequestFormBody(params));

        for (UploadFile uploadFile : files) {
            File localFile = new File(uploadFile.getPath());
            MediaType mediaType = MediaType.parse(getMimeType(localFile));
            builder.addFormDataPart(
                    TextUtils.isEmpty(uploadFile.getName())
                            ? "file"
                            : uploadFile.getName()
                    , localFile.getName()
                    , RequestBody.create(localFile, mediaType)
            );
        }

        builder.setType(MultipartBody.FORM);
        return new XRequestBody(builder.build(), isOneShot);
    }

    /**
     * 获取文件mineType
     */
    private String getMimeType(File file) {
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null && !type.isEmpty()) {
            return type;
        }
        return "file/*";
    }

    /**
     * 获取文件扩展名
     */
    private String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.isEmpty() || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    //------------------------------------------重写父类方法------------------------------------------

    @NonNull
    @Override
    public Requester addHeader(@NonNull String name, @NonNull String value) {
        super.addHeader(name, value);
        return this;
    }

    @NonNull
    @Override
    public Request build() {
        return super.build();
    }

    @NonNull
    @Override
    public Requester cacheControl(@NonNull CacheControl cacheControl) {
        super.cacheControl(cacheControl);
        return this;
    }

    @NonNull
    @Override
    public Requester header(@NonNull String name, @NonNull String value) {
        super.header(name, value);
        return this;
    }

    @NonNull
    @Override
    public Requester headers(@NonNull Headers headers) {
        super.headers(headers);
        return this;
    }

    public Requester headers(Map<String, String> headerMap) {
        if (headerMap == null || headerMap.isEmpty()) {
            return this;
        }

        Set<Map.Entry<String, String>> entrySet = headerMap.entrySet();
        for (Map.Entry<String, String> item : entrySet) {
            this.addHeader(item.getKey(), item.getValue());
        }
        return this;
    }

    @NonNull
    @Override
    public Requester removeHeader(@NonNull String name) {
        super.removeHeader(name);
        return this;
    }

    @NonNull
    @Override
    public <T> Requester tag(@NonNull Class<? super T> type, @Nullable T tag) {
        super.tag(type, tag);
        return this;
    }

    @NonNull
    @Override
    public Requester tag(@Nullable Object tag) {
        super.tag(tag);
        return this;
    }

    public Requester url(@NonNull String url, Map<String, Object> params) {
        this.url = url;
        if (StringUtilKt.startsWithAnyX(url, "http://", "https://")) {
            super.url(this.completionHttpQueryParameter(params));
        } else {
            super.url(this.completionOtherParameter(params));
        }
        return this;
    }

    @NonNull
    @Override
    public Requester url(@NonNull URL url) {
        super.url(url);
        this.url = url.toString();
        return this;
    }

    @NonNull
    @Override
    public Requester url(@NonNull String url) {
        super.url(url);
        this.url = url;
        return this;
    }

    @NonNull
    @Override
    public Requester url(@NonNull HttpUrl url) {
        super.url(url);
        this.url = url.toString();
        return this;
    }

    @NonNull
    @Override
    public Requester delete(@Nullable RequestBody body) {
        super.delete(body);
        return this;
    }

    @NonNull
    @Override
    public Requester get() {
        super.get();
        return this;
    }

    @NonNull
    @Override
    public Requester head() {
        super.head();
        return this;
    }

    @NonNull
    @Override
    public Requester patch(@NonNull RequestBody body) {
        super.patch(body);
        return this;
    }

    @NonNull
    @Override
    public Requester post(@NonNull RequestBody body) {
        super.post(body);
        return this;
    }

    @NonNull
    @Override
    public Requester put(@NonNull RequestBody body) {
        super.put(body);
        return this;
    }

    @NonNull
    @Override
    public Requester method(@NonNull String method, @Nullable RequestBody body) {
        this.requestType = ObjectUtilKt.getOrDefault(requestType, RequestType.Other);
        this.method = ObjectUtilKt.getOrDefault(method, "GET");
        this.requestBody = body;
        super.method(method, body);
        return this;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 应用请求body
     */
    private void applyRequestBody() {
        switch (ObjectUtilKt.getOrDefault(requestType, RequestType.Other)) {
            case RequestType.Post_Json:
                super.post(this.createRequestBody(params, false));
                break;
            case RequestType.Put_Json:
                super.put(this.createRequestBody(params, false));
                break;
            case RequestType.Patch_Json:
                super.patch(this.createRequestBody(params, false));
                break;
            case RequestType.Delete_Json:
                super.delete(this.createRequestBody(params, false));
                break;
            case RequestType.Post_Form:
                super.post(this.createRequestFormBody(params));
                break;
            case RequestType.Put_Form:
                super.put(this.createRequestFormBody(params));
                break;
            case RequestType.Patch_Form:
                super.patch(this.createRequestFormBody(params));
                break;
            case RequestType.Delete_Form:
                super.delete(this.createRequestFormBody(params));
                break;
            case RequestType.Post_Xml:
                super.post(this.createRequestBody(params, true));
                break;
            case RequestType.Put_Xml:
                super.put(this.createRequestBody(params, true));
                break;
            case RequestType.Patch_Xml:
                super.patch(this.createRequestBody(params, true));
                break;
            case RequestType.Delete_Xml:
                super.delete(this.createRequestBody(params, true));
                break;
            case RequestType.Upload_File:
                super.post(this.filesToMultipartBody(files, params));
                break;
            case RequestType.Get_Params:
                if (params == null) {
                    super.get();
                    break;
                }
                if (StringUtilKt.startsWithAnyX(url, "http://", "https://")) {
                    super.url(this.completionHttpQueryParameter(params));
                } else {
                    super.url(this.completionOtherParameter(params));
                }
                super.get();
                break;
            case RequestType.Other:
            default:
                if (requestBody != null && !this.isTextType(requestBody.contentType())) {
                    super.method(method, new XRequestBody(requestBody, isOneShot));
                    break;
                }

                String content = null;
                MediaType contentType = null;
                if (requestBody != null) {
                    contentType = requestBody.contentType();
                    Charset charset = null;

                    if (contentType != null) {
                        charset = contentType.charset(StandardCharsets.UTF_8);
                    }

                    if (charset == null) {
                        charset = StandardCharsets.UTF_8;
                    }

                    Buffer buffer = new Buffer();
                    try {
                        requestBody.writeTo(buffer);
                        content = URLDecoder.decode(buffer.readString(charset), "UTF-8");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (content == null) {
                    super.method(method, null);
                    break;
                }

                if (JsonKt.isJson(content)) {
                    if (contentType == null) {
                        contentType = MediaType.parse("application/json; charset=UTF-8");
                    }
                    super.method(method, new XRequestBody(RequestBody.create(content, contentType), isOneShot));
                } else if (JsonKt.isXml(content)) {
                    if (contentType == null) {
                        contentType = MediaType.parse("application/xml; charset=UTF-8");
                    }
                    super.method(method, new XRequestBody(RequestBody.create(content, contentType), isOneShot));
                } else {
                    if (contentType == null) {
                        contentType = MediaType.parse("text/plain; charset=UTF-8");
                    }
                    super.method(method, new XRequestBody(RequestBody.create(content, contentType), isOneShot));
                }
        }
    }

    /**
     * 补全http地址后的参数
     */
    private HttpUrl completionHttpQueryParameter(Object params) {
        if (url == null) {
            throw new RuntimeException("未指定请求URL地址");
        }

        if (params == null) {
            return HttpUrl.get(url);
        }

        Map<String, Object> map = null;
        if (JsonKt.isEntity(params.getClass())) {
            map = JsonKt.toJSONObject(params);
        } else if ((params instanceof String) && JsonKt.isJson(ObjectUtilKt.toString(params))) {
            map = JsonKt.toJSONObject(params);
        }

        if (map == null || map.isEmpty()) {
            return HttpUrl.get(url);
        }

        HttpUrl.Builder urlBuilder = HttpUrl.get(url).newBuilder();
        Set<Map.Entry<String, Object>> entrySet = map.entrySet();

        for (Map.Entry<String, Object> item : entrySet) {
            if (item.getValue() == null) {
                urlBuilder.addQueryParameter(item.getKey(), "");
                continue;
            }

            if (JsonKt.isEntity(item.getValue().getClass())) {
                urlBuilder.addQueryParameter(item.getKey(), JsonKt.toJson(item.getValue()));
            } else {
                urlBuilder.addQueryParameter(item.getKey(), ObjectUtilKt.toString(item.getValue()));
            }
        }

        HttpUrl httpUrl = urlBuilder.build();
        url = httpUrl.toString();

        return httpUrl;
    }

    /**
     * 补全其他url地址后的参数
     */
    private String completionOtherParameter(Object params) {
        if (url == null) {
            throw new RuntimeException("未指定请求URL地址");
        }

        if (params == null) {
            return url;
        }

        Map<String, Object> map = null;
        if (JsonKt.isEntity(params.getClass())) {
            map = JsonKt.toJSONObject(params);
        } else if ((params instanceof String) && JsonKt.isJson(ObjectUtilKt.toString(params))) {
            map = JsonKt.toJSONObject(params);
        }

        if (map == null || map.isEmpty()) {
            return url;
        }

        return URLUtil.completeParams(url, map);
    }

    /**
     * 验证MediaType 是否为文本类型
     */
    private boolean isTextType(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }

        String type = mediaType.type();
        String subtype = mediaType.subtype();

        // 判断是否是文本类型
        return "text".equalsIgnoreCase(type) || //
                "application".equalsIgnoreCase(type) && "json".equalsIgnoreCase(subtype) || // JSON文本
                "application".equalsIgnoreCase(type) && "xml".equalsIgnoreCase(subtype); // XML文本
    }

    /**
     * 请求类型标识
     */
    private interface RequestType {

        int Other = 0;

        int Post_Json = 1;
        int Post_Form = 2;
        int Post_Xml = 3;

        int Put_Json = 4;
        int Put_Form = 5;
        int Put_Xml = 6;

        int Patch_Json = 7;
        int Patch_Form = 8;
        int Patch_Xml = 9;

        int Delete_Json = 10;
        int Delete_Form = 11;
        int Delete_Xml = 12;

        int Upload_File = 13;

        int Get_Params = 14;
    }
}
