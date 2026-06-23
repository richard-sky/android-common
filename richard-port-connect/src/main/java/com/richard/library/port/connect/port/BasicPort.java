package com.richard.library.port.connect.port;

import com.richard.library.port.connect.exception.PortException;

import java.io.IOException;

/**
 * @author: Richard
 * @createDate: 2026/4/21 13:46
 * @version: 1.0
 * @description: 端口实现抽象类
 */
public abstract class BasicPort {

    /**
     * 打开端口
     */
    public abstract void openPort() throws IOException, PortException;

    /**
     * 检查端口是否有效(耗时)
     */
    public abstract boolean checkValid();

    /**
     * 当前端口是否已打开(非耗时)
     */
    public abstract boolean isOpened();

    /**
     * 向端口写入数据
     *
     * @param data byte数据
     * @return 写入数据长度
     * @throws IOException io异常
     */
    public abstract int write(byte[] data) throws IOException, PortException;

    /**
     * 向端口写入数据
     *
     * @param data   byte数据
     * @param offset 写入数据时的偏移量
     * @param len    需要写入的数据量
     * @return 写入数据长度
     * @throws IOException io异常
     */
    public abstract int write(byte[] data, int offset, int len) throws IOException, PortException;

    /**
     * 读取端口的数据
     *
     * @param buffer 读取到的数据结果
     * @return 读取到数据长度
     * @throws IOException io异常
     */
    public abstract int read(byte[] buffer) throws IOException, PortException;

    /**
     * 读取端口的数据
     *
     * @param buffer 读取到的数据结果
     * @param offset 读取到数据存储的起始偏移量
     * @param len    读取到数据存储到buffer的最大长度
     * @return 读取到的数据长度
     * @throws IOException io异常
     */
    public abstract int read(byte[] buffer, int offset, int len) throws IOException, PortException;

    /**
     * 关闭端口连接
     *
     * @return 是否关闭成功
     */
    public abstract boolean closePort();
}

