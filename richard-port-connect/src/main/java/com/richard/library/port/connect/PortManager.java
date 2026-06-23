package com.richard.library.port.connect;

import android.content.Context;

import com.richard.library.port.connect.exception.PortException;
import com.richard.library.port.connect.model.PortInfo;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * author Richard
 * date 2021/1/5 14:41
 * version V1.0
 * description: 端口管理类
 */
public final class PortManager {

    private final Map<String, PortControl> portMap = new HashMap<>();

    private PortManager() {
    }

    private static final class HelperHolder {
        static final PortManager helper = new PortManager();
    }

    public static PortManager get() {
        return HelperHolder.helper;
    }

    /**
     * 连接端口设备
     *
     * @param portInfo 端口
     */
    public PortControl connect(PortInfo portInfo) throws PortException, IOException {
        switch (portInfo.getPortType()) {
            case USB:
                return connectUSBPort(portInfo.getContext(), String.valueOf(portInfo.getUsbProductId()), String.valueOf(portInfo.getUsbVendorId()));
            case Bluetooth:
                return connectBTPort(portInfo.getBluetoothMac());
            case Serial:
                return connectSerialPort(portInfo.getSerialPath(), portInfo.getBaudRate());
            case Ethernet:
                return connectNetPort(portInfo.getEthernetIP(), portInfo.getEthernetPort());
            case Unknown:
            default:
                throw new PortException("未知端口类型");
        }
    }

    /**
     * 连接网络设备
     *
     * @param ip   IP 地址
     * @param port 端口
     */
    public PortControl connectNetPort(String ip, int port) throws PortException, IOException {
        String key = getNetPortKey(ip, port);
        PortControl portControl = portMap.get(key);
        if (portControl == null) {
            portControl = new PortControl(ip, port);
            portMap.put(key, portControl);
        }

        if (!portControl.isLinked()) {
            portControl.connect();
        }

        return portControl;
    }

    /**
     * 连接蓝牙设备
     *
     * @param bluetoothID 蓝牙ID
     */
    public PortControl connectBTPort(String bluetoothID) throws PortException, IOException {
        PortControl portControl = portMap.get(getBTPortKey(bluetoothID));
        if (portControl == null) {
            portControl = new PortControl(bluetoothID);
            portMap.put(bluetoothID, portControl);
        }

        if (!portControl.isLinked()) {
            portControl.connect();
        }

        return portControl;
    }

    /**
     * 连接USB设备（会主动请求USB权限）
     *
     * @param context   context
     * @param productId USB产品id
     * @param vendorId  USB供应商id
     */
    public PortControl connectUSBPort(Context context, String productId, String vendorId) throws PortException, IOException {
        String key = getUsbPortKey(productId, vendorId);
        PortControl portControl = portMap.get(key);
        if (portControl == null) {
            portControl = new PortControl(context, productId, vendorId);
            portMap.put(key, portControl);
        }

        portControl.setAutoRequestPermission(true);

        if (!portControl.isLinked()) {
            portControl.connect();
        }

        return portControl;
    }

    /**
     * 连接USB设备（不主动请求USB权限）
     *
     * @param context   context
     * @param productId USB产品id
     * @param vendorId  USB供应商id
     */
    public PortControl connectUSBPortNoAutoRequestPermission(Context context, String productId, String vendorId) throws PortException, IOException {
        String key = getUsbPortKey(productId, vendorId);
        PortControl portControl = portMap.get(key);
        if (portControl == null) {
            portControl = new PortControl(context, productId, vendorId);
            portMap.put(key, portControl);
        }

        portControl.setAutoRequestPermission(false);

        if (!portControl.isLinked()) {
            portControl.connect();
        }

        return portControl;
    }

    /**
     * 连接串口设备
     *
     * @param serialPath 串口地址
     * @param baudRate   波特率
     */
    public PortControl connectSerialPort(String serialPath, int baudRate) throws PortException, IOException {
        return this.connectSerialPort(serialPath, baudRate, 0);
    }

    /**
     * 连接串口设备
     *
     * @param serialPath 串口地址
     * @param baudRate   波特率
     */
    public PortControl connectSerialPort(String serialPath, int baudRate, int flag) throws PortException, IOException {
        PortControl portControl = portMap.get(getSerialPortKey(serialPath));
        if (portControl == null) {
            portControl = new PortControl(baudRate, serialPath, flag);
            portMap.put(serialPath, portControl);
        }

        if (!portControl.isLinked()) {
            portControl.connect();
        }

        return portControl;
    }

    /**
     * 获取PortControl
     *
     * @param portKey 连接对象key
     */
    public PortControl getPort(String portKey) {
        return portMap.get(portKey);
    }

    /**
     * 断开指定连接对象
     *
     * @param portKey 连接对象key
     */
    public void disconnect(String portKey) {
        PortControl value = portMap.get(portKey);
        if (value == null) {
            return;
        }

        try {
            value.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            portMap.remove(portKey);
        }
    }

    /**
     * 断开所有设备连接
     */
    public void disconnectAll() {
        Collection<PortControl> portList = portMap.values();
        for (PortControl item : portList) {
            try {
                item.disconnect();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        portMap.clear();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 获取网口连接方式的管理Map key
     *
     * @param ip   IP地址
     * @param port 端口号
     */
    public static String getNetPortKey(String ip, int port) {
        return String.format("%s:%s", ip, port);
    }

    /**
     * 获取蓝牙连接方式的管理Map key
     *
     * @param bluetoothID 蓝牙id
     */
    public static String getBTPortKey(String bluetoothID) {
        //nothing
        return bluetoothID;
    }

    /**
     * 获取USB连接方式的管理Map key
     *
     * @param productId USB产品id
     * @param vendorId  USB供应商id
     */
    public static String getUsbPortKey(String productId, String vendorId) {
        return String.format("%s:%s", productId, vendorId);
    }

    /**
     * 获取串口连接方式的管理Map key
     *
     * @param serialPath 串口地址
     */
    public static String getSerialPortKey(String serialPath) {
        //nothing
        return serialPath;
    }
}
