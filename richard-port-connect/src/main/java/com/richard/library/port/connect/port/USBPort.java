package com.richard.library.port.connect.port;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.richard.library.port.connect.exception.PortException;
import com.richard.library.port.connect.helper.USBHelper;
import com.richard.library.port.connect.model.PortInfo;
import com.richard.library.port.connect.model.USBDeviceDTO;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author: Richard
 * @createDate: 2026/4/21 13:54
 * @version: 1.0
 * @description: USB通信
 */
public class USBPort extends BasicPort {

    private static final String TAG = USBPort.class.getSimpleName();
    private int writeTimeout = 1000;
    private int readTimeout = 2000;
    private final Object mLock = new Object();
    private static final String ACTION_USB_PERMISSION = "com.richard.library.port.connect.USB_PERMISSION";
    private final UsbManager usbManager;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private USBReceiver usbReceiver;
    private final PortInfo portInfo;

    public USBPort(PortInfo portInfo) {
        this.portInfo = portInfo;
        this.usbManager = (UsbManager) portInfo.getContext().getSystemService(Context.USB_SERVICE);
    }

    public USBPort(Context context, int usbVendorId, int usbProductId) {
        this(new PortInfo());
        this.portInfo.setContext(context);
        this.portInfo.setUsbVendorId(usbVendorId);
        this.portInfo.setUsbProductId(usbProductId);
        this.portInfo.setAutoRequestPermission(true);
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public void openPort() throws IOException, PortException {
        this.closePort();
        this.registerUSBReceiver();

        USBDeviceDTO usbDevice = USBHelper.get().getUsbDeviceDTO(portInfo.getUsbProductId(), portInfo.getUsbVendorId());
        if (usbDevice == null || usbDevice.getUsbDevice() == null) {
            throw new PortException("The USB device is not connected");
        }

        if (!this.usbManager.hasPermission(usbDevice.getUsbDevice())) {
            Log.e(TAG, "USB is not permission");

            if (this.portInfo.isAutoRequestPermission() && !this.portInfo.isReConnect()) {
                PendingIntent permissionIntent = PendingIntent.getBroadcast(portInfo.getContext(), 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                this.usbManager.requestPermission(usbDevice.getUsbDevice(), permissionIntent);
                int maxTryCount = 5;

                do {
                    Log.e(TAG, "Check user authorization in USB");
                    if (maxTryCount <= 0) {
                        break;
                    }
                    maxTryCount--;
                    SystemClock.sleep(1000);
                } while (!this.usbManager.hasPermission(usbDevice.getUsbDevice()));

                if (!usbManager.hasPermission(usbDevice.getUsbDevice())) {
                    throw new PortException("USB permission not granted");
                }
            }
        }

        UsbInterface usbInterface = null;

        interfaceLoop:
        for (int i = 0, interfaceCount = usbDevice.getUsbDevice().getInterfaceCount(); i < interfaceCount; i++) {
            usbInterface = usbDevice.getUsbDevice().getInterface(i);

            if (usbInterface.getEndpointCount() == 0) {
                continue;
            }

            for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                UsbEndpoint usbEndpoint = usbInterface.getEndpoint(i);
                if (usbEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                    if (usbEndpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                        this.usbEndpointOut = usbEndpoint;
                    } else if (usbEndpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                        this.usbEndpointIn = usbEndpoint;
                    }
                }

                if (this.usbEndpointOut != null && this.usbEndpointIn != null) {
                    break interfaceLoop;
                }
            }
        }

        this.usbInterface = usbInterface;
        this.usbDeviceConnection = this.usbManager.openDevice(usbDevice.getUsbDevice());
        if (this.usbDeviceConnection == null || !this.usbDeviceConnection.claimInterface(this.usbInterface, true)) {
            throw new PortException("Failed to open USB port");
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
        return this.usbDeviceConnection != null
                && this.usbEndpointIn != null
                && this.usbEndpointOut != null
                && USBHelper.get().getUsbDeviceDTO(portInfo.getUsbProductId(), portInfo.getUsbVendorId()) != null;
    }

    @Override
    public int write(byte[] data) throws IOException, PortException {
        return this.write(data, 0, data.length);
    }

    @Override
    public int write(byte[] data, int offset, int len) throws IOException, PortException {
        this.checkPortOpen();
        int transferLength = this.usbDeviceConnection.bulkTransfer(this.usbEndpointOut, data, len, writeTimeout);

        if (transferLength < 0) {
            Log.e(TAG, String.format("Sending data of length %s failed", len));
        }
        return transferLength;
    }

    @Override
    public int read(byte[] buffer) throws IOException, PortException {
        return this.read(buffer, 0, buffer.length);
    }

    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException, PortException {
        this.checkPortOpen();
        return this.usbDeviceConnection.bulkTransfer(this.usbEndpointIn, buffer, buffer.length, readTimeout);
    }

    @Override
    public boolean closePort() {
        synchronized (this.mLock) {
            this.unregisterUSBReceiver();
            if (this.usbInterface != null && this.usbDeviceConnection != null) {
                this.usbDeviceConnection.releaseInterface(this.usbInterface);
                this.usbDeviceConnection.close();
                this.usbDeviceConnection = null;
                return true;
            } else {
                return false;
            }
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

    /**
     * 注册usb广播
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerUSBReceiver() {
        this.unregisterUSBReceiver();

        usbReceiver = new USBReceiver(usbDevice -> {
            if (usbDevice.getProductId() == portInfo.getUsbProductId() && usbDevice.getVendorId() == portInfo.getUsbVendorId()) {
                this.closePort();
            }
        });

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            portInfo.getContext().registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            portInfo.getContext().registerReceiver(usbReceiver, filter);
        }
    }

    /**
     * 取消注册usb广播
     */
    private void unregisterUSBReceiver() {
        if (usbReceiver != null) {
            portInfo.getContext().unregisterReceiver(usbReceiver);
            usbReceiver = null;
        }
    }

    /**
     * USB设备事件广播
     */
    private static class USBReceiver extends BroadcastReceiver {

        private final Callback callback;

        public USBReceiver(@NotNull Callback callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        callback.onUsbDetached(device);
                    }
                }
            }
        }

        interface Callback {
            /**
             * 当usb设备拔除时回调
             */
            void onUsbDetached(UsbDevice usbDevice);
        }
    }
}

