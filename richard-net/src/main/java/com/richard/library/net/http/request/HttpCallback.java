package com.richard.library.net.http.request;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.richard.library.context.util.JsonKt;
import com.richard.library.net.http.dict.ResponseCode;
import com.richard.library.net.http.exception.HttpException;
import com.richard.library.net.http.model.ResponseData;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import okhttp3.Callback;

/**
 * <pre>
 * Description : Retrofit网络请求回调
 * Author : admin-richard
 * Date : 2017/3/23 15:43
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/3/23 15:43     admin-richard         new file.
 * </pre>
 */
public abstract class HttpCallback<T> implements Callback {

    /**
     * 响应数据内容是否属于xml格式数据
     */
    private boolean isXmlResponse;

    public HttpCallback() {

    }

    public HttpCallback(boolean isXmlResponse) {
        this.isXmlResponse = isXmlResponse;
    }

    @Override
    public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
        if (response.isRedirect()) {
            String redirectURL = response.header("location");
            this.onRedirectURL(
                    TextUtils.isEmpty(redirectURL)
                            ? response.header("Location", "")
                            : redirectURL
            );
        }

        if (response.code() != 200) {
            this.onFailure(
                    null
                    , HttpError.convertErrorMessage(response.code())
                    , ResponseCode.FAIL.getCode()
            );
            return;
        }

        if (response.body() == null) {
            this.onFailure(
                    null
                    , ResponseCode.BODY_NULL.getMessage()
                    , ResponseCode.BODY_NULL.getCode()
            );
            return;
        }

        ResponseData<T> responseData;
        String body = null;
        try {
            body = response.body().string();

            if (TextUtils.isEmpty(body)) {
                throw new HttpException("Response body is empty");
            }

            Type type = JsonKt.getType(this.getClass());
            if (String.class == type || Object.class == type) {
                this.onSuccess((T) body);
                return;
            }

            Object converterResult = null;

            if (isXmlResponse) {
                JSONObject jsonObject = new XmlToJson.Builder(body).build().toJson();
                if (jsonObject != null && jsonObject.length() > 0) {
                    converterResult = JsonKt.toObject(jsonObject.toString(), type);
                }
            } else {
                converterResult = JsonKt.toObject(body, type);
            }

            if (converterResult == null) {
                throw new HttpException(
                        "The result of parsing JSON is empty or abnormal，and the JSON data is : "
                                .concat(body)
                );
            }

            if (!(converterResult instanceof ResponseData)) {
                this.onSuccess((T) converterResult);
                return;
            }

            responseData = (ResponseData<T>) converterResult;

            if (responseData.isSuccess()) {
                this.onSuccess((T) responseData);
            } else {
                this.onFailure(body, responseData.getMessage(), responseData.getCode());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.onFailure(
                    body
                    , ResponseCode.DATA_ERROR.getMessage()
                    , ResponseCode.DATA_ERROR.getCode()
            );
        } finally {
            response.close();
        }
    }

    @Override
    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
        if (!call.isCanceled()) {
            if (e instanceof SocketTimeoutException) {
                this.onFailure(
                        null
                        , ResponseCode.REQUEST_TIME_OUT.getMessage()
                        , ResponseCode.REQUEST_TIME_OUT.getCode()
                );
            } else {
                this.onFailure(
                        null
                        , ResponseCode.NET_ERROR.getMessage()
                        , ResponseCode.NET_ERROR.getCode()
                );
            }
        }
    }

    /**
     * 请求数据成功时的回调
     *
     * @param response 响应数据
     */
    public abstract void onSuccess(T response);

    /**
     * 当存在重定向时回调
     *
     * @param redirectURL 重定向地址
     */
    public void onRedirectURL(String redirectURL) {
    }

    /**
     * 获取数据失败时回调
     *
     * @param message 消息描述
     * @param code    可能为null
     */
    public abstract void onFailure(String responseBody, String message, String code);
}
