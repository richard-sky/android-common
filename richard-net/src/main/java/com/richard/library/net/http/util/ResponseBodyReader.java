package com.richard.library.net.http.util;

import android.text.TextUtils;

import com.alibaba.fastjson.TypeReference;
import com.richard.library.context.util.JsonKt;
import com.richard.library.net.http.exception.DataErrorException;
import com.richard.library.net.http.exception.ResponseEmptyException;

import org.json.JSONObject;

import java.io.IOException;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import okhttp3.Response;

/**
 * <pre>
 * Description : 通用读取 Response<ResponseBody> 响应体数据
 * Author : admin-richard
 * Date : 2019-06-13 21:02
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-13 21:02     admin-richard         new file.
 * </pre>
 */
public final class ResponseBodyReader {

    private ResponseBodyReader() {
    }

    /**
     * 读取响应body
     *
     * @param response 响应体
     * @param type     响应数据内容type
     * @param <T>      返回数据
     * @return 读取结果
     * @throws IOException io
     */
    public static <T> T read(Response response, TypeReference<T> type) throws IOException {
        return read(response, type, false);
    }

    /**
     * 读取响应body(已捕获异常)
     *
     * @param response 响应体
     * @param type     响应数据内容type
     * @param <T>      返回数据
     * @return 读取结果
     */
    public static <T> T readForCatchException(Response response, TypeReference<T> type) {
        try {
            return read(response, type, false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 读取响应xml格式的body
     *
     * @param response 响应体
     * @param type     响应数据内容type
     * @param <T>      返回数据
     * @return 读取结果
     * @throws IOException io
     */
    public static <T> T readXml(Response response, TypeReference<T> type) throws IOException {
        return read(response, type, true);
    }

    /**
     * 读取响应xml格式的body（已捕获异常）
     *
     * @param response 响应体
     * @param type     响应数据内容type
     * @param <T>      返回数据
     * @return 读取结果
     */
    public static <T> T readXmlForCatchException(Response response, TypeReference<T> type) {
        try {
            return read(response, type, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取响应body
     *
     * @param response      响应体
     * @param type          响应数据内容type
     * @param isXmlResponse 是否属于xml响应数据
     * @param <T>           返回数据
     * @return 读取结果
     * @throws IOException io
     */
    public static <T> T read(Response response, TypeReference<T> type, boolean isXmlResponse) throws IOException {
        if (response == null) {
            return null;
        }

        String body = "";
        if (response.body() != null) {
            body = response.body().string();
        }

        if (TextUtils.isEmpty(body)) {
            throw new ResponseEmptyException("Response body is empty");
        }

        T responseData = null;

        if (isXmlResponse) {
            JSONObject jsonObject = new XmlToJson.Builder(body).build().toJson();
            if (jsonObject != null && jsonObject.length() > 0) {
                responseData = JsonKt.toObject(jsonObject.toString(), type);
            }
        } else {
            if (type.getType() == String.class) {
                responseData = (T) body;
            } else {
                responseData = JsonKt.toObject(body, type);
            }
        }

        if (responseData == null) {
            if (response.code() != 200) {
                throw new DataErrorException(String.format("The request was unsuccessful! statusCode " +
                        "= %s , errorBody = %s", response.code(), body));
            }
            throw new DataErrorException(
                    "The result of parsing JSON is empty or abnormal，and the JSON data is : "
                            .concat(body)
            );
        }
        return responseData;
    }
}
