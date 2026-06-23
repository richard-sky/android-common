package com.richard.library.bluetooth.core.callback;


import com.richard.library.bluetooth.core.exception.BleException;

public abstract class BleMtuChangedCallback extends BleBaseCallback {

    public abstract void onSetMTUFailure(BleException exception);

    public abstract void onMtuChanged(int mtu);

}
