package com.richard.library.port.connect.model;

import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.text.TextUtils;


import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * author：Richard
 * time：2021-06-18 13:53
 * version：v1.0.0
 * description：USB设备信息
 */
public class USBDeviceDTO implements Serializable {

    private static final long serialVersionUID = -1842517539301797385L;

    /**
     * 名称
     */
    private String name;

    /**
     * 生产商名称
     */
    private String manufacturerName;

    /**
     * 供应商id
     */
    private int vendorId;

    /**
     * 产品id
     */
    private int productId;

    /**
     * clazz
     */
    private int clazz;

    /**
     * subclass
     */
    private int subclass;

    /**
     * 协议
     */
    private int protocol;

    /**
     * USB设备
     */
    @Nullable
    private UsbDevice usbDevice;

    public USBDeviceDTO(String name, String manufacturerName, int vendorId, int productId, int clazz,
                        int subclass, int protocol, @Nullable UsbDevice usbDevice) {
        this.name = name;
        this.manufacturerName = manufacturerName;
        this.vendorId = vendorId;
        this.productId = productId;
        this.clazz = clazz;
        this.subclass = subclass;
        this.protocol = protocol;
        this.usbDevice = usbDevice;
    }

    public USBDeviceDTO(@Nullable UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }

    public USBDeviceDTO() {
    }

    /**
     * 获取显示名称
     */
    public String getShowName(){
        if (manufacturerName == null && usbDevice != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return String.format("%s-%s", usbDevice.getManufacturerName(), this.getProductId());
            }
        }
        return String.format("%s-%s", manufacturerName, this.getProductId());
    }

    public String getName() {
        if (name == null && usbDevice != null) {
            if (!TextUtils.isEmpty(usbDevice.getDeviceName())) {
                return usbDevice.getDeviceName();
            }
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturerName() {
        if (manufacturerName == null && usbDevice != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return usbDevice.getManufacturerName();
            }
        }
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public int getVendorId() {
        if (vendorId == 0 && usbDevice != null) {
            return usbDevice.getVendorId();
        }
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getProductId() {
        if (productId == 0 && usbDevice != null) {
            return usbDevice.getProductId();
        }
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getClazz() {
        if (clazz == 0 && usbDevice != null) {
            return usbDevice.getDeviceClass();
        }
        return clazz;
    }

    public void setClazz(int clazz) {
        this.clazz = clazz;
    }

    public int getSubclass() {
        if (subclass == 0 && usbDevice != null) {
            return usbDevice.getDeviceSubclass();
        }
        return subclass;
    }

    public void setSubclass(int subclass) {
        this.subclass = subclass;
    }

    public int getProtocol() {
        if (protocol == 0 && usbDevice != null) {
            return usbDevice.getDeviceProtocol();
        }
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    @Nullable
    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public void setUsbDevice(@Nullable UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }
}
