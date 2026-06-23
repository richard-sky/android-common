package com.richard.library.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.richard.library.bluetooth.adapter.BleDeviceAdapter;
import com.richard.library.bluetooth.core.BleManager;
import com.richard.library.bluetooth.core.callback.BleScanCallback;
import com.richard.library.bluetooth.core.data.BleDevice;
import com.richard.library.bluetooth.databinding.DialogSelectBleBinding;
import com.richard.library.bluetooth.utils.BleUtil;

import java.util.List;

/**
 * <pre>
 * Description : 蓝牙设备选择
 * Author : Richard
 * Date : 2023/4/16 07:56
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2023/4/16 07:56      xiejiao         new file.
 * </pre>
 */
public class SelectBleDialogV2 extends AppCompatDialogFragment {

    private DialogSelectBleBinding binding;
    private BleDeviceAdapter adapter;
    private boolean isScanning = false;//是否正在扫描
    private Callback callback;
    private boolean isExcludeUnknown;//是否排除未知蓝牙设备

    /**
     * 打开蓝牙设备选择框
     *
     * @param manager          FragmentManager
     * @param isExcludeUnknown 是否排除未知蓝牙设备
     * @param callback         回调
     */
    public static void start(FragmentManager manager, boolean isExcludeUnknown, Callback callback) {
        SelectBleDialogV2 dialog = new SelectBleDialogV2();
        dialog.isExcludeUnknown = isExcludeUnknown;
        dialog.setCallback(callback);
        dialog.show(manager, String.valueOf(System.currentTimeMillis()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogSelectBleBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initData();
        this.bindListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                params.width = (int) (getScreenWidth() * 0.95);
                params.height = (int) (getScreenHeight() * 0.8);
            } else {
                params.width = this.dp2px(320);
                params.height = (int) (getScreenHeight() * 0.8);
            }

            window.setAttributes(params);
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        binding.rvView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new BleDeviceAdapter();
        binding.rvView.setAdapter(adapter);
        this.setDefaultAnimatorOpen(false);

        if (!BleManager.getInstance().isSupportBle()) {
            this.updateStateUI(false, "该设备不支持蓝牙");
        } else if (!BleManager.getInstance().isBlueEnable()) {
            this.openBluetooth();
        } else {
            this.scanBluetooth();
        }
    }

    /**
     * 设置是否开启默认动画
     */
    public void setDefaultAnimatorOpen(boolean isOpen) {
        RecyclerView.ItemAnimator itemAnimator = binding.rvView.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setAddDuration(isOpen ? 120 : 0);
            itemAnimator.setChangeDuration(isOpen ? 250 : 0);
            itemAnimator.setMoveDuration(isOpen ? 250 : 0);
            itemAnimator.setRemoveDuration(isOpen ? 120 : 0);
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(isOpen);
        }
    }

