package com.richard.library.net.ftp.client;

import android.util.Log;

import com.richard.library.context.util.UIThread;
import com.richard.library.net.ftp.FileTransferClient;
import com.richard.library.net.ftp.common.ConnectListener;
import com.richard.library.net.ftp.common.ProgressInputStream;
import com.richard.library.net.ftp.common.ServerFeatures;
import com.richard.library.net.ftp.common.TransferProgressListener;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.List;

/**
 * TFTP客户端实现（支持取消上传/下载）
 * 注意：TFTP协议非常简单，不支持目录操作、文件列表, 文件下载进度监听等
 */
public class TFTPClientImpl implements FileTransferClient {

    private static final String TAG = "TFTPClientImpl";
    private static final int DEFAULT_TIMEOUT = 10000;
    private static final int PROGRESS_UPDATE_INTERVAL = 200;

    private TFTPClient tftpClient;

    // 连接参数
    private String host;                            //FTP服务器地址
    private Integer port;                           //FTP服务器端口

    // 配置参数
    private int timeout = DEFAULT_TIMEOUT;          //超时时间
    private int transferMode = TFTP.BINARY_MODE;    //传输模式

    // 监听器
    private ConnectListener connectionListener;
    private TransferProgressListener transferProgressListener;

    // 状态
    private boolean isConnected = false;
    private volatile boolean isTransferring = false;
    private boolean isOpened = false;

    // ====================== 新增：取消标志 ======================
    private volatile boolean isCanceled = false;

