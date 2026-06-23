package com.richard.dev.common;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/03/28.
 * 基本API服务
 */
public interface UserService {

    /**
     * GET请求
     *
     * @param url    请求URL
     */
    @GET
    Call<ResponseBody> doGet(@Url String url);

}
