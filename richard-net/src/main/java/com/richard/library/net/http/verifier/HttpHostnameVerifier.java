package com.richard.library.net.http.verifier;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by Administrator on 2017/6/21.
 * 验证域名
 */

public class HttpHostnameVerifier implements HostnameVerifier {

    private final String baseURL;

    public HttpHostnameVerifier(String baseURL){
        this.baseURL =  baseURL;
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if (baseURL.equals(hostname)) {
            return true;
        } else {
            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
            return hv.verify(hostname, session);
        }
    }
}
