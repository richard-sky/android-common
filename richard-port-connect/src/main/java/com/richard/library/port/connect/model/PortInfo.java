package com.richard.library.port.connect.model;

import android.content.Context;
import android.text.TextUtils;

import com.richard.library.port.connect.enumerate.PortType;

import java.io.Serializable;

/**
 * author Richard
 * date 2020/8/18 10:44
 * version V1.0
 * description: 端口信息
 */
public class PortInfo implements Serializable {

    /**
     * 端口类型
     */
    private PortType portType;

    //-------------USB---------------
    /**
     * usb名称
     */
    private String usbPathName;

    /**
     * usb供应商ID
     */
    private int usbVendorId;

    /**
     * usb 产品ID
     */
    private int usbProductId;

    /**
     * 是否主动获取USB连接权限
     */
    private boolean isAutoRequestPermission;

    /**
     * 是否属于USB重连接操作
     */
    private boolean isReConnect;

    //-------------NET---------------

    /**
     * 网络端口
     */
    private int ethernetPort;

    /**
     * 网络IP地址
     */
    private String ethernetIP;

    //-------------蓝牙---------------
    /**
     * 蓝牙地址
     */
    private String bluetoothMac;

    //-------------串口---------------
    /**
     * 串行path
     */
    private String serialPath;

    /**
     * 波特率
     */
    private int baudRate;

    /**
     * 标记
     */
    private int flags = 0;

    //-------------其它---------------
    /**
     * context
     */
    private Context context;

    /**
     * 是否已打开端口连接
     */
    private boolean isOpened;

    public PortInfo() {
        this.portType = PortType.Unknown;
        this.usbPathName = "";
        this.usbProductId = 0;
        this.usbVendorId = 0;
        this.ethernetPort = 0;
        this.ethernetIP = "";
        this.bluetoothMac = "";
        this.serialPath = "";
        this.baudRate = 0;
        this.context = null;
        this.isOpened = false;
        this.isAutoRequestPermission = true;
        this.isReConnect = false;
    }

    /**
     * 设置USB信息
     *
     * @param context   Context
     * @param productId usb产品ID
     * @param vendorId  usb供应商ID
     */
    public PortInfo applyUSB(Context context, String productId, String vendorId) {
        this.portType = PortType.USB;
        this.context = context;

        if (!TextUtils.isEmpty(productId)) {
            this.usbProductId = Integer.parseInt(productId);
        }

        if (!TextUtils.isEmpty(vendorId)) {
            this.usbVendorId = Integer.parseInt(vendorId);
        }
        return this;
    }

    /**
     * 设置蓝牙信息
     *
     * @param bluetoothMac 蓝牙地址
     */
    public PortInfo applyBluetooth(String bluetoothMac) {
        this.portType = PortType.Bluetooth;
        this.bluetoothMac = bluetoothMac;
        return this;
    }

    /**
     * 设置网络连接信息
     *
     * @param ethernetIP   网络IP地址
     * @param ethernetPort 网口端口号
     */
    public PortInfo applyNet(String ethernetIP, int ethernetPort) {
        this.portType = PortType.Ethernet;
        this.ethernetIP = ethernetIP;
        this.ethernetPort = ethernetPort;
        return this;
    }

    /**
     * 设置串口信息
     *
     * @param baudRate   波特率
     * @param serialPath 串口地址
     */
    public PortInfo applySerial(int baudRate, String serialPath) {
        this.portType = PortType.Serial;
        this.baudRate = baudRate;
        this.serialPath = serialPath;
        return this;
    }

    /**
     * 设置串口信息
     *
     * @param baudRate   波特率
     * @param serialPath 串口地址
     * @param flags      标记
     */
    public PortInfo applySerial(int baudRate, String serialPath, int flags) {
        this.portType = PortType.Serial;
        this.baudRate = baudRate;
        this.serialPath = serialPath;
        this.flags = flags;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PortType getPortType() {
        return portType;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public String getUsbPathName() {
        return usbPathName;
    }

    public void setUsbPathName(String usbPathName) {
        this.usbPathName = usbPathName;
    }

    public int getUsbProductId() {
        return usbProductId;
    }

    public void setUsbProductId(int usbProductId) {
        this.usbProductId = usbProductId;
    }

    public int getUsbVendorId() {
        return usbVendorId;
    }

    public void setUsbVendorId(int usbVendorId) {
        this.usbVendorId = usbVendorId;
    }

    public int getEthernetPort() {
        return ethernetPort;
    }

    public void setEthernetPort(int ethernetPort) {
        this.ethernetPort = ethernetPort;
    }

    public String getEthernetIP() {
        return ethernetIP;
    }

    public void setEthernetIP(String ethernetIP) {
        this.ethernetIP = ethernetIP;
    }

    public String getBluetoothMac() {
        return bluetoothMac;
    }

    public void setBluetoothMac(String bluetoothMac) {
        this.bluetoothMac = bluetoothMac;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public String getSerialPath() {
        return serialPath;
    }

    public void setSerialPath(String serialPath) {
        this.serialPath = serialPath;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public boolean isReConnect() {
        return isReConnect;
    }

    public void setReConnect(boolean reConnect) {
        isReConnect = reConnect;
    }

    public boolean isAutoRequestPermission() {
        return isAutoRequestPermission;
    }

    public void setAutoRequestPermission(boolean autoRequestPermission) {
        isAutoRequestPermission = autoRequestPermission;
    }
}
