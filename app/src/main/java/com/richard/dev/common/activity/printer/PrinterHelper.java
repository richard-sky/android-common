package com.richard.dev.common.activity.printer;

import android.content.Context;

import com.richard.library.basic.util.ToastUtil;
import com.richard.library.bluetooth.core.BleManager;
import com.richard.library.bluetooth.core.callback.ConnectAndWriteCallback;
import com.richard.library.bluetooth.core.data.BleDevice;
import com.richard.library.bluetooth.core.exception.BleException;
import com.richard.library.context.util.UIThread;
import com.richard.library.port.connect.PortControl;
import com.richard.library.port.connect.PortManager;
import com.richard.library.port.connect.enumerate.PortType;
import com.richard.library.port.connect.exception.PortException;
import com.richard.library.port.connect.helper.USBHelper;
import com.richard.library.printer.enumerate.TicketSpec;
import com.richard.library.printer.utils.PrinterCmdPager;

import java.io.IOException;

/**
 * author：Richard
 * time：2021-06-18 16:09
 * version：v1.0.0
 * description：打印机辅助类
 */
public final class PrinterHelper {

    private static PrinterHelper instance;

    /**
     * 获取单例
     */
    public static PrinterHelper get() {
        if (instance == null) {
            synchronized (PrinterHelper.class) {
                if (instance == null) {
                    instance = new PrinterHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 网络端口打印
     *
     * @param ip       ip地址
     * @param port     端口
     * @param data     打印数据
     * @param printNum 打印份数
     */
    public void netPortPrint(String ip, int port, byte[] data, int printNum) throws PortException, IOException {
        PortControl printer = PortManager.get().connectNetPort(ip, port);
        try {
            this.print(printer, printNum, data);
        } finally {
            printer.disconnect();
        }
    }

    /**
     * 蓝牙端口打印
     *
     * @param bluetoothID 蓝牙id
     * @param data        打印数据
     * @param printNum    打印份数
     */
    public void bluetoothPrint(String bluetoothID, byte[] data, int printNum) throws PortException {
//        PortControl printer = PortManager.get().connectBTPort(bluetoothID);
//        try {
//            this.print(printer, printNum, data);
//        }finally {
////            printer.disconnect();
//        }

        BleManager.getInstance().connectAndWrite(bluetoothID, data, false, new ConnectAndWriteCallback() {
            @Override
            public void onWriteSuccess(BleDevice device, int current, int total, byte[] justWrite) {
                if (current < total) {
                    return;
                }

                if (printNum - 1 <= 0) {
                    BleManager.getInstance().disconnect(device);
                } else {
                    try {
                        bluetoothPrint(bluetoothID, data, printNum - 1);
                    } catch (PortException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onWriteFailure(BleDevice device, BleException exception) {
                BleManager.getInstance().disconnect(device);
                ToastUtil.showLongError(String.format("蓝牙打印异常: %s", exception));
            }
        });
    }

    /**
     * 串口打印
     *
     * @param serialPath 串口地址
     * @param baudRate   波特率
     * @param data       打印数据
     * @param printNum   打印份数
     */
    public void serialPrint(String serialPath, int baudRate, byte[] data, int printNum) throws PortException, IOException {
        PortControl printer = null;
        try {
            printer = PortManager.get().connectSerialPort(serialPath, baudRate);
            this.print(printer, printNum, data);
        } catch (SecurityException e) {
            throw new PortException("该端口不允许访问");
        } finally {
            if (printer != null) {
                printer.disconnect();
            }
        }
    }

    /**
     * usb打印
     *
     * @param context      context
     * @param usbProductId usb产品id
     * @param data         打印数据
     * @param printNum     打印份数
     */
    public void usbPrint(Context context, String usbProductId, byte[] data, int printNum) throws PortException, IOException {
        int productId = Integer.parseInt(usbProductId);

        if (USBHelper.get().isHasPermission(productId, 0)) {
            this.startUsbPrint(context, usbProductId, data, printNum);
            return;
        }

        if (USBHelper.get().getUsbDeviceDTO(productId, 0) == null) {
            throw new PortException("未发现打印设备，请检查打印设备是否连接正常");
        }

        try {
            startUsbPrint(context, usbProductId, data, printNum);
        } catch (Throwable e) {
            UIThread.runOnUiThread(() -> {
                USBHelper.get().addRun(Integer.parseInt(usbProductId), 0, () -> {
                    try {
                        startUsbPrint(context, usbProductId, data, printNum);
                    } catch (PortException | IOException pe) {
                        pe.printStackTrace();
                    }
                });
                USBHelper.get().requestPermission(productId, 0);
            });
        }
    }

    /**
     * 开始usb打印
     *
     * @param context      context
     * @param usbProductId usb产品id
     * @param data         打印数据
     * @param printNum     打印份数
     */
    private void startUsbPrint(Context context, String usbProductId, byte[] data, int printNum)
            throws PortException, IOException {
        PortControl printer = PortManager.get().connectUSBPortNoAutoRequestPermission(context, usbProductId, null);
        try {
            this.print(printer, printNum, data);
        } finally {
            printer.disconnect();
        }
    }

    /**
     * 打印
     *
     * @param portControl 端口控制对象
     * @param printNum    打印份数
     * @param data        打印数据
     * @throws PortException 端口异常
     */
    private void print(PortControl portControl, int printNum, byte[] data) throws PortException, IOException {
        PrinterCmdPager pager;
        int pageNo = 1;
        int pageSize;

        switch (portControl.getPortType()) {
            case USB:
            case Serial:
            case Ethernet:
                pageSize = 100;
                break;
            case Bluetooth:
            default:
                pageSize = 1024;
        }

        //写入打印内容数据
        //分段批次打印（防止小票打印内容不全的问题）
        pager = new PrinterCmdPager(data, pageSize);
        for (int i = 0; i < printNum; i++) {
            pageNo = 1;
            while (true) {
                byte[] pageList = pager.page(pageNo++);
                if (pageList.length == 0) {
                    break;
                }
                portControl.write(pageList);
            }
        }
    }

    /**
     * 断开所有连接
     */
    public void disconnectAll() {
        PortManager.get().disconnectAll();
        BleManager.getInstance().disconnectAllDevice();
    }

    /**
     * 测试打印
     *
     * @param context      context(建议传applicationContext)
     * @param isPrintLabel 是否属于打印标签，true：打印标签、false:打印票据
     * @param portType     打印机连接端口类型
     * @param ticketSpec   小票规格
     * @param linkTarget   连接目标(USB:productId、蓝牙：bluetoothId、网口：ip)
     * @param port         网络端口号(仅linkTarget为网口时有效)
     */
    public static void testPrint(Context context, boolean isPrintLabel, PortType portType
            , TicketSpec ticketSpec, String linkTarget, String port) throws PortException, IOException {

        byte[] data;
        if (isPrintLabel) {
            data = PrinterUtil.getLabelTestData(40, 30);
        } else {
            data = PrinterUtil.getTicketTestData(ticketSpec, portType, linkTarget, port);
        }

        switch (portType) {
            case USB:
                PrinterHelper.get().usbPrint(context, linkTarget, data, 1);
                break;
            case Ethernet:
                if (port == null) {
                    throw new PortException("端口号不能为空");
                }
                PrinterHelper.get().netPortPrint(linkTarget, Integer.parseInt(port), data, 1);
                break;
            case Bluetooth:
                PrinterHelper.get().bluetoothPrint(linkTarget, data, 1);
                break;
            case Serial:
                PrinterHelper.get().serialPrint(linkTarget, 9600, data, 1);
                break;
            default:
                throw new PortException("未支持该打印机");
        }
    }
}
