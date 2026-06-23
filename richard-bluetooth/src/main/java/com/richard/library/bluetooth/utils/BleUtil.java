package com.richard.library.bluetooth.utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.richard.library.bluetooth.core.data.BleDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: Richard
 * @createDate: 2024/12/25 11:45
 * @version: 1.0
 * @description: 蓝牙工具类
 */
public final class BleUtil {

    /**
     * 获取Gatt
     *
     * @param gatt         gatt
     * @param propertyType 属性类型，详见BluetoothGattCharacteristic类PROPERTY开头的常量
     */
    public static BluetoothGattCharacteristic getGattCharacteristic(BluetoothGatt gatt, int propertyType) {
        BluetoothGattCharacteristic gattCharacteristic = null;
        for (BluetoothGattService service : gatt.getServices()) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if ((characteristic.getProperties() & propertyType) > 0) {
                    gattCharacteristic = characteristic;
                    break;
                }
            }
            if (gattCharacteristic != null) {
                break;
            }
        }

        return gattCharacteristic;
    }

    /**
     * 加载已配对的蓝牙设备列表
     */
    public static List<BleDevice> getBondedDeviceList(Context context, BluetoothAdapter bluetoothAdapter) {
        List<BleDevice> data = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return data;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.isEmpty()) {
            return data;
        }

        List<BluetoothDevice> pairedDeviceList = new ArrayList<>(pairedDevices);
        for (BluetoothDevice item : pairedDeviceList) {
            data.add(new BleDevice(item));
        }

        return data;
    }

}
