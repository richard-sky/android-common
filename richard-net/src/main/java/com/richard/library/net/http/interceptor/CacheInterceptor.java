package com.richard.library.net.http.interceptor;

import androidx.annotation.NonNull;

import com.richard.library.context.util.NetUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <pre>
 * Description : 缓存拦截器处理
 * Author : admin-richard
 * Date : 2018/12/5 11:46
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/12/5 11:46     admin-richard         new file.
 * </pre>
 * <p>
 * 缓存机制
 * 在响应请求之后在 data/data/<包名>/cache 下建立一个response 文件夹，保持缓存数据。
 * 这样我们就可以在请求的时候，如果判断到没有网络，自动读取缓存的数据。
 * 同样这也可以实现，在我们没有网络的情况下，重新打开App可以浏览的之前显示过的内容。
 * 也就是：判断网络，有网络，则从网络获取，并保存到缓存中，无网络，则从缓存中获取。
 * https://werb.github.io/2016/07/29/%E4%BD%BF%E7%94%A8Retrofit2+OkHttp3%E5%AE%9E%E7%8E%B0%E7%BC%93%E5%AD%98%E5%A4%84%E7%90%86/
 */
public class CacheInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetUtil.isConnected()) {
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        }

        Response originalResponse = chain.proceed(request);
        if (NetUtil.isConnected()) {
            // 有网络时 设置缓存为默认值
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .build();
        } else {
            // 无网络时 设置超时为2天
            int maxStale = 60 * 60 * 24 * 2;
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("Pragma")
                    .build();
        }
    }
}