    /**
     * 事件监听绑定
     */
    private void bindListener() {
        binding.ivClose.setOnClickListener((v) -> {
            dismiss();
        });

        binding.btnMatch.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            requestToMatchLauncher.launch(intent);
        });

        binding.btnScan.setOnClickListener(v -> {
            if (isScanning) {
                this.stopScan();
            } else {
                this.scanBluetooth();
            }
        });

        adapter.setCallback(device -> {
            dismiss();
            if (callback != null) {
                callback.onResultDevice(device);
            }
        });
    }

    @Override
    public void onDestroy() {
        this.stopScan();
        enableBluetooth.unregister();
        requestLocation.unregister();
        requestBluetoothConnectByScan.unregister();
        requestBluetoothConnectByOpen.unregister();
        requestBluetoothScan.unregister();
        requestToMatchLauncher.unregister();
        super.onDestroy();
    }

    /**
     * 打开蓝牙
     */
    private void openBluetooth() {
        //是Android12
        if (isAndroid12Above()) {
            //检查是否有BLUETOOTH_CONNECT权限
            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                //打开蓝牙
                enableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            } else {
                //请求权限
                requestBluetoothConnectByOpen.launch(Manifest.permission.BLUETOOTH_CONNECT);
            }
            return;
        }

        //不是Android12 直接打开蓝牙
        enableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
    }

    /**
     * 扫描蓝牙设备
     */
    private void scanBluetooth() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startScan();
            return;
        }

        //是Android12
        //检查是否有BLUETOOTH_CONNECT权限
        if (isAndroid12Above() && !hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            requestBluetoothConnectByScan.launch(Manifest.permission.BLUETOOTH_CONNECT);
            return;
        }

        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        if (isAndroid12Above() && !hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            requestBluetoothScan.launch(Manifest.permission.BLUETOOTH_SCAN);
            return;
        }
        startScan();
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        if (isScanning) {
            return;
        }
        if (isOpenBluetooth()) {
            adapter.clear();
            adapter.update(BleUtil.getBondedDeviceList(getContext(), BleManager.getInstance().getBluetoothAdapter()));
            BleManager.getInstance().scan(bleScanCallback);
        }
        isScanning = true;
        binding.btnScan.setText("停止扫描");
        this.updateStateUI(true, "蓝牙扫描中");
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        if (!isScanning) {
            return;
        }
        if (isOpenBluetooth()) {
            BleManager.getInstance().cancelScan();
        }
        isScanning = false;
        binding.btnScan.setText("开始扫描");
        binding.contentLoading.setVisibility(View.GONE);
    }

    /**
     * 蓝牙扫描回调
     */
    private final BleScanCallback bleScanCallback = new BleScanCallback() {
        @Override
        public void onScanStarted(boolean success) {
            updateStateUI(success, success ? "蓝牙扫描中" : "开启蓝牙扫描失败");
            if (!success) {
                stopScan();
            }
        }

        @Override
        public void onLeScan(BleDevice bleDevice) {
        }

        @Override
        @SuppressWarnings("all")
        public void onScanning(BleDevice bleDevice) {
            if (isExcludeUnknown) {
                String name = bleDevice.getDevice().getAlias();
                if (TextUtils.isEmpty(name)) {
                    name = bleDevice.getName();
                }
                if (TextUtils.isEmpty(name)) {
                    return;
                }
            }

            adapter.update(bleDevice);
        }

        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            updateStateUI(false, "扫描完成");
            stopScan();
        }
    };

    /**
     * 更新显示状态updateStateUI
     */
    private void updateStateUI(boolean isShowProgress, String msg) {
        binding.contentLoading.setVisibility(View.VISIBLE);
        binding.pbView.setVisibility(isShowProgress ? View.VISIBLE : View.GONE);
        binding.tvState.setText(msg);
    }

    /**
     * 验证蓝牙是否已经开启
     */
    private boolean isOpenBluetooth() {
        return BleManager.getInstance().isSupportBle() && BleManager.getInstance().isBlueEnable();
    }

    /**
     * 验证是否拥有指定权限
     */
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获得屏幕高度
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     */
    private int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 验证是否是android 12以上的版本
     */
    private boolean isAndroid12Above() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    private void showMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    //打开蓝牙意图
    private final ActivityResultLauncher<Intent> enableBluetooth = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        switch (result.getResultCode()) {
            case Activity.RESULT_OK:
                if (isOpenBluetooth()) {
                    scanBluetooth();
                } else {
                    showMsg("蓝牙未打开");
                    dismiss();
                }
                break;
            case Activity.RESULT_CANCELED:
                dismiss();
                break;
        }
    });

    //请求定位权限意图
    private final ActivityResultLauncher<String> requestLocation = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            //扫描蓝牙
            scanBluetooth();
        } else {
            showMsg("当前Android系统需要定位权限才能扫描设备");
        }
    });

    //请求BLUETOOTH_CONNECT权限意图
    private final ActivityResultLauncher<String> requestBluetoothConnectByScan = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            //扫描蓝牙
            scanBluetooth();
        } else {
            showMsg("蓝牙连接权限未授权，无法打开蓝牙");
        }
    });

    //请求BLUETOOTH_CONNECT权限意图
    private final ActivityResultLauncher<String> requestBluetoothConnectByOpen = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            //打开蓝牙
            enableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        } else {
            showMsg("蓝牙连接权限未授权，无法打开蓝牙");
        }
    });


    //请求BLUETOOTH_SCAN权限意图
    private final ActivityResultLauncher<String> requestBluetoothScan = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            //进行扫描
            scanBluetooth();
        } else {
            showMsg("扫描蓝牙权限未授权，无法扫描蓝牙");
        }
    });

    //配对回调
    private final ActivityResultLauncher<Intent> requestToMatchLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                    , result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            scanBluetooth();
                        }
                    });

    /**
     * dp转px
     *
     * @param dpVal dp值
     */
    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void onResultDevice(BleDevice device);

    }
}
