package com.richard.library.printer.command;

/**
 * @author: Richard
 * @createDate: 2024/1/24 20:31
 * @version: 1.0
 * @description: 打印机状态获取指令
 */
public final class PrinterStateCmd {

    public static byte[] GetStatus() {
        try {
            byte[] var0;
            (var0 = new byte[12])[0] = 16;
            var0[1] = 4;
            var0[2] = 1;
            var0[3] = 16;
            var0[4] = 4;
            var0[5] = 2;
            var0[6] = 16;
            var0[7] = 4;
            var0[8] = 3;
            var0[9] = 16;
            var0[10] = 4;
            var0[11] = 4;
            return var0;
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static byte[] GetStatus1() {
        try {
            byte[] var0;
            (var0 = new byte[3])[0] = 16;
            var0[1] = 4;
            var0[2] = 1;
            return var0;
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static byte[] GetStatus2() {
        try {
            byte[] var0;
            (var0 = new byte[3])[0] = 16;
            var0[1] = 4;
            var0[2] = 2;
            return var0;
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static byte[] GetStatus3() {
        try {
            byte[] var0;
            (var0 = new byte[3])[0] = 16;
            var0[1] = 4;
            var0[2] = 3;
            return var0;
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static byte[] GetStatus4() {
        try {
            byte[] var0;
            (var0 = new byte[3])[0] = 16;
            var0[1] = 4;
            var0[2] = 4;
            return var0;
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static byte[] GetStatus5() {
        try {
            byte[] var0;
            (var0 = new byte[3])[0] = 16;
            var0[1] = 4;
            var0[2] = 5;
            return var0;
        } catch (Exception var1) {
            var1.printStackTrace();
            return null;
        }
    }

    public static int CheckStatus(byte[] var0) {
        if ((var0[0] & 22) != 22) {
            return 2;
        } else if ((var0[1] & 4) == 4) {
            return 3;
        } else if ((var0[2] & 8) == 8) {
            return 4;
        } else if ((var0[2] & 64) == 64) {
            return 5;
        } else if ((var0[2] & 32) == 32) {
            return 6;
        } else if ((var0[3] & 96) == 96) {
            return 7;
        } else {
            return (var0[3] & 12) == 12 ? 8 : 0;
        }
    }

    public static int CheckStatus1(byte var0) {
        return (var0 & 22) != 22 ? 2 : 0;
    }

    public static int CheckStatus2(byte var0) {
        return (var0 & 4) == 4 ? 3 : 0;
    }

    public static int CheckStatus3(byte var0) {
        if ((var0 & 8) == 8) {
            return 4;
        } else if ((var0 & 64) == 64) {
            return 5;
        } else {
            return (var0 & 32) == 32 ? 6 : 0;
        }
    }

    public static int CheckStatus4(byte var0) {
        if ((var0 & 96) == 96) {
            return 7;
        } else {
            return (var0 & 12) == 12 ? 8 : 0;
        }
    }

    public static int CheckStatus5(byte var0) {
        if ((var0 & 128) != 128) {
            return 4;
        } else if ((var0 & 1) != 1) {
            return 5;
        } else if ((var0 & 8) != 8) {
            return 6;
        } else if ((var0 & 2) != 2) {
            return 7;
        } else {
            return (var0 & 4) != 4 ? 8 : 0;
        }
    }

    public static int checkStatusEnd(byte var0) {
        return 0;
    }
}