    /**
     * 设置连接参数
     */
    public void setConnectionParams(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 设置连接参数
     */
    public void setConnectionParams(String host) {
        this.host = host;
        this.port = null;
    }

    /**
     * 设置超时时间
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 设置传输模式
     *
     * @see TFTP
     */
    public void setTransferMode(int mode) {
        this.transferMode = mode;
    }

    @Override
    public boolean connect() throws Exception {
        if (isConnected()) {
            Log.w(TAG, "当前TFTP已经处于连接状态");
            return true;
        }

        tftpClient = new TFTPClient();
        tftpClient.setDefaultTimeout(Duration.ofMillis(timeout));
        tftpClient.open();
        isOpened = true;

        isConnected = true;
        isCanceled = false;

        if (connectionListener != null) {
            UIThread.runOnUiThread(() -> connectionListener.onConnected());
        }

        return true;
    }

    @Override
    public void disconnect() {
        try {
            if (tftpClient != null && isOpened) {
                tftpClient.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "关闭TFTP客户端错误", e);
        } finally {
            isConnected = false;
            isOpened = false;
            isTransferring = false;
            isCanceled = false;

            if (connectionListener != null) {
                UIThread.runOnUiThread(() -> connectionListener.onDisconnected());
            }
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected && isOpened;
    }

    // ====================== 上传文件（支持取消） ======================
    @Override
    public boolean uploadFile(String localPath, String remotePath) throws IOException {
        // 重置取消状态
        isCanceled = false;

        File localFile = new File(localPath);
        if (!localFile.exists()) {
            throw new IOException("本地文件不存在: " + localPath);
        }

        InetAddress hostAddress = InetAddress.getByName(host);

        try (FileInputStream fileInputStream = new FileInputStream(localFile);
             ProgressInputStream inputStream = new ProgressInputStream(
                     fileInputStream, localFile.getName(), localFile.length())) {

            //初始化进度监听
            inputStream.setUpdateProgressInterval(PROGRESS_UPDATE_INTERVAL);
            inputStream.setListener((filename, transferred, totalSize) -> {
                // 检查取消
                if (isCanceled) {
                    return;
                }
                if (transferProgressListener != null && isTransferring) {
                    UIThread.runOnUiThread(() -> transferProgressListener.onProgress(filename, transferred, totalSize));
                }
            });

            //设置取消检查
            inputStream.setCancelChecker(() -> isCanceled);

            isTransferring = true;
            if (port != null) {
                tftpClient.sendFile(remotePath, transferMode, inputStream, hostAddress, port);
            } else {
                tftpClient.sendFile(remotePath, transferMode, inputStream, hostAddress);
            }
            isTransferring = false;

            // 取消判断
            if (isCanceled) {
                Log.i(TAG, "上传已取消：" + localPath);
                callbackCancel(localPath);
                return false;
            }

            // 成功
            if (transferProgressListener != null) {
                UIThread.runOnUiThread(() -> {
                    long fileSize = localFile.length();
                    transferProgressListener.onProgress(localFile.getName(), fileSize, fileSize);
                    transferProgressListener.onComplete(localFile.getName());
                });
            }

            return true;
        } catch (Throwable e) {
            isTransferring = false;

            if (isCanceled) {
                Log.i(TAG, "上传已取消");
                callbackCancel(localPath);
                return false;
            }

            Log.e(TAG, "上传失败", e);
            callbackError(localPath, e.getMessage());
            throw e;
        }
    }

    // ====================== 下载文件（支持取消） ======================
    @Override
    public boolean downloadFile(String remotePath, String localPath) throws IOException {
        // 重置取消状态
        isCanceled = false;

        File localFile = new File(localPath);
        File parentDir = localFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        InetAddress hostAddress = InetAddress.getByName(host);

        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            isTransferring = true;

            if (port != null) {
                tftpClient.receiveFile(remotePath, transferMode, fos, hostAddress, port);
            } else {
                tftpClient.receiveFile(remotePath, transferMode, fos, hostAddress);
            }

            isTransferring = false;

            if (isCanceled) {
                Log.i(TAG, "下载已取消：" + remotePath);
                callbackCancel(remotePath);
                return false;
            }

            // 成功
            if (transferProgressListener != null) {
                UIThread.runOnUiThread(() -> {
                    long fileSize = localFile.length();
                    transferProgressListener.onProgress(localFile.getName(), fileSize, fileSize);
                    transferProgressListener.onComplete(localFile.getName());
                });
            }

            return true;
        } catch (IOException e) {
            isTransferring = false;

            if (isCanceled) {
                Log.i(TAG, "下载已取消");
                callbackCancel(remotePath);
                return false;
            }

            Log.e(TAG, "下载失败", e);
            callbackError(remotePath, e.getMessage());
            throw e;
        }
    }

    // ====================== 【对外方法】取消传输 ======================

    /**
     * 取消当前上传/下载
     */
    @Override
    public void cancelTransfer() {
        if (!isTransferring) return;

        isCanceled = true;
        Log.i(TAG, "用户取消TFTP传输");

        // TFTP 取消唯一方法：关闭套接字，强制中断IO
        try {
            if (tftpClient != null && isOpened) {
                tftpClient.close();
            }
        } catch (Exception e) {
            Log.w(TAG, "取消传输时异常（可忽略）", e);
        }
    }

    // ====================== 回调工具 ======================
    private void callbackCancel(String path) {
        if (transferProgressListener != null) {
            UIThread.runOnUiThread(() -> transferProgressListener.onCancel(path));
        }
    }

    private void callbackError(String path, String msg) {
        if (transferProgressListener != null) {
            UIThread.runOnUiThread(() -> transferProgressListener.onError(path, msg));
        }
    }

    @Override
    public List<FTPFile> listFiles(String remotePath) throws IOException {
        throw new UnsupportedOperationException("TFTP不支持列出文件");
    }

    @Override
    public boolean createDirectory(String remotePath) throws IOException {
        throw new UnsupportedOperationException("TFTP不支持创建目录");
    }

    @Override
    public boolean deleteFile(String remotePath) throws IOException {
        throw new UnsupportedOperationException("TFTP不支持删除文件");
    }

    @Override
    public boolean renameFile(String from, String to) throws IOException {
        throw new UnsupportedOperationException("TFTP不支持重命名文件");
    }

    @Override
    public boolean fileExists(String remotePath) throws IOException {
        throw new UnsupportedOperationException("TFTP不支持检查文件是否存在");
    }

    @Override
    public String getWorkingDirectory() throws IOException {
        throw new UnsupportedOperationException("TFTP没有工作目录概念");
    }

    @Override
    public void setConnectionListener(ConnectListener listener) {
        this.connectionListener = listener;
    }

    @Override
    public void setTransferProgressListener(TransferProgressListener listener) {
        this.transferProgressListener = listener;
    }

    @Override
    public ServerFeatures getServerFeatures() throws IOException {
        ServerFeatures features = new ServerFeatures();
        features.setSystemType("TFTP Server");
        return features;
    }

    @Override
    public String getReplyString() {
        return "TFTP操作完成";
    }

    /**
     * 获取底层TFTP客户端
     */
    public TFTPClient getRawTFTPClient() {
        return tftpClient;
    }

    /**
     * 获取TFTP客户端是否已打开
     */
    public boolean isOpened() {
        return isOpened;
    }
}