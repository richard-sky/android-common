package com.richard.library.port.connect.helper;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.ArrayMap;
import android.widget.Toast;

import com.richard.library.port.connect.model.USBDeviceDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author：Richard
 * time：2021-06-18 13:53
 * version：v1.0.0
 * description：USB设备辅助类
 */
public final class USBHelper {

    //跨进程广播Action
    private static final String ACTION_CROSS_PROCESS_RECEIVER = "$action.cross.process.receiver.CrossProcessReceiver$";

    //USB权限Action
    private static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";

    //通知刷新usb设备列表
    public static final String NOTIFY_UPDATE_USB_DEVICE = "update_usb_device";


    private static USBHelper instance;
    private Context context;
    private UsbManager usbManager;
    private final List<USBDeviceDTO> deviceList = new ArrayList<>();
    private final ArrayMap<String, List<Runnable>> runMap = new ArrayMap<>();

    /**
     * 初始化USBPrinter
     */
    public void init(Context applicationContext) {
        this.context = applicationContext;
        usbManager = (UsbManager) applicationContext.getSystemService(Context.USB_SERVICE);
        this.registerUSBReceiver();
    }

    /**
     * 获取单例
     */
    public static USBHelper get() {
        if (instance == null) {
            synchronized (USBHelper.class) {
                if (instance == null) {
                    instance = new USBHelper();
                }
            }
        }

        return instance;
    }

    /**
     * 获取RunMapKey
     *
     * @param productId usb产品id
     * @param vendorId  供应商id
     */
    private String getRunMapKey(int productId, int vendorId) {
        return String.format("%s_%s", productId, vendorId);
    }

    /**
     * 添加待usb授权后执行体
     *
     * @param productId usb产品id
     * @param vendorId  供应商id(为0时不校验该参数)
     * @param runnable  执行体
     */
    public void addRun(int productId, int vendorId, Runnable runnable) {
        if (runnable == null) {
            return;
        }
        String key = this.getRunMapKey(productId, vendorId);
        List<Runnable> eventList = this.runMap.get(key);

        if (eventList == null) {
            eventList = new ArrayList<>();
            this.runMap.put(key, eventList);
        }

        eventList.add(runnable);
    }

    /**
     * 更新USB设备列表数据
     */
    public void updateDeviceList() {
        deviceList.clear();
        HashMap<String, UsbDevice> deviceMap = usbManager.getDeviceList();
        for (UsbDevice device : deviceMap.values()) {
            deviceList.add(new USBDeviceDTO(device));
        }
    }

    /**
     * 获取设备列表
     */
    public List<USBDeviceDTO> getDeviceList() {
        if (deviceList.size() <= 0) {
            this.updateDeviceList();
        }
        return deviceList;
    }

    /**
     * 获取usb设备DTO
     *
     * @param productId usb设备产品id
     * @param vendorId  usb设备供应商id(为0时不校验该参数)
     */
    public USBDeviceDTO getUsbDeviceDTO(int productId, int vendorId) {
        List<USBDeviceDTO> usbDeviceList = getDeviceList();
        for (USBDeviceDTO item : usbDeviceList) {
            if (item.getProductId() == productId
                    && (vendorId == 0 || item.getVendorId() == vendorId)) {
                return item;
            }
        }

        return null;
    }

    /**
     * 请求权限
     *
     * @param productId usb设备产品id
     * @param vendorId  usb设备供应商id(为0时不校验该参数)
     */
    public void requestPermission(int productId, int vendorId) {
        synchronized (this) {
            USBDeviceDTO usbDeviceDTO = getUsbDeviceDTO(productId, vendorId);
            if (usbDeviceDTO == null) {
                return;
            }

            if (usbManager.hasPermission(usbDeviceDTO.getUsbDevice())) {
                return;
            }
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent permissionIntent = PendingIntent.getBroadcast(context,
                    0, new Intent(ACTION_USB_PERMISSION), 0);
            USBHelper.get().usbManager.requestPermission(usbDeviceDTO.getUsbDevice(), permissionIntent);
        }
    }

