package com.richard.library.bluetooth.core.callback;

import com.richard.library.bluetooth.core.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
