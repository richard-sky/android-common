package com.richard.library.printer.utils;

import androidx.annotation.NonNull;

import com.richard.library.printer.command.PrinterStateCmd;

/**
 * @author: Richard
 * @createDate: 2024/1/24 20:03
 * @version: 1.0
 * @description: 打印机状态相关操作辅助类
 */
public final class PrinterStateUtil {

    /**
     * 获取打印机状态(来自美松打印SDK)
     */
    public static int getPrinterStateCode(@NonNull GetState reader) throws Throwable {
        int iRet = -1;
        byte[] bRead1 = new byte[1];
        byte[] bWrite1 = PrinterStateCmd.GetStatus1();

        reader.write(bWrite1);
        if (reader.read(bRead1) > 0) {
            iRet = PrinterStateCmd.CheckStatus1(bRead1[0]);
        }

        if (iRet != 0)
            return iRet;

        byte[] bRead2 = new byte[1];
        byte[] bWrite2 = PrinterStateCmd.GetStatus2();
        reader.write(bWrite2);
        if (reader.read(bRead2) > 0) {
            iRet = PrinterStateCmd.CheckStatus2(bRead2[0]);
        }

        if (iRet != 0)
            return iRet;

        byte[] bRead3 = new byte[1];
        byte[] bWrite3 = PrinterStateCmd.GetStatus3();
        reader.write(bWrite3);
        if (reader.read(bRead3) > 0) {
            iRet = PrinterStateCmd.CheckStatus3(bRead3[0]);
        }

        if (iRet != 0)
            return iRet;

        byte[] bRead4 = new byte[1];
        byte[] bWrite4 = PrinterStateCmd.GetStatus4();
        reader.write(bWrite4);
        if (reader.read(bRead4) > 0) {
            iRet = PrinterStateCmd.CheckStatus4(bRead4[0]);
        }
        return iRet;
    }

    /**
     * 获取打印状态的文字描述
     */
    public static String getPrinterStateMessage(int stateCode) {
        //0 打印机正常 、1 打印机未连接或未上电、2 打印机和调用库不匹配
        //3 打印头打开 、4 切刀未复位 、5 打印头过热 、6 黑标错误 、7 纸尽 、8 纸将尽
        switch (stateCode) {
            case 0:
                return "打印机正常";
            case 1:
                return "打印机未连接或未上电";
            case 2:
                return "打印机和调用库不匹配";
            case 3:
                return "打印头打开";
            case 4:
                return "切刀未复位";
            case 5:
                return "打印头过热";
            case 6:
                return "黑标错误";
            case 7:
                return "纸尽";
            case 8:
                return "纸将尽";
            case -500:
                return "打印驱动为空";
        }
        return "未知错误异常" + stateCode;
    }

    /**
     * 获取打印机状态(来自研科)
     */
    public static int getPrinterStateCode2(@NonNull GetState reader) throws Throwable {
        int stateCode = -1;
        byte[] read = new byte[4];
        byte[] cmd = new byte[]{16, 4, 4};

        reader.write(cmd);
        if (reader.read(read) > 0) {
            if ((read[0] & 96) > 0) {
                stateCode = 2;
            } else {
                cmd = new byte[]{29, 97, 8};
                reader.write(cmd);
                stateCode = reader.read(read) > 0 ? 1 : 0;
            }
        }
        return stateCode;
    }

    /**
     * 获取打印机状态消息
     * @param state 状态
     */
    public static String getPrinterStateMessage2(int state){
        String message = "";
        switch (state) {
            case 1:
                message = "打印完成";
                break;
            case 2:
                message = "打印机缺纸";
                break;
            case 0:
                message = "打印任务进行中";
                break;
            case -500:
                message = "打印未连接";
                break;
            default:
                message = "未知错误：" + state;
        }
        return message;
    }

    /**
     * 端口数据读取回调接口
     */
    public interface GetState {

        /**
         * 发送读取状态的指令
         *
         * @param cmd 指令
         * @return 写入成功的长度
         */
        int write(byte[] cmd) throws Throwable;

        /**
         * 读取状态
         *
         * @param buffer 读取到的执行指令结果内容
         * @return 读取到的长度
         */
        int read(byte[] buffer) throws Throwable;

    }
}
