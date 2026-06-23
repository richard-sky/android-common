/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.richard.library.port.connect.serialport;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 串口查找器
 */
public class SerialPortFinder {

    private static final String TAG = "SerialPort";
    private Vector<Driver> drivers = null;

    /**
     * 获取全部串口设备列表
     */
    public List<String> getAllDevices() {
        List<String> devices = new ArrayList<>();
        // Parse each driver
        Iterator<Driver> driverIterator;
        try {
            driverIterator = getDrivers().iterator();
            while (driverIterator.hasNext()) {
                Driver driver = driverIterator.next();
                for (File file : driver.getDevices()) {
                    String device = file.getName();
                    String value = String.format("%s (%s)", device, driver.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices;
    }

    /**
     * 获取全部串口地址列表
     */
    public List<String> getAllDevicesPath() {
        List<String> devices = new ArrayList<>();
        // Parse each driver
        Iterator<Driver> driverIterator;
        try {
            driverIterator = getDrivers().iterator();
            while (driverIterator.hasNext()) {
                Driver driver = driverIterator.next();
                for (File file : driver.getDevices()) {
                    String device = file.getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices;
    }


    /**
     * 获取驱动列表
     */
    public Vector<Driver> getDrivers() throws IOException {
        if (drivers == null) {
            drivers = new Vector<>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String l;
            while ((l = r.readLine()) != null) {
                // Issue 3:
                // Since driver name may contain spaces, we do not extract driver name with split()
                String drivername = l.substring(0, 0x15).trim();
                String[] w = l.split(" +");
                if ((w.length >= 5) && (w[w.length - 1].equals("serial"))) {
                    Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length - 4]);
                    drivers.add(new Driver(drivername, w[w.length - 4]));
                }
            }
            r.close();
        }
        return drivers;
    }

    /**
     * 驱动信息
     */
    public static class Driver {

        private Vector<File> devices = null;
        private final String driverName;
        private final String deviceRoot;

        public Driver(String name, String root) {
            driverName = name;
            deviceRoot = root;
        }

        /**
         * 获取设备列表
         */
        public Vector<File> getDevices() {
            if (devices == null) {
                devices = new Vector<File>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();

                if (files != null) {
                    int i;
                    for (i = 0; i < files.length; i++) {
                        if (files[i].getAbsolutePath().startsWith(deviceRoot)) {
                            Log.d(TAG, "Found new device: " + files[i]);
                            devices.add(files[i]);
                        }
                    }
                }
            }
            return devices;
        }

        /**
         * 获取设备名称
         */
        public String getName() {
            return driverName;
        }
    }
}
