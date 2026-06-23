package com.richard.library.net.ftp;

import com.richard.library.net.ftp.client.FTPClientImpl;
import com.richard.library.net.ftp.client.FTPSClientImpl;
import com.richard.library.net.ftp.client.TFTPClientImpl;

/**
 * 网络文件传输客户端工厂
 */
public class FileTransferFactory {

    /**
     * 创建FTP客户端
     */
    public static FTPClientImpl createFTPClient(String host, int port, String username, String password) {
        FTPClientImpl client = new FTPClientImpl();
        client.setConnectionParams(host, port, username, password);
        return client;
    }

    /**
     * 创建FTP客户端
     */
    public static FTPClientImpl createFTPClient(String host, String username, String password) {
        FTPClientImpl client = new FTPClientImpl();
        client.setConnectionParams(host, username, password);
        return client;
    }

    /**
     * 创建FTPS客户端
     */
    public static FTPSClientImpl createFTPSClient(String host, int port, String username, String password) {
        FTPSClientImpl client = new FTPSClientImpl();
        client.setConnectionParams(host, port, username, password);
        return client;
    }

    /**
     * 创建FTPS客户端
     */
    public static FTPSClientImpl createFTPSClient(String host, String username, String password) {
        FTPSClientImpl client = new FTPSClientImpl();
        client.setConnectionParams(host, username, password);
        return client;
    }

    /**
     * 创建TFTP客户端
     */
    public static TFTPClientImpl createTFTPClient(String host, int port) {
        TFTPClientImpl client = new TFTPClientImpl();
        client.setConnectionParams(host, port);
        return client;
    }

    /**
     * 创建TFTP客户端
     */
    public static TFTPClientImpl createTFTPClient(String host) {
        TFTPClientImpl client = new TFTPClientImpl();
        client.setConnectionParams(host);
        return client;
    }
}
