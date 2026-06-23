package com.richard.dev.common.activity.printer;

import static com.richard.library.port.connect.enumerate.PortType.Ethernet;

import android.provider.Settings;

import com.richard.library.context.util.DeviceUtil;
import com.richard.library.context.util.ThreadUtil;
import com.richard.library.context.AppContext;
import com.richard.library.port.connect.enumerate.PortType;
import com.richard.library.printer.command.PrinterCmd;
import com.richard.library.printer.command.PrinterCmdUtil;
import com.richard.library.printer.command.gprinter.EscCommand;
import com.richard.library.printer.command.gprinter.LabelCommand;
import com.richard.library.printer.enumerate.Align;
import com.richard.library.printer.enumerate.TicketSpec;
import com.richard.library.printer.utils.PrintParams;

import java.util.Vector;

/**
 * @author: Richard
 * @createDate: 2023/10/21 11:01
 * @version: 1.0
 * @description: 打印机相关工具类
 */
public final class PrinterUtil {

    /**
     * 设置USB端口授权白名单
     */
    public static void setUsbAuthWhiteList() {
        ThreadUtil.getCachedPool().execute(() -> {
            try {
                //一敏设备
                //参考链接：https://oss-sg.imin.sg/docs/en/Permissions.html#if-you-want-auto-authorization-without-popping-up-the-usb-permission-dialog-imin-provides-the-following-two-solutions-to-solve-what-you-want
                if ("yimin".equalsIgnoreCase(DeviceUtil.getVendor())) {
                    //添加当前app为白名单
                    Settings.System.putString(AppContext.get().getContentResolver(), "imin_system_add", AppContext.getPackageName());
                    //移除当前app白名单
                    //Settings.System.putString(AppContext.get().getContentResolver() , "imin_system_remove" , AppContext.getPackageName());
                    //移除当前设备的所有白名单
                    //Settings.System.putString(AppContext.get().getContentResolver() , "imin_system_clear" , AppContext.getPackageName());
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 组合小票打印数据
     */
    public static PrintParams combinationTicketData(TicketSpec spec, PrintParams data) {
        data.add(PrintParams.getByte("\n   "));
        data.add(PrintParams.getByte("\n   "));

        //重置复位打印机
        data.add(0, PrinterCmdUtil.resetPrinter());

        //设置行距（注释掉,现使用打印机的默认行距）
//        data.add(1, PrinterCmd.setLineSpacing(70));

        //打印并换行
        data.add(PrinterCmdUtil.printLineFeed());

        //打印缓冲区中的数据并输入n行
        data.add(PrinterCmd.printFeedNLines(3));

        //发出提示音和灯
        data.add(PrinterCmd.printerOrderBuzzingAndWarningLight(4, 1, 1));

        //进纸切割
        data.add(PrinterCmdUtil.feedPaperCutPartial());

        return data;
    }

    /**
     * 获取标签测试打印数据
     *
     * @param width  标签宽
     * @param height 表填高
     */
    public static byte[] getLabelTestData(int width, int height) {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addSize(width, height);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(3);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 开启带Response的打印，用于连续打印
        tsc.addQueryPrinterStatus(LabelCommand.RESPONSE_MODE.ON);
        // 设置原点坐标
        tsc.addReference(0, 0);
        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4);
        // 撕纸模式开启
        tsc.addTear(EscCommand.ENABLE.ON);
        // 清除打印缓冲区
        tsc.addCls();

        tsc.addText(0, 10, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_2, LabelCommand.FONTMUL.MUL_2,
                "标签测试打印");

        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响
        tsc.addSound(2, 100);

        Vector<Byte> cmd = tsc.getCommand();
        byte[] result = new byte[cmd.size()];
        for (int i = 0; i < cmd.size(); i++) {
            result[i] = cmd.get(i);
        }
        return result;
    }

    /**
     * 获取票据打印测试数据
     *
     * @param ticketSpec 小票规格
     * @param portType   端口类型
     * @param linkTarget 连接目标
     * @param port       网口端口
     */
    public static byte[] getTicketTestData(TicketSpec ticketSpec, PortType portType, String linkTarget, String port) {
        PrintParams params = new PrintParams(ticketSpec);
        params.add("小票测试打印", 1, false, Align.CENTER);
        params.addNextRow();

        if (portType != null) {
            params.addRow("连接类型：" + portType);
        }

        params.addRow("指定小票规格：" + ticketSpec.name());
        params.addRow("连接目标：" + linkTarget);
        params.addNextRow();
        params.addNextRow();

        if (portType == Ethernet) {
            params.addRow("端口号：" + (port == null ? "无" : port));
        }

        PrinterUtil.combinationTicketData(ticketSpec, params);

        return params.toByteArray();
    }

}
