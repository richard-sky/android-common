package com.richard.library.port.connect.port;


import android.util.Log;

import com.richard.library.port.connect.exception.PortException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author: Richard
 * @createDate: 2026/4/21 13:48
 * @version: 1.0
 * @description: 局域网网络通信
 */
public class EthernetPort extends BasicPort {

    private static final String TAG = EthernetPort.class.getSimpleName();
    private Socket socket;
    private InetAddress inetAddress;
    private SocketAddress socketAddress;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int connectTimeout = 1500;
    private final String ip;
    private final int port;

    public EthernetPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public void openPort() throws IOException, PortException {
        this.closePort();
        this.socket = new Socket();
        try {
            this.inetAddress = Inet4Address.getByName(ip);
            this.socketAddress = new InetSocketAddress(this.inetAddress, port);
            this.socket.connect(this.socketAddress, connectTimeout);
            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
        } catch (IOException e) {
            this.closePort();
            throw e;
        }
    }

    @Override
    public boolean checkValid() {
        byte[] b = new byte[4];
        try {
            return this.read(b) != -1;
        } catch (IOException | PortException e) {
            Log.e(TAG, "An exception occurred while checking if the port is valid: ", e);
        }
        return false;
    }

    @Override
    public boolean isOpened() {
        return this.socket != null && this.socket.isConnected() && this.outputStream != null && this.inputStream != null;
    }

    @Override
    public int write(byte[] data) throws IOException, PortException {
        return this.write(data, 0, data.length);
    }

    @Override
    public int write(byte[] data, int offset, int len) throws IOException, PortException {
        this.checkPortOpen();
        try {
            this.outputStream.write(data, offset, len);
            this.outputStream.flush();
            return len;
        } catch (IOException e) {
            Log.e(TAG, "Exception occurred while sending data immediately: ", e);
            throw e;
        }
    }

    @Override
    public int read(byte[] buffer) throws IOException, PortException {
        return this.read(buffer, 0, buffer.length);
    }

    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException, PortException {
        this.checkPortOpen();
        try {
            int readLength = this.inputStream.read(buffer, offset, len);
            Log.d(TAG, "read length" + readLength);
            return readLength;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "connection device is lost");
            throw e;
        }
    }

    @Override
    public boolean closePort() {
        try {
            if (this.outputStream != null) {
                this.outputStream.close();
                this.outputStream = null;
            }

            if (this.inputStream != null) {
                this.inputStream.close();
                this.inputStream = null;
            }

            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Close port error!", e);
            return false;
        }
    }

    /**
     * 检查端口是否已打开
     */
    private void checkPortOpen() throws PortException {
        if (this.isOpened()) {
            return;
        }
        Log.e(TAG, "Port not connected");
        throw new PortException("Port not connected");
    }
}

