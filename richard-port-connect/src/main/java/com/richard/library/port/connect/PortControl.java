//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.richard.library.port.connect;

import android.content.Context;
import android.util.Log;

import com.richard.library.port.connect.enumerate.PortType;
import com.richard.library.port.connect.exception.PortException;
import com.richard.library.port.connect.model.PortInfo;
import com.richard.library.port.connect.port.BasicPort;
import com.richard.library.port.connect.port.BluetoothPort;
import com.richard.library.port.connect.port.EthernetPort;
import com.richard.library.port.connect.port.SerialPort;
import com.richard.library.port.connect.port.USBPort;

import java.io.IOException;
import java.util.List;

/**
 * 端口控制
 */
public class PortControl {

    private final PortInfo portInfo;
    private BasicPort port = null;

    public PortControl(PortInfo portInfo) {
        this.portInfo = portInfo;
    }

    /**
     * 构造
     *
     * @param context   context
     * @param productId usb 产品id
     * @param vendorId  usb 供应商id
     */
    public PortControl(Context context, String productId, String vendorId) {
        this.portInfo = new PortInfo().applyUSB(context, productId, vendorId);
    }

    /**
     * 构造
     *
     * @param bluetoothMac 蓝牙Mac地址
     */
    public PortControl(String bluetoothMac) {
        this.portInfo = new PortInfo().applyBluetooth(bluetoothMac);
    }

    /**
     * 构造
     *
     * @param ethernetIP   网络IP地址
     * @param ethernetPort 网口端口号
     */
    public PortControl(String ethernetIP, int ethernetPort) {
        this.portInfo = new PortInfo().applyNet(ethernetIP, ethernetPort);
    }

    /**
     * 构造
     *
     * @param baudRate   波特率
     * @param serialPath 串口地址
     */
    public PortControl(int baudRate, String serialPath) {
        this.portInfo = new PortInfo().applySerial(baudRate, serialPath);
    }

    /**
     * 构造
     *
     * @param baudRate   波特率
     * @param serialPath 串口地址
     * @param flags      标记
     */
    public PortControl(int baudRate, String serialPath, int flags) {
        this.portInfo = new PortInfo().applySerial(baudRate, serialPath, flags);
    }

    /**
     * 连接并打开端口
     */
    public void connect() throws PortException, IOException {
        try {
            this.startConnect(false);
        } catch (Throwable e) {
            Log.e("PortControl", String.format("error:(connect)%s-即将重试连接", e.getMessage()));
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            this.startConnect(true);
        }
    }

    /**
     * 开始连接并打开端口
     */
    private void startConnect(boolean isReConnect) throws PortException, IOException {
        this.portInfo.setReConnect(isReConnect);
        if (port == null) {
            switch (this.portInfo.getPortType()) {
                case USB:
                    this.port = new USBPort(portInfo);
                    break;
                case Bluetooth:
                    this.port = new BluetoothPort(portInfo.getBluetoothMac());
                    break;
                case Ethernet:
                    this.port = new EthernetPort(portInfo.getEthernetIP(), portInfo.getEthernetPort());
                    break;
                case Serial:
                    this.port = new SerialPort(portInfo.getSerialPath(), portInfo.getBaudRate(), portInfo.getFlags());
                    break;
                default:
                    throw new PortException("未找到相应的设备连接设备");
            }
        }

        this.port.openPort();
    }

    /**
     * 设置USB连接时是否自动请求权限
     */
    void setAutoRequestPermission(boolean isAutoRequestPermission) {
        this.portInfo.setAutoRequestPermission(isAutoRequestPermission);
    }

    /**
     * 获取设备连接名称
     */
    public String getDeviceLinkName() {
        switch (this.portInfo.getPortType()) {
            case USB:
                return portInfo.getUsbPathName();
            case Ethernet:
                return String.format("%s:%s", portInfo.getEthernetIP(), portInfo.getEthernetPort());
            case Bluetooth:
                return portInfo.getBluetoothMac();
            case Serial:
                return portInfo.getSerialPath();
            case Unknown:
            default:
                return "";
        }
    }


    /**
     * 获取当前连接端口类型
     */
    public PortType getPortType() {
        return portInfo.getPortType();
    }

    /**
     * 获取连接端口信息(耗时方法)
     */
    public PortInfo getPortInfo() {
        this.portInfo.setOpened(this.port.checkValid());
        return this.portInfo;
    }

    /**
     * 检查设备连接状态
     *
     * @return 是否已连接(耗时方法)
     */
    public boolean checkLinkedState() {
        if (this.port == null) {
            return false;
        }
        return this.getPortInfo().isOpened();
    }

    /**
     * 是否已连接（非耗时）
     */
    public boolean isLinked() {
        if (this.port == null) {
            return false;
        }
        return this.port.isOpened();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (this.port == null) {
            return;
        }

        this.port.closePort();
        this.port = null;
    }

    /**
     * 写入数据到当前已连接设备
     *
     * @param data 写入数据
     */
    public int write(List<byte[]> data) throws PortException, IOException {
        if (data == null || data.isEmpty()) {
            return -1;
        }

        int writeCount = -1;
        for (byte[] item : data) {
            if (writeCount < 0) {
                writeCount = this.write(item);
            } else {
                writeCount += this.write(item);
            }
        }

        return writeCount;
    }

    /**
     * 写入数据
     *
     * @param data 数据
     */
    public int write(byte[] data) throws PortException, IOException {
        if (data == null || data.length == 0) {
            return -1;
        }

        try {
            return this.port.write(data);
        } catch (IOException | PortException e) {
            Log.e("PortControl", String.format("error:(write)%s-即将重试连接并写入", e.getMessage()));
            this.connect();
            return this.port.write(data);
        }
    }

    /**
     * 向端口写入数据
     */
    public int write(byte[] data, int offset, int len) throws PortException, IOException {
        if (data == null || data.length == 0) {
            return -1;
        }

        try {
            return this.port.write(data, offset, len);
        } catch (IOException | PortException e) {
            Log.e("PortControl", String.format("error:(write)%s-即将重试连接并写入", e.getMessage()));
            this.connect();
            return this.port.write(data, offset, len);
        }
    }

    /**
     * 读取端口数据
     */
    public int read(byte[] buffer, int offset, int count) throws PortException, IOException {
        return this.port.read(buffer, offset, count);
    }

    /**
     * 读取端口数据
     */
    public int read(byte[] buffer) throws PortException, IOException {
        return this.read(buffer, 0, buffer.length);
    }
}
