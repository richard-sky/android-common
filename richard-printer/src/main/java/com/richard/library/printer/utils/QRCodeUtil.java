package com.richard.library.printer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @author: Richard
 * @createDate: 2025/6/26 10:29
 * @version: 1.0
 * @description: 二维码工具类
 */
public final class QRCodeUtil {

    /**
     * 生成可配置的二维码打印指令(ESC指令)
     *
     * @param data       二维码内容
     * @param errLevel   纠错等级 (0x30-L, 0x31-M, 0x32-Q, 0x33-H)
     * @param moduleSize 模块尺寸 (1-16)
     * @param charset    字符编码
     * @return ESC/POS指令字节数组
     */
    public static byte[] generateQRCode(String data, byte errLevel, int moduleSize, Charset charset) {
        // 参数校验
        if (errLevel < 0x30 || errLevel > 0x33) {
            throw new IllegalArgumentException("纠错等级参数错误，有效值：0x30(L)-0x33(H)");
        }
        if (moduleSize < 1 || moduleSize > 16) {
            throw new IllegalArgumentException("模块尺寸范围错误，有效值：1-16");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //换行
            outputStream.write(0x0A);

            // 1. 设置二维码模型（固定为QR Code MODEL2）
            byte[] modelCommand = {
                    0x1D, 0x28, 0x6B, 0x04, 0x00,
                    0x31, 0x41, 0x32, 0x00
            };
            outputStream.write(modelCommand);

            // 2. 设置模块尺寸
            byte[] sizeCommand = {
                    0x1D, 0x28, 0x6B, 0x03, 0x00,
                    0x31, 0x43, (byte) moduleSize
            };
            outputStream.write(sizeCommand);

            // 3. 设置纠错等级
            byte[] eccCommand = {
                    0x1D, 0x28, 0x6B, 0x03, 0x00,
                    0x31, 0x45, errLevel
            };
            outputStream.write(eccCommand);

            // 4. 构建数据指令
            byte[] dataBytes = data.getBytes(charset);
            int dataLength = dataBytes.length + 3; // 31 50 30占3字节
            byte pL = (byte) (dataLength & 0xFF);
            byte pH = (byte) ((dataLength >> 8) & 0xFF);

            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            dataStream.write(new byte[]{0x1D, 0x28, 0x6B, pL, pH, 0x31, 0x50, 0x30});
            dataStream.write(dataBytes);
            outputStream.write(dataStream.toByteArray());

            // 5. 打印二维码指令
            byte[] printCommand = {
                    0x1D, 0x28, 0x6B, 0x03, 0x00,
                    0x31, 0x51, 0x30
            };
            outputStream.write(printCommand);

            return outputStream.toByteArray();

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("不支持的编码格式: " + charset.name(), e);
        } catch (IOException e) {
            throw new RuntimeException("IO操作异常", e);
        }
    }

}
