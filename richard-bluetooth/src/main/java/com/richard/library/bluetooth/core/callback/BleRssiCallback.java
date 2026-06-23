package com.richard.library.bluetooth.core.callback;


import com.richard.library.bluetooth.core.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback{

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}