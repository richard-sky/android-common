package com.richard.library.context.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * @author: Richard
 * @createDate: 2024/10/22 17:48
 * @version: 1.0
 * @description: url 工具类
 */
public final class URLUtil {

    private URLUtil() {
    }

    /**
     * 获取指定url中的某个参数
     *
     * @param url  链接
     * @param name 指定获取的参数名
     * @return 返货参数名的value
     */
    public static String getParamByUrl(String url, String name) {
        if (url != null && !url.isEmpty()) {
            int index = url.indexOf("?");
            String temp = (index >= 0 ? url.substring(index + 1) : url);
            String[] keyValue = temp.split("&");
            String destPrefix = name + "=";
            for (String str : keyValue) {
                if (str.indexOf(destPrefix) == 0) {
                    return str.substring(destPrefix.length());
                }
            }
        }
        return null;
    }

    /**
     * 补全URL
     */
    public static String complete(String host, String relativeUrl) {
        if (TextUtils.isEmpty(host)) {
            return relativeUrl;
        }

        if (TextUtils.isEmpty(relativeUrl)) {
            return host;
        }

        if (StringUtilKt.startsWithAny(relativeUrl, new String[]{"http://", "https://", "ftp://", "file://", "ws://", "wss://"})) {
            return relativeUrl;
        }

        if (host.endsWith("/") && relativeUrl.startsWith("/")) {
            return host.concat(relativeUrl.substring(1));
        }

        if (!host.endsWith("/") && !relativeUrl.startsWith("/")) {
            return host.concat("/").concat(relativeUrl);
        }

        return host.concat(relativeUrl);
    }

    /**
     * 补全url后的参数
     */
    public static String completeParams(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder urlBuilder = new StringBuilder();
        Set<Map.Entry<String, Object>> entrySet = params.entrySet();

        try {
            for (Map.Entry<String, Object> item : entrySet) {
                if (item.getValue() == null) {
                    urlBuilder.append(item.getKey())
                            .append("=")
                            .append(URLEncoder.encode(ObjectUtilKt.toString(item.getValue()), "UTF-8"))
                            .append("&");
                    continue;
                }

                if (JsonKt.isEntity(item.getValue().getClass())) {
                    urlBuilder.append(item.getKey())
                            .append("=")
                            .append(URLEncoder.encode(JsonKt.toJson(item.getValue()), "UTF-8"))
                            .append("&");
                } else {
                    urlBuilder.append(item.getKey())
                            .append("=")
                            .append(URLEncoder.encode(ObjectUtilKt.toString(item.getValue()), "UTF-8"))
                            .append("&");
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return url + "?" + urlBuilder.substring(0, urlBuilder.length() - 1);
    }
}
