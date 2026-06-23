package com.richard.library.port.connect.exception;


/**
 * author Richard
 * date 2020/8/10 10:10
 * version V1.0
 * description: 自定义相关端口异常
 */
public class PortException extends Exception{

    public PortException(String message){
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
