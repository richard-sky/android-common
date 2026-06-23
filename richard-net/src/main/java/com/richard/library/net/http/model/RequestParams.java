package com.richard.library.net.http.model;

import androidx.annotation.NonNull;

import com.richard.library.context.util.JsonKt;

import java.util.LinkedHashMap;


/**
 * <pre>
 * Description : 请求参数
 * Author : admin-richard
 * Date : 2019-06-06 09:44
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-06 09:44     admin-richard         new file.
 * </pre>
 */
public class RequestParams extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = -6861880271023522714L;

    @NonNull
    public RequestParams put(String key, Object value) {
        if (value == null) {
            return this;
        }
        super.put(key, value);
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        String json = JsonKt.toJson(this);
        return json == null ? "" : json;
    }
}
