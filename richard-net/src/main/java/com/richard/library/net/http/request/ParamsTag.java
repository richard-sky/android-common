package com.richard.library.net.http.request;

import java.util.Map;

/**
 * @author: Richard
 * @createDate: 2024/9/9 14:25
 * @version: 1.0
 * @description: 请求参数tag对象
 */
public class ParamsTag {

    private final Object params;

    public ParamsTag(Object params) {
        this.params = params;
    }

    public Object getParams() {
        return params;
    }

    public Map<String,Object> map(){
        if(params instanceof Map){
            return (Map<String, Object>) params;
        }
        return null;
    }
}