    /**
     * 是否有USB权限
     *
     * @param productId usb设备产品id
     * @param vendorId  usb设备供应商id(为0时不校验该参数)
     */
    public boolean isHasPermission(int productId, int vendorId) {
        USBDeviceDTO usbDeviceDTO = getUsbDeviceDTO(productId, vendorId);
        if (usbDeviceDTO == null) {
            return false;
        }

        return usbManager.hasPermission(usbDeviceDTO.getUsbDevice());
    }

    /**
     * 销毁对象
     */
    public void destroy() {
        deviceList.clear();
        runMap.clear();
        usbManager = null;
        this.unregisterUSBReceiver();
    }

    //------------------------------------------------------------

    /**
     * 执行usb授权成功之后的执行事件
     */
    private void execute(Intent intent) {
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (usbDevice == null) {
            return;
        }

        String key = getRunMapKey(usbDevice.getProductId(), usbDevice.getVendorId());

        List<Runnable> eventList = USBHelper.get().runMap.get(key);
        if (eventList == null) {
            key = getRunMapKey(usbDevice.getProductId(), 0);
            eventList = USBHelper.get().runMap.get(key);
            if (eventList == null) {
                return;
            }
        }
        runMap.remove(key);

        try {
            List<Runnable> finalEventList = eventList;
            new Thread(() -> {
                for (Runnable event : finalEventList) {
                    event.run();
                }
            }).start();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 当usb设备拔掉或拒绝权限时执行
     */
    private void removeRunMap(Intent intent) {
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (usbDevice == null) {
            return;
        }

        String key = getRunMapKey(usbDevice.getProductId(), usbDevice.getVendorId());

        List<Runnable> eventList = runMap.remove(key);
        if (eventList == null) {
            key = getRunMapKey(usbDevice.getProductId(), 0);
            runMap.remove(key);
        }
    }

    //-------------------------USB插拔广播--------------------------
    private USBDeviceReceiver usbDeviceReceiver;

    /**
     * 注册usb广播
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerUSBReceiver() {
        this.unregisterUSBReceiver();
        usbDeviceReceiver = new USBDeviceReceiver(context);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(usbDeviceReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }else{
            context.registerReceiver(usbDeviceReceiver, filter);
        }
    }

    /**
     * 取消注册usb广播
     */
    private void unregisterUSBReceiver() {
        if (usbDeviceReceiver != null) {
            context.unregisterReceiver(usbDeviceReceiver);
            usbDeviceReceiver = null;
        }
    }

    /**
     * USB设备广播接收
     */
    private static class USBDeviceReceiver extends BroadcastReceiver {

        private final Context context;

        private USBDeviceReceiver(Context context) {
            this.context = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }

            switch (intent.getAction()) {
                case ACTION_USB_PERMISSION:
                    //请求USB权限
                    synchronized (this) {
                        boolean isGrantedPermission = intent.getBooleanExtra(
                                UsbManager.EXTRA_PERMISSION_GRANTED, false);
                        if (isGrantedPermission) {
                            USBHelper.get().updateDeviceList();
                            USBHelper.get().execute(intent);
                        } else {
                            USBHelper.get().removeRunMap(intent);
                            Toast.makeText(context, "设备权限被拒绝，将无法使用相关USB设备",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED://USB插入
                    USBHelper.get().updateDeviceList();
                    this.notifyUpdateUSBDeviceList();
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED://USB拔出
                    USBHelper.get().updateDeviceList();
                    this.notifyUpdateUSBDeviceList();
                    USBHelper.get().removeRunMap(intent);
                    break;
            }
        }


        /**
         * 通知刷新USB打印设备列表
         */
        private void notifyUpdateUSBDeviceList() {
            Intent intent = new Intent(ACTION_CROSS_PROCESS_RECEIVER);
            intent.putExtra("key_event_type", NOTIFY_UPDATE_USB_DEVICE);
            context.sendBroadcast(intent);
        }
    }
}
