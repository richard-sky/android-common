package com.richard.library.port.connect.port;


import android.util.Log;

import com.richard.library.port.connect.exception.PortException;
import com.richard.library.port.connect.serialport.SerialPortControl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: Richard
 * @createDate: 2026/4/21 13:53
 * @version: 1.0
 * @description: 串口通信
 */
public class SerialPort extends BasicPort {

    private static final String TAG = SerialPort.class.getSimpleName();
    private SerialPortControl serialPortControl;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final String serialPath;
    private final int baudRate;
    private final int flags;

    public SerialPort(String serialPath, int baudRate) {
        this.serialPath = serialPath;
        this.baudRate = baudRate;
        this.flags = 0;
    }

    public SerialPort(String serialPath, int baudRate, int flags) {
        this.serialPath = serialPath;
        this.baudRate = baudRate;
        this.flags = flags;
    }

    @Override
    public void openPort() throws IOException, PortException {
        this.closePort();

        File file = new File(serialPath);
        if (!file.exists()) {
            throw new PortException("The device path does not exist");
        }

        try {
            this.serialPortControl = new SerialPortControl(file, baudRate, flags);
            this.inputStream = this.serialPortControl.getInputStream();
            this.outputStream = this.serialPortControl.getOutputStream();
        } catch (Throwable e) {
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
        return this.serialPortControl != null && this.outputStream != null && this.inputStream != null;
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
            Log.e("SerialPort", "write data error!", e);
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
            int readLength = this.inputStream.available() > 0 ? this.inputStream.read(buffer, offset, len) : 0;
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
            if (this.inputStream != null) {
                this.inputStream.close();
                this.inputStream = null;
            }

            if (this.outputStream != null) {
                this.outputStream.close();
                this.outputStream = null;
            }

            if (this.serialPortControl != null) {
                this.serialPortControl.close();
                this.serialPortControl = null;
            }

            return true;
        } catch (IOException e) {
            Log.e("SerialPort", "Close the steam or serial port error!", e);
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

