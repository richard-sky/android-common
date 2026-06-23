package com.richard.library.net.websocket;

import androidx.annotation.NonNull;

/**
 * @author: Richard
 * @createDate: 2026/6/3 11:16
 * @version: 1.0
 * @description: websocket api 调用返回数据过滤
 */
@FunctionalInterface
public interface InvokeFilter<T> {

    boolean filter(@NonNull T data);

}
