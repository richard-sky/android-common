package com.richard.library.net.ftp.client;

import android.util.Log;

import com.richard.library.context.util.UIThread;
import com.richard.library.net.http.https.CertificateIgnoreSSLParams;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;

import javax.net.ssl.SSLContext;

/**
 * FTPS客户端实现
 */
public class FTPSClientImpl extends FTPClientImpl {

    private static final String TAG = "FTPSClientImpl";

    // SSL配置
    private SSLContext sslContext;                  //SSL上下文
    private boolean trustAllCertificates = false;   //是否信任所有证书
    private String[] enabledCipherSuites;           //启用的加密套件
    private boolean implicitMode = false;           //是否使用隐式FTPS

    /**
     * 设置SSL上下文
     */
    public void setSSLContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    /**
     * 设置是否信任所有证书
     */
    public void setTrustAllCertificates(boolean trustAll) {
        this.trustAllCertificates = trustAll;
    }

    /**
     * 设置启用的加密套件
     */
    public void setEnabledCipherSuites(String[] cipherSuites) {
        this.enabledCipherSuites = cipherSuites;
    }

    /**
     * 设置隐式模式
     */
    public void setImplicitMode(boolean implicit) {
        this.implicitMode = implicit;
    }

    /**
     * 创建信任所有证书的SSL上下文
     */
    private SSLContext createTrustAllSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, CertificateIgnoreSSLParams.getTrustManager(), new SecureRandom());
        return context;
    }

    @Override
    public boolean connect() throws Exception {
        if (isConnected()) {
            Log.w(TAG, "当前FTP已经处于连接状态");
            return true;
        }

        // 如果没有提供SSL上下文，则创建一个
        if (sslContext == null && trustAllCertificates) {
            try {
                sslContext = createTrustAllSSLContext();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                Log.e(TAG, "创建SSL上下文失败", e);
                throw e;
            }
        }

        // 创建FTPS客户端
        if (sslContext != null) {
            // 使用自定义SSL上下文
            ftpClient = new FTPSClient(implicitMode, sslContext);
        } else {
            // 使用默认SSL上下文
            ftpClient = new FTPSClient(implicitMode);
        }

        // 配置客户端
        ftpClient.setConnectTimeout(connectTimeout);
        ftpClient.setDataTimeout(Duration.ofMillis(dataTimeout));
        ftpClient.setControlEncoding(encoding);
        ftpClient.setBufferSize(bufferSize);

        // 设置启用的加密套件
        if (enabledCipherSuites != null && enabledCipherSuites.length > 0) {
            ((FTPSClient) ftpClient).setEnabledCipherSuites(enabledCipherSuites);
        }

        // 连接服务器
        if (port != null) {
            ftpClient.connect(host, port);
        } else {
            ftpClient.connect(host);
        }

        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            ftpClient.disconnect();
            throw new IOException("FTPS服务器拒绝连接: " + replyCode);
        }

        // 登录
        if (!ftpClient.login(username, password)) {
            ftpClient.logout();
            ftpClient.disconnect();
            throw new IOException("FTPS登录失败");
        }

        // 如果是显式FTPS，需要执行保护模式命令
        if (!implicitMode) {
            ((FTPSClient) ftpClient).execPBSZ(0);  // 设置保护缓冲区大小为0
            ((FTPSClient) ftpClient).execPROT("P"); // 设置数据通道为私有（加密）
        }

        // 设置传输模式
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        // 设置被动模式
        if (passiveMode) {
            ftpClient.enterLocalPassiveMode();
        } else {
            ftpClient.enterLocalActiveMode();
        }

        isConnected = true;

        if (connectionListener != null) {
            UIThread.runOnUiThread(() -> connectionListener.onConnected());
        }

        return true;
    }

    /**
     * 获取底层FTPS客户端
     */
    public FTPSClient getRawFTPSClient() {
        return (FTPSClient) ftpClient;
    }
}
