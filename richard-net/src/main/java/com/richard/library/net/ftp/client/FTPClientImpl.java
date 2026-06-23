package com.richard.library.net.ftp.client;

import android.util.Log;

import com.richard.library.context.util.UIThread;
import com.richard.library.net.ftp.FileTransferClient;
import com.richard.library.net.ftp.common.ConnectListener;
import com.richard.library.net.ftp.common.ProgressInputStream;
import com.richard.library.net.ftp.common.ProgressOutputStream;
import com.richard.library.net.ftp.common.ServerFeatures;
import com.richard.library.net.ftp.common.TransferProgressListener;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FTP客户端实现（支持安全取消上传/下载）
 */
public class FTPClientImpl implements FileTransferClient {

    private static final String TAG = "FTPClientImpl";
    private static final int DEFAULT_TIMEOUT = 30000;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int PROGRESS_UPDATE_INTERVAL = 200;

    protected FTPClient ftpClient;

    // 连接参数
    protected String host;                                  //FTP服务器地址
    protected Integer port;                                 //FTP服务器端口
    protected String username;                              //用户名
    protected String password;                              //密码

    // 配置参数
    protected int connectTimeout = DEFAULT_TIMEOUT;         //连接超时
    protected int dataTimeout = DEFAULT_TIMEOUT;            //数据传输超时
    protected int bufferSize = DEFAULT_BUFFER_SIZE;         //缓冲区大小
    protected boolean passiveMode = true;                   //是否使用被动模式
    protected String encoding = "UTF-8";                    //编码格式

    // 监听器
    protected TransferProgressListener transferProgressListener;
    protected ConnectListener connectionListener;

    // 状态
    protected boolean isConnected = false;
    protected volatile boolean isTransferring = false;

    // ====================== 新增：取消标志 ======================
    protected volatile boolean isCanceled = false;

    /**
     * 设置连接参数
     */
    public void setConnectionParams(String host, String username, String password) {
        this.host = host;
        this.port = null;
        this.username = username;
        this.password = password;
    }

