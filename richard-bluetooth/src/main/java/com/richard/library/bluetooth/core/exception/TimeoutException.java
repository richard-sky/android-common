package com.richard.library.bluetooth.core.exception;


public class TimeoutException extends BleException {

    public TimeoutException() {
        super(ERROR_CODE_TIMEOUT, "无法连接蓝牙，请检查蓝牙是否开启或是否在有效距离内");
    }

}
