package com.richard.dev.common.activity.printer;

import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.dev.common.R;
import com.richard.dev.common.application.App;
import com.richard.dev.common.databinding.ActivityPrinterBinding;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.bluetooth.SelectBleDialogV2;
import com.richard.library.bluetooth.core.BleManager;
import com.richard.library.bluetooth.core.BleQueueSender;
import com.richard.library.bluetooth.core.data.BleDevice;
import com.richard.library.port.connect.PortControl;
import com.richard.library.port.connect.PortManager;
import com.richard.library.port.connect.exception.PortException;
import com.richard.library.port.connect.helper.USBHelper;
import com.richard.library.printer.command.DZELabelCmd;
import com.richard.library.printer.command.PrinterCmdUtil;
import com.richard.library.printer.enumerate.Align;
import com.richard.library.printer.enumerate.TicketSpec;
import com.richard.library.printer.utils.ESCBarcodeUtil;
import com.richard.library.printer.utils.PrintParams;

import java.io.IOException;

@Route(path = "/test/printer")
public class TestPrinterActivity extends BasicBindingActivity<ActivityPrinterBinding> {

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_printer);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("测试打印");
        navigationbar.setTitleTextViewShow(true);

        USBHelper.get().init(App.getInstance());
    }

    @Override
    public void bindListener() {
        binding.btnSelectBle.setOnClickListener(v -> {
            showSelectBleWindow(v);
        });

        binding.btnTestPrint.setOnClickListener(v -> {
            generatorPrintData();
//                startPrint("8211",null,generatorPrintData());
//                startPrint("512",null,generatorPrintData());
//                startPrint("192.168.3.200","9100",generatorPrintData());

//                List<USBDeviceDTO> devices = USBHelper.get().getDeviceList();
//                for (USBDeviceDTO item : devices) {
//                    Log.d("testtt", String.format("Device---> %s[%s][productId = %s,vendorId = %s]", item.getManufacturerName(), item.getShowName(), item.getProductId(), item.getVendorId()));
//                }
        });

        binding.btnTestSerial.setOnClickListener((v) -> {
            new Thread(() -> {
                try {
                    PortControl printer = PortManager.get().connectSerialPort("/dev/ttyS3", 9600);
                    int count = 10;
                    while (count > 0) {
                        byte[] buffer = new byte[64];
                        printer.read(buffer);
                        Log.d("testtt", new String(buffer, 0, buffer.length));
                        count--;

                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (PortException | IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        binding.btnTestHoneywellPrint.setOnClickListener((v) -> {
            new Thread(this::testHoneywellLabelPrint).start();
        });
    }

    private void showSelectBleWindow(View v) {
//        SelectBleDialogV2.startForResult(getSupportFragmentManager(), true, device -> {
//            BluetoothDevice bleDevice = device.getDevice();
//            classicBlePrint(bleDevice.getAddress(), null, generatorPrintData());
//        });
        //classicBlePrint("DC:0D:30:7A:B6:69",null,generatorPrintData());
        SelectBleDialogV2.start(
                getSupportFragmentManager()
                , true
                , new SelectBleDialogV2.Callback() {
                    @Override
                    public void onResultDevice(BleDevice device) {
                        lowBlePrint(device);
                    }
                }
        );
    }

    /**
     * 测试霍尼韦尔标签打印
     */
    private void testHoneywellLabelPrint() {
        try {

            DZELabelCmd cmd = new DZELabelCmd();
            cmd.PRN_AddTextToLabel("测试哈哈哈", 100, 100);
            cmd.PRN_PrintLabel(1);

            PortControl portControl = PortManager.get().connectUSBPort(getApplicationContext(), "65040", null);
            portControl.write(cmd.getCmdResult());

            Thread.sleep(1000);

            //端口连接
            portControl.disconnect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试打印
     */
    private PrintParams generatorPrintData() {
        PrintParams printParams = new PrintParams(TicketSpec.SPEC_58);
        printParams.add("消费小票", 1, false, Align.CENTER);
        printParams.addRow(0, String.format("单号:%s", "AD123213212321231231223434343432"));
        printParams.addSplitLine(0, true);

        // 打印数字Code128（自动用C字符集）
        byte[] cmd1 = ESCBarcodeUtil.generateBarcode(
                ESCBarcodeUtil.BarcodeType.CODE128,
                "PSDH0002202506270001",  // 偶数长度数字
                50,
                2,
                ESCBarcodeUtil.TextPosition.BELOW,
                ESCBarcodeUtil.BarcodeCharset.DEFAULT
        );

        // 打印混合内容Code128（自动用B字符集）
        byte[] cmd2 = ESCBarcodeUtil.generateBarcode(
                ESCBarcodeUtil.BarcodeType.CODE128,
                "中文AB12cd",
                60,
                3,
                ESCBarcodeUtil.TextPosition.BOTH,
                ESCBarcodeUtil.BarcodeCharset.UTF8
        );

        // 打印混合内容EAN13
        byte[] cmd3 = ESCBarcodeUtil.generateBarcode(
                ESCBarcodeUtil.BarcodeType.EAN13,
                "6911111111111",
                60,
                3,
                ESCBarcodeUtil.TextPosition.BOTH,
                ESCBarcodeUtil.BarcodeCharset.DEFAULT
        );

        // 打印混合内容CODE93
        byte[] cmd4 = ESCBarcodeUtil.generateBarcode(
                ESCBarcodeUtil.BarcodeType.CODE93,
                "6911111111111",
                60,
                3,
                ESCBarcodeUtil.TextPosition.BOTH,
                ESCBarcodeUtil.BarcodeCharset.DEFAULT
        );

        // 打印混合内容CODE93
        byte[] cmd5 = ESCBarcodeUtil.generateBarcode(
                ESCBarcodeUtil.BarcodeType.CODABAR,
                "6911111111111",
                60,
                3,
                ESCBarcodeUtil.TextPosition.BOTH,
                ESCBarcodeUtil.BarcodeCharset.DEFAULT
        );

        printParams.add(cmd1);
        printParams.add(cmd2);
        printParams.add(cmd3);
        printParams.add(cmd4);
        printParams.add(cmd5);

        //第一种
//        printParams.addRow("名称", "数量", "单价", "小计");
//        printParams.addRow(EllipsizeMode.LINE, "青椒肉丝炒饭", "1", "0.99", "0.99");
//        printParams.addRow(EllipsizeMode.COLUMN_LINE, "牛排4人套餐", "1", "0.99", "0.99");
//        printParams.addRow(EllipsizeMode.NEXT_LINE, "青椒肉丝炒饭", "1", "0.99", "0.99");
//        printParams.addRow(EllipsizeMode.ELLIPSIS, "牛排4人套餐牛排4人套餐", "1", "0.99", "0.99");
//        printParams.addRow(EllipsizeMode.SINGLE_LINE, "牛排4人套餐牛排4人套餐", "1", "0.99", "0.99");
//        printParams.addRow(0,new ColumnItem("牛排4人套餐牛排4人套餐",false,EllipsizeMode.SINGLE_LINE),new ColumnItem("1"),new ColumnItem("0.99"),new ColumnItem("0.99"));
//        printParams.addSplitLine(0, true);
//        printParams.addNextRow();
//        printParams.add(PrintImageUtil.getPrintBitmapData(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_loading_logo), 200, 200));

        Log.d("testtt", printParams.toString());


        //第二种
//        int fontSize = 0;
//        float[] widthWeigh = new float[]{2F, 1, 1, 1};
//        printParams.addRow(
//                fontSize
//                , widthWeigh
//                , new ColumnItem("名称", Align.LEFT)
//                , new ColumnItem("数量", Align.LEFT)
//                , new ColumnItem("单价", Align.LEFT)
//                , new ColumnItem("合计", Align.RIGHT)
//        );
//
//        printParams.addRow(
//                fontSize
//                , widthWeigh
//                , new ColumnItem("土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝土豆肉丝", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
//                , new ColumnItem("10", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
//                , new ColumnItem("9990099900999009990099900", false, EllipsizeMode.ELLIPSIS, Align.LEFT)
//                , new ColumnItem("0.99", false, EllipsizeMode.COLUMN_LINE, Align.RIGHT)
//        );
//
//        printParams.addRow(
//                fontSize
//                , widthWeigh
//                , new ColumnItem("青椒土豆肉丝", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
//                , new ColumnItem("100", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
//                , new ColumnItem("0.99", false, EllipsizeMode.COLUMN_LINE, Align.LEFT)
//                , new ColumnItem("", false, EllipsizeMode.COLUMN_LINE, Align.RIGHT)
//        );

        return printParams;
    }

    /**
     * 经典蓝牙打印
     *
     * @param data 必填 打印数据
     * @return 是否打印成功
     */
    public void classicBlePrint(String linkTarget, String port, final PrintParams data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //连接打印机并打印
//                    PortControl printer = null;
//
//                    if (ObjectUtilKt.isNotEmpty(linkTarget) && ObjectUtilKt.isNotEmpty(port)) {
//                        printer = PortManager.get().connectNetPort(linkTarget, ObjectUtilKt.toInt(port));
//                    }else{
//                        printer = PortManager.get().connectUSBPort(getApplicationContext(), linkTarget,null);
////                        printer = PortManager.get().connectBTPort(linkTarget);
//                    }

                    //重置复位打印机
                    data.add(0, PrinterCmdUtil.resetPrinter());

                    //打印并换行
                    data.add(PrinterCmdUtil.printLineFeed());

                    //进纸切割
                    data.add(PrinterCmdUtil.feedPaperCutPartial());

                    //重置复位打印机
                    data.add(0, PrinterCmdUtil.resetPrinter());

                    //PrinterHelper.get().bluetoothPrint(linkTarget, data.toByteArray(), 1);
                    BleQueueSender.send(linkTarget, data.toByteArray(), 1);

                    //写入打印内容数据
//                    printer.write(printDataList);

                    //端口连接
//                    printer.disconnect();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 低功耗蓝牙打印
     */
    private void lowBlePrint(BleDevice device) {
        PrintParams params = generatorPrintData();
        //重置复位打印机
        params.add(0, PrinterCmdUtil.resetPrinter());

        //打印并换行
        params.add(PrinterCmdUtil.printLineFeed());

        //进纸切割
        params.add(PrinterCmdUtil.feedPaperCutPartial());

        //重置复位打印机
        params.add(0, PrinterCmdUtil.resetPrinter());

        BleQueueSender.send(device.getMac(), params.toByteArray(), 1);

//        BleManager.getInstance().connectAndWrite(device, params.toByteArray(), false, new ConnectAndWriteCallback() {
//            @Override
//            public void onWriteSuccess(BleDevice device, int current, int total, byte[] justWrite) {
//                if (current >= total) {
//                    BleManager.getInstance().disconnect(device);
//                    ToastUtil.showSuccess("打印完成");
//                }
//            }
//
//            @Override
//            public void onWriteFailure(BleDevice device, BleException exception) {
//                ToastUtil.showError(exception.getDescription());
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        PortManager.get().disconnectAll();
        BleManager.getInstance().disconnectAllDevice();
        super.onDestroy();
    }


}