package com.richard.library.bluetooth.core.callback;

import com.richard.library.bluetooth.core.data.BleDevice;
import com.richard.library.bluetooth.core.exception.BleException;

/**
 * @author: Richard
 * @createDate: 2024/12/31 11:27
 * @version: 1.0
 * @description: 连接并写入数据回调
 */
public abstract class ConnectAndWriteCallback {

    /**
     * 写入成功回调
     *
     * @param device    写入设备
     * @param current   当前写入成功的数据的长度
     * @param total     总共需要写入数据的长度
     * @param justWrite 本次写入成功的数据
     */
    public abstract void onWriteSuccess(BleDevice device, int current, int total, byte[] justWrite);

    /**
     * 写入失败回调
     *
     * @param device    写入设备
     * @param exception 异常
     */
    public abstract void onWriteFailure(BleDevice device, BleException exception);

}
