package com.richard.library.net.ftp;

import com.richard.library.net.ftp.common.ConnectListener;
import com.richard.library.net.ftp.common.ServerFeatures;
import com.richard.library.net.ftp.common.TransferProgressListener;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.List;

/**
 * 网络文件传输客户端接口
 */
public interface FileTransferClient {

    /**
     * 连接服务器
     */
    boolean connect() throws Exception;

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 检查是否连接
     */
    boolean isConnected();

    /**
     * 上传文件
     */
    boolean uploadFile(String localPath, String remotePath) throws IOException;

    /**
     * 下载文件
     */
    boolean downloadFile(String remotePath, String localPath) throws IOException;

    /**
     * 取消上传或下载文件
     */
    void cancelTransfer() throws IOException;

    /**
     * 列出目录文件
     */
    List<FTPFile> listFiles(String remotePath) throws IOException;

    /**
     * 创建目录
     */
    boolean createDirectory(String remotePath) throws IOException;

    /**
     * 删除文件
     */
    boolean deleteFile(String remotePath) throws IOException;

    /**
     * 重命名文件
     */
    boolean renameFile(String from, String to) throws IOException;

    /**
     * 检查文件是否存在
     */
    boolean fileExists(String remotePath) throws IOException;

    /**
     * 获取当前工作目录
     */
    String getWorkingDirectory() throws IOException;

    /**
     * 设置连接监听器
     */
    void setConnectionListener(ConnectListener listener);

    /**
     * 设置传输进度监听器
     */
    void setTransferProgressListener(TransferProgressListener listener);

    /**
     * 获取服务器特性
     */
    ServerFeatures getServerFeatures() throws IOException;

    /**
     * 获取最后一条命令回复
     */
    String getReplyString();
}
