//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.richard.library.printer.command.gprinter;

import com.jcraft.jzlib.ZOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@SuppressWarnings("all")
public abstract class ZLibUtils {
    public static void main(String[] args) {
    }

    public ZLibUtils() {
    }

    public static byte[] zLib(byte[] bContent) throws IOException {
        try {
            ByteArrayOutputStream e = new ByteArrayOutputStream();
            ZOutputStream zOut = new ZOutputStream(e, 9);
            DataOutputStream objOut = new DataOutputStream(zOut);
            objOut.write(bContent);
            objOut.flush();
            zOut.close();
            byte[] data1 = e.toByteArray();
            e.flush();
            e.close();
            return data1;
        } catch (IOException var6) {
            var6.printStackTrace();
            throw var6;
        }
    }

    public static byte[] zLib(byte[] bContent, int level) throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZOutputStream zOut = new ZOutputStream(out, level);
            DataOutputStream objOut = new DataOutputStream(zOut);
            objOut.write(bContent);
            objOut.flush();
            zOut.close();
            byte[] data = out.toByteArray();
            out.close();
            return data;
        } catch (IOException var6) {
            var6.printStackTrace();
            throw var6;
        }
    }
}
