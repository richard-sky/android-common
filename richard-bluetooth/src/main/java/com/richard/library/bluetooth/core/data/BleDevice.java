package com.richard.library.bluetooth.core.data;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 蓝牙设备
 * 1.BluetoothDevice.getType()
 * BluetoothDevice.DEVICE_TYPE_CLASSIC = 1;//经典蓝牙
 * BluetoothDevice.DEVICE_TYPE_DUAL = 3;//经典蓝牙和低功耗蓝牙
 * BluetoothDevice.DEVICE_TYPE_LE = 2;//低功耗蓝牙
 * BluetoothDevice.DEVICE_TYPE_UNKNOWN = 0;//未知
 * <p>
 * 2.BluetoothDevice.getBondState()
 * BluetoothDevice.BOND_BONDED = 12;//已配对
 * BluetoothDevice.BOND_BONDING = 11;//配对中
 * BluetoothDevice.BOND_NONE = 10;//未配对
 */
@SuppressLint("MissingPermission")
public class BleDevice implements Parcelable {

    private BluetoothDevice device;
    private byte[] scanRecord;
    private int rssi;
    private long timestampNanos;

    public BleDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BleDevice(BluetoothDevice device, int rssi, byte[] scanRecord, long timestampNanos) {
        this.device = device;
        this.scanRecord = scanRecord;
        this.rssi = rssi;
        this.timestampNanos = timestampNanos;
    }

    protected BleDevice(Parcel in) {
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        scanRecord = in.createByteArray();
        rssi = in.readInt();
        timestampNanos = in.readLong();
    }

    public String getBondStateName() {
        if (device == null) {
            return "";
        }
        switch (device.getBondState()) {
            case BluetoothDevice.BOND_BONDED:
                return "已配对";
            case BluetoothDevice.BOND_BONDING:
                return "配对中";
            case BluetoothDevice.BOND_NONE:
                return "未配对";
            default:
                return "";
        }
    }

    public String getTypeName() {
        if (device == null) {
            return "";
        }
        switch (device.getType()) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "classic";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return "dual";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return "le";
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
            default:
                return "unknown";
        }
    }

    /**
     * 是否已经配对
     */
    public boolean isBonded(){
        return device != null && device.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
        dest.writeByteArray(scanRecord);
        dest.writeInt(rssi);
        dest.writeLong(timestampNanos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel in) {
            return new BleDevice(in);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };

    public String getName() {
        if (device != null) {
            return device.getName();
        }
        return null;
    }

    public String getMac() {
        if (device != null) {
            return device.getAddress();
        }
        return null;
    }

    public String getKey() {
        if (device != null) {
            return device.getName() + device.getAddress();
        }
        return "";
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public long getTimestampNanos() {
        return timestampNanos;
    }

    public void setTimestampNanos(long timestampNanos) {
        this.timestampNanos = timestampNanos;
    }

}
