package com.richard.library.port.connect.port;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.richard.library.port.connect.exception.PortException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author: Richard
 * @createDate: 2026/4/21 13:47
 * @version: 1.0
 * @description: 经典蓝牙通信
 */
public class BluetoothPort extends BasicPort {

    private static final String TAG = BluetoothPort.class.getSimpleName();
    private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final String bluetoothMac;

    public BluetoothPort(String bluetoothMac) {
        this.bluetoothMac = bluetoothMac;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void openPort() throws IOException, PortException {
        this.closePort();
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        if (this.adapter == null) {
            throw new PortException("Bluetooth is not support");
        }

        if (!this.adapter.isEnabled()) {
            throw new PortException("Bluetooth is not open");
        }

        this.adapter.cancelDiscovery();

        if (!BluetoothAdapter.checkBluetoothAddress(bluetoothMac)) {
            throw new PortException("Bluetooth address is invalid");
        }

        this.device = this.adapter.getRemoteDevice(bluetoothMac);
        this.socket = this.device.createInsecureRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
        this.socket.connect();
        this.inputStream = this.socket.getInputStream();
        this.outputStream = this.socket.getOutputStream();
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
            if (this.inputStream != null) {
                this.inputStream.close();
                this.inputStream = null;
            }

            if (this.outputStream != null) {
                this.outputStream.close();
                this.outputStream = null;
            }

            if (this.socket != null) {
                this.socket.close();
                this.socket = null;
            }

            return true;
        } catch (IOException e) {
            Log.e(TAG, "Close port error! ", e);
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