    /**
     * 设置连接参数
     */
    public void setConnectionParams(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 设置连接超时
     */
    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    /**
     * 设置数据传输超时
     */
    public void setDataTimeout(int timeout) {
        this.dataTimeout = timeout;
    }

    /**
     * 设置缓冲区大小
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * 设置被动模式
     */
    public void setPassiveMode(boolean passive) {
        this.passiveMode = passive;
    }

    /**
     * 设置编码
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean connect() throws Exception {
        if (isConnected()) {
            Log.w(TAG, "当前FTP已经处于连接状态");
            return true;
        }
        ftpClient = new FTPClient();

        // 配置客户端
        ftpClient.setConnectTimeout(connectTimeout);
        ftpClient.setDataTimeout(Duration.ofMillis(dataTimeout));
        ftpClient.setControlEncoding(encoding);
        ftpClient.setBufferSize(bufferSize);

        // 连接服务器
        if (port != null) {
            ftpClient.connect(host, port);
        } else {
            ftpClient.connect(host);
        }

        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            ftpClient.disconnect();
            throw new IOException("FTP服务器拒绝连接: " + replyCode);
        }

        // 登录
        if (!ftpClient.login(username, password)) {
            ftpClient.logout();
            ftpClient.disconnect();
            throw new IOException("FTP登录失败");
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

    @Override
    public void disconnect() {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            Log.e(TAG, "断开连接错误", e);
        } finally {
            isConnected = false;
            isTransferring = false;
            isCanceled = false;

            if (connectionListener != null) {
                UIThread.runOnUiThread(connectionListener::onDisconnected);
            }
        }
    }

    @Override
    public boolean isConnected() {
        if (!isConnected) return false;
        try {
            return ftpClient != null && ftpClient.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    // ====================== 上传文件（支持取消） ======================
    @Override
    public boolean uploadFile(String localPath, String remotePath) throws IOException {
        // 每次传输前重置取消状态
        isCanceled = false;

        File localFile = new File(localPath);
        if (!localFile.exists()) {
            throw new IOException("本地文件不存在: " + localPath);
        }

        try (FileInputStream fis = new FileInputStream(localFile);
             ProgressInputStream in = new ProgressInputStream(fis, localFile.getName(), localFile.length())) {

            //初始化进度监听
            in.setUpdateProgressInterval(PROGRESS_UPDATE_INTERVAL);
            in.setListener((filename, transferred, totalSize) -> {
                // 传输中检查取消
                if (isCanceled) return;
                if (transferProgressListener != null && isTransferring) {
                    UIThread.runOnUiThread(() ->
                            transferProgressListener.onProgress(filename, transferred, totalSize));
                }
            });

            //设置取消检查
            in.setCancelChecker(() -> isCanceled);

            createRemoteDirectories(remotePath);
            isTransferring = true;

            boolean success = false;
            try {
                if (!isCanceled) {
                    success = ftpClient.storeFile(remotePath, in);
                }
            } finally {
                isTransferring = false;
                // 取消后必须清理FTP命令，防止连接卡死
                if (isCanceled) {
                    try {
                        ftpClient.completePendingCommand();
                    } catch (Exception ignored) {
                    }
                }
            }

            // 取消回调
            if (isCanceled) {
                Log.i(TAG, "上传已取消：" + localPath);
                if (transferProgressListener != null) {
                    UIThread.runOnUiThread(() -> transferProgressListener.onCancel(localPath));
                }
                return false;
            }

            // 成功回调
            if (success && transferProgressListener != null) {
                UIThread.runOnUiThread(() -> {
                    transferProgressListener.onProgress(localFile.getName(), localFile.length(), localFile.length());
                    transferProgressListener.onComplete(localFile.getName());
                });
            }

            return success;

        } catch (Throwable e) {
            isTransferring = false;
            if (isCanceled) {
                Log.i(TAG, "上传已取消");
                if (transferProgressListener != null) {
                    UIThread.runOnUiThread(() -> transferProgressListener.onCancel(localPath));
                }
                return false;
            }

            Log.e(TAG, "上传失败", e);
            if (transferProgressListener != null) {
                UIThread.runOnUiThread(() ->
                        transferProgressListener.onError(localPath, e.getMessage()));
            }
            throw e;
        }
    }

    // ====================== 下载文件（支持取消） ======================
    @Override
    public boolean downloadFile(String remotePath, String localPath) throws IOException {
        // 每次传输前重置取消状态
        isCanceled = false;

        File localFile = new File(localPath);

        // 创建本地目录
        File parentDir = localFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 获取文件大小
        FTPFile[] files = ftpClient.listFiles(remotePath);
        if (files.length == 0) {
            throw new IOException("远程文件不存在: " + remotePath);
        }

        long fileSize = files[0].getSize();

        try (FileOutputStream fos = new FileOutputStream(localFile);
             ProgressOutputStream out = new ProgressOutputStream(fos, localFile.getName(), fileSize)) {

            //初始化进度监听
            out.setUpdateProgressInterval(PROGRESS_UPDATE_INTERVAL);
            out.setListener((filename, transferred, totalSize) -> {
                if (isCanceled) return;
                if (transferProgressListener != null && isTransferring) {
                    UIThread.runOnUiThread(() ->
                            transferProgressListener.onProgress(filename, transferred, totalSize));
                }
            });

            //设置取消检查
            out.setCancelChecker(() -> isCanceled);

            isTransferring = true;
            boolean success = false;

            try {
                if (!isCanceled) {
                    success = ftpClient.retrieveFile(remotePath, out);
                }
            } finally {
                isTransferring = false;
                // 取消后必须清理FTP命令
                if (isCanceled) {
                    try {
                        ftpClient.completePendingCommand();
                    } catch (Exception ignored) {
                    }
                }
            }

            // 取消回调
            if (isCanceled) {
                Log.i(TAG, "下载已取消：" + remotePath);
                if (transferProgressListener != null) {
                    UIThread.runOnUiThread(() -> transferProgressListener.onCancel(remotePath));
                }
                return false;
            }

            // 成功回调
            if (success && transferProgressListener != null) {
                UIThread.runOnUiThread(() -> {
                    transferProgressListener.onProgress(localFile.getName(), fileSize, fileSize);
                    transferProgressListener.onComplete(localFile.getName());
                });
            }

            return success;

        } catch (Throwable e) {
            isTransferring = false;
            if (isCanceled) {
                Log.i(TAG, "下载已取消");
                if (transferProgressListener != null) {
                    UIThread.runOnUiThread(() -> transferProgressListener.onCancel(remotePath));
                }
                return false;
            }

            Log.e(TAG, "下载失败", e);
            if (transferProgressListener != null) {
                UIThread.runOnUiThread(() ->
                        transferProgressListener.onError(remotePath, e.getMessage()));
            }
            throw e;
        }
    }

    // ====================== 【对外方法】取消上传/下载 ======================

    /**
     * 取消当前正在进行的文件传输
     */
    @Override
    public void cancelTransfer() {
        if (!isTransferring) return;

        isCanceled = true;
        Log.i(TAG, "用户主动取消传输");

        // 发送FTP ABOR 命令，立即中断数据传输
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.abort();
            }
        } catch (Exception e) {
            Log.w(TAG, "取消传输时异常（可忽略）", e);
        }
    }

    @Override
    public List<FTPFile> listFiles(String remotePath) throws IOException {
        FTPFile[] files = ftpClient.listFiles(remotePath);
        return new ArrayList<>(Arrays.asList(files));
    }

    @Override
    public boolean createDirectory(String remotePath) throws IOException {
        return ftpClient.makeDirectory(remotePath);
    }

    @Override
    public boolean deleteFile(String remotePath) throws IOException {
        return ftpClient.deleteFile(remotePath);
    }

    @Override
    public boolean renameFile(String from, String to) throws IOException {
        return ftpClient.rename(from, to);
    }

    @Override
    public boolean fileExists(String remotePath) throws IOException {
        FTPFile[] files = ftpClient.listFiles(remotePath);
        return files.length > 0 && files[0].isFile();
    }

    @Override
    public String getWorkingDirectory() throws IOException {
        return ftpClient.printWorkingDirectory();
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
        features.setSystemType(ftpClient.getSystemType());

        int reply = ftpClient.feat();
        if (FTPReply.isPositiveCompletion(reply)) {
            String replyString = ftpClient.getReplyString();
            features.setSupportsMLSD(replyString.contains("MLSD"));
            features.setSupportsUTF8(replyString.contains("UTF8"));
            features.setSupportsSize(replyString.contains("SIZE"));
            features.setSupportsMDTM(replyString.contains("MDTM"));
        }

        return features;
    }

    @Override
    public String getReplyString() {
        return ftpClient.getReplyString();
    }

    /**
     * 创建远程目录（包括父目录）
     */
    protected void createRemoteDirectories(String remotePath) throws IOException {
        // 保存当前目录
        String originalDir = ftpClient.printWorkingDirectory();

        // 分割路径
        String[] parts = remotePath.split("/");
        StringBuilder currentPath = new StringBuilder();

        for (int i = 0; i < parts.length - 1; i++) { // 不包含文件名
            String part = parts[i];
            if (part.isEmpty()) {
                if (i == 0) {
                    // 绝对路径
                    currentPath.append("/");
                }
                continue;
            }

            if (currentPath.length() > 0 && !currentPath.toString().endsWith("/")) {
                currentPath.append("/");
            }
            currentPath.append(part);

            String dirPath = currentPath.toString();

            // 尝试进入目录
            if (!ftpClient.changeWorkingDirectory(dirPath)) {
                // 如果进入失败，尝试创建目录
                if (!ftpClient.makeDirectory(dirPath)) {
                    // 恢复原始目录
                    if (originalDir != null) {
                        ftpClient.changeWorkingDirectory(originalDir);
                    }
                    throw new IOException("创建目录失败: " + dirPath);
                }
                // 进入新创建的目录
                ftpClient.changeWorkingDirectory(dirPath);
            }
        }

        // 恢复原始目录
        if (originalDir != null) {
            ftpClient.changeWorkingDirectory(originalDir);
        }
    }

    /**
     * 更改工作目录
     */
    public boolean changeWorkingDirectory(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }

    /**
     * 测试连接
     */
    public boolean testConnection() throws IOException {
        if (!isConnected()) {
            return false;
        }
        return ftpClient.sendNoOp();
    }

    /**
     * 获取底层FTP客户端
     */
    public FTPClient getRawFTPClient() {
        return ftpClient;
    }
}