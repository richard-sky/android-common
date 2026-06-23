package com.richard.library.net.http.https;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * <pre>
 * Package com.retrofit.https
 * Description : SSL参数
 * author Administrator
 * date 2018/3/5 11:27
 * version V1.0
 * </pre>
 */
public class SSLParams {

    private SSLSocketFactory sSLSocketFactory;
    private TrustManager trustManager;

    public SSLParams(SSLSocketFactory sSLSocketFactory, TrustManager trustManager) {
        this.sSLSocketFactory = sSLSocketFactory;
        this.trustManager = trustManager;
    }

    public SSLParams() {
    }

    public SSLSocketFactory getsSLSocketFactory() {
        return sSLSocketFactory;
    }

    public void setsSLSocketFactory(SSLSocketFactory sSLSocketFactory) {
        this.sSLSocketFactory = sSLSocketFactory;
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }

    public void setTrustManager(TrustManager trustManager) {
        this.trustManager = trustManager;
    }
}
