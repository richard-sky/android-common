package com.richard.dev.common.activity.speech.model;

import java.io.Serializable;

/**
 * @author: Richard
 * @createDate: 2026/6/29 11:01
 * @version: 1.0
 * @description: 语音设备激活
 */
public class DeviceActive implements Serializable {

    /// 设备信息
    private String deviceInfo;

    /// 设备名称
    private String deviceName;

    /// 设备密钥
    private String deviceSecret;

    /// 产品ID
    private String productId;

    /// 错误码
    private String errId;

    /// 错误信息
    private String error;

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSecret() {
        return deviceSecret;
    }

    public void setDeviceSecret(String deviceSecret) {
        this.deviceSecret = deviceSecret;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getErrId() {
        return errId;
    }

    public void setErrId(String errId) {
        this.errId = errId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
