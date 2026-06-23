package com.richard.library.net.http.request;

import androidx.annotation.NonNull;

import com.richard.library.net.http.converter.FastJsonConverterFactory;
import com.richard.library.net.http.download.FileResponseBody;
import com.richard.library.net.http.download.ProgressCallback;
import com.richard.library.net.http.https.CertificateIgnoreSSLParams;
import com.richard.library.net.http.https.SSLParams;
import com.richard.library.net.http.verifier.HttpHostnameVerifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * @author: Richard
 * @createDate: 2024/9/3 11:13
 * @version: 1.0
 * @description: 网络请求客户端
 */
public class RequestClient {

    private final Config config = new Config();
    private static volatile RequestClient instance;
    private volatile OkHttpClient client;
    private volatile Retrofit retrofit;

    private RequestClient() {
    }

    /**
     * 获取单例
     */
    public static RequestClient get() {
        if (instance == null) {
            synchronized (RequestClient.class) {
                if (instance == null) {
                    instance = new RequestClient();
                }
            }
        }
        return instance;
    }

    /**
     * 获取请求客户端
     */
    public OkHttpClient client() {
        if (client == null) {
            synchronized (RequestClient.class) {
                if (client == null) {
                    client = this.generatorHttpClient();
                }
            }
        }
        return client;
    }

    /**
     * 获取retrofit
     */
    public Retrofit retrofit() {
        if (retrofit == null) {
            synchronized (RequestClient.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(config.baseURL)
                            .client(this.client())
                            .addConverterFactory(FastJsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    /**
     * 获取对应的Service
     */
    public <T> T create(Class<T> service) {
        return this.retrofit().create(service);
    }

    /**
     * 是否设置了默认的日志记录拦截器
     */
    public boolean isDefaultLogInterceptor() {
        return config.logCallback != null;
    }

    /**
     * 生成httpClient实例
     */
    private OkHttpClient generatorHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        //添加自定义拦截器
        if (!config.interceptorList.isEmpty()) {
            okHttpClientBuilder.interceptors().addAll(config.interceptorList);
        }

        //日志记录拦截器
        if (config.logCallback != null) {
            okHttpClientBuilder.interceptors().add(new LoggerInterceptor(config.logCallback));
        }

        //添加缓存拦截器
//        okHttpClientBuilder.addInterceptor(new CacheInterceptor());
//        // 指定缓存路径,缓存大小 50Mb
//        Cache cache = new Cache(new File(AppContext.get().getCacheDir(), "HttpCache"),
//                1024 * 1024 * 50);

        // Cookie 持久化
//        ClearableCookieJar cookieJar = new PersistentCookieJar(
//                new SetCookieCache()
//                , new SharedPrefsCookiePersistor(AppContext.get())
//        );

        if (config.executorService != null) {
            okHttpClientBuilder.dispatcher(new Dispatcher(config.executorService));
        }

        if (config.ignoreSSL) {
            SSLParams sslParams = CertificateIgnoreSSLParams.getSSLParams();
            okHttpClientBuilder.sslSocketFactory(sslParams.getsSLSocketFactory(), (X509TrustManager) sslParams.getTrustManager())
                    .hostnameVerifier(CertificateIgnoreSSLParams.getHostnameVerifier());
        } else {
            okHttpClientBuilder.hostnameVerifier(new HttpHostnameVerifier(config.baseURL));
        }

        //初始化网络请求框架参数
        okHttpClientBuilder
//                .cache(cache)
//                .cookieJar(cookieJar)
                .followRedirects(config.followRedirects)
                .retryOnConnectionFailure(config.isOpenRetryConnect)//是否开启重连机制
                .connectTimeout(config.connectTimeout, config.timeUnit)//设置超时时间
                .readTimeout(config.readTimeout, config.timeUnit)//设置读取超时时间
                .writeTimeout(config.writeTimeout, config.timeUnit)//设置写的超时时间
                .addNetworkInterceptor(new Interceptor() {
                    @NonNull
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request();
                        ProgressCallback callback = request.tag(ProgressCallback.class);

                        if (callback == null) {
                            return chain.proceed(request);
                        }

                        Response originalResponse = chain.proceed(request);
                        return originalResponse
                                .newBuilder()
                                .body(new FileResponseBody(originalResponse.body(), callback))
                                .build();
                    }
                });

        return okHttpClientBuilder.build();
    }

    /**
     * 配置相关参数
     */
    public Config config() {
        return config;
    }

    /**
     * 配置相关
     */
    public static class Config {

        /**
         * 请求root url
         */
        private String baseURL = "http://www/";

        /**
         * 是否忽略https证书验证
         */
        private boolean ignoreSSL = false;

        /**
         * 是否允许重定向
         */
        private boolean followRedirects = true;

        /**
         * 是否开启重连机制
         */
        private boolean isOpenRetryConnect = true;

        /**
         * 超时时间单位
         */
        private TimeUnit timeUnit = TimeUnit.SECONDS;

        /**
         * 连接超时时间
         */
        private long connectTimeout = 5;

        /**
         * 读取超时时间
         */
        private long readTimeout = 60;

        /**
         * 写入超时时间
         */
        private long writeTimeout = 10;

        /**
         * 日志拦截器回调
         */
        private LogCallback logCallback;

        /**
         * 自定义拦截器
         */
        private final List<Interceptor> interceptorList = new ArrayList<>();

        /**
         * 指定的线程池
         */
        private ExecutorService executorService;


        public Config baseURL(String baseURL) {
            this.baseURL = baseURL;
            return this;
        }

        public Config ignoreSSL(boolean ignoreSSL) {
            this.ignoreSSL = ignoreSSL;
            return this;
        }

        public Config followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public Config addInterceptor(Interceptor interceptor) {
            this.interceptorList.add(interceptor);
            return this;
        }

        public Config addInterceptors(List<Interceptor> interceptor) {
            this.interceptorList.addAll(interceptor);
            return this;
        }

        public Config openRetryConnect(boolean openRetryConnect) {
            this.isOpenRetryConnect = openRetryConnect;
            return this;
        }

        public Config timeUnit(@NonNull TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public Config connectTimeout(long timeout) {
            this.connectTimeout = timeout;
            return this;
        }

        public Config readTimeout(long timeout) {
            this.readTimeout = timeout;
            return this;
        }

        public Config writeTimeout(long timeout) {
            this.writeTimeout = timeout;
            return this;
        }

        public Config logCallback(LogCallback callback) {
            this.logCallback = callback;
            return this;
        }

        public Config setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }
    }

}
