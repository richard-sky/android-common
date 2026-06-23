package com.richard.library.net.http.util;

import android.text.TextUtils;

import com.alibaba.fastjson.TypeReference;
import com.richard.library.context.util.JsonKt;
import com.richard.library.net.http.exception.DataErrorException;
import com.richard.library.net.http.exception.ResponseEmptyException;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * @author: Richard
 * @createDate: 2024/9/6 10:24
 * @version: 1.0
 * @description: 请求body 读取
 */
public final class RequestBodyReader {

    private RequestBodyReader() {
    }

    /**
     * 读取请求体中body
     *
     * @param body 请求body
     * @param type 请求数据内容type
     * @param <T>  返回数据
     * @return 读取结果
     * @throws IOException io
     */
    public static <T> T read(RequestBody body, TypeReference<T> type) throws IOException {
        return read(body, type, false);
    }

    /**
     * 读取请求体中body(已捕获异常)
     *
     * @param body 请求body
     * @param type 请求数据内容type
     * @param <T>  返回数据
     * @return 读取结果
     */
    public static <T> T readForCatchException(RequestBody body, TypeReference<T> type) {
        try {
            return read(body, type, false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 读取请求体中的xml格式的body
     *
     * @param body 请求body
     * @param type 请求数据内容type
     * @param <T>  返回数据
     * @return 读取结果
     * @throws IOException io
     */
    public static <T> T readXml(RequestBody body, TypeReference<T> type) throws IOException {
        return read(body, type, true);
    }

    /**
     * 读取请求体中的xml格式的body（已捕获异常）
     *
     * @param body 请求body
     * @param type 请求数据内容type
     * @param <T>  返回数据
     * @return 读取结果
     */
    public static <T> T readXmlForCatchException(RequestBody body, TypeReference<T> type) {
        try {
            return read(body, type, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取请求body
     *
     * @param body         请求body
     * @param type         请求数据内容type
     * @param isXmlRequest 是否属于xml请求数据
     * @param <T>          返回数据
     * @return 读取结果
     * @throws IOException io
     */
    public static <T> T read(RequestBody body, TypeReference<T> type, boolean isXmlRequest) throws IOException {
        if (body == null) {
            throw new ResponseEmptyException("Request body is null");
        }

        String content = null;
        MediaType contentType = body.contentType();
        Charset charset = null;

        if (contentType != null) {
            charset = contentType.charset(StandardCharsets.UTF_8);
        }

        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }

        Buffer buffer = new Buffer();
        try {
            body.writeTo(buffer);
            content = URLDecoder.decode(buffer.readString(charset), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (TextUtils.isEmpty(content)) {
            throw new ResponseEmptyException("Request body is empty");
        }

        T requestData = null;

        if (isXmlRequest) {
            JSONObject jsonObject = new XmlToJson.Builder(content).build().toJson();
            if (jsonObject != null && jsonObject.length() > 0) {
                requestData = JsonKt.toObject(jsonObject.toString(), type);
            }
        } else {
            if (type.getType() == String.class) {
                requestData = (T) content;
            } else {
                requestData = JsonKt.toObject(content, type);
            }
        }

        if (requestData == null) {
            throw new DataErrorException(
                    "The result of parsing JSON is empty or abnormal，and the JSON data is : "
                            .concat(content)
            );
        }
        return requestData;
    }

}
