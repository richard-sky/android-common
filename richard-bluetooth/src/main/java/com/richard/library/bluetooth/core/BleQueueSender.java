package com.richard.library.bluetooth.core;

import com.richard.library.bluetooth.core.callback.ConnectAndWriteCallback;
import com.richard.library.bluetooth.core.data.BleDevice;
import com.richard.library.bluetooth.core.exception.BleException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: Richard
 * @createDate: 2025/3/31 15:31
 * @version: 1.0
 * @description: 蓝牙队列数据发送
 */
public final class BleQueueSender {

    private final static LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<>(50);
    private final static AtomicBoolean isRunning = new AtomicBoolean(false);
    private static Callback callback;

    /**
     * 设置回调
     */
    public static void setCallback(Callback callback) {
        BleQueueSender.callback = callback;
    }

    /**
     * 发送蓝牙数据(队列满时，不会阻塞线程，但不会进入队列)
     *
     * @param bluetoothId 蓝牙id
     * @param data        数据
     * @param sendNum     需发送的次数
     */
    public static boolean send(String bluetoothId, byte[] data, int sendNum) {
        boolean isOffer = queue.offer(new Task(bluetoothId, data, sendNum));
        if (isRunning.get()) {
            return isOffer;
        }
        startSend(queue.poll());
        return isOffer;
    }

    /**
     * 发送蓝牙数据(队列满时会阻塞线程，直至队列有空位)
     *
     * @param bluetoothId 蓝牙id
     * @param data        数据
     * @param sendNum     需发送的次数
     */
    public static void sendBlock(String bluetoothId, byte[] data, int sendNum) throws InterruptedException {
        queue.put(new Task(bluetoothId, data, sendNum));
        if (isRunning.get()) {
            return;
        }
        startSend(queue.poll());
    }

    private static void startSend(Task task) {
        if (task == null) {
            return;
        }

        isRunning.set(true);

        //低功耗蓝牙方式
        BleManager.getInstance().connectAndWrite(task.bluetoothId, task.data, false, new ConnectAndWriteCallback() {
            @Override
            public void onWriteSuccess(BleDevice device, int current, int total, byte[] justWrite) {
                if (current < total) {
                    return;
                }

                if (task.sendNum - 1 <= 0) {
                    if (callback != null) {
                        try {
                            callback.onSendSuccess(task.bluetoothId);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                    sendNext(device);
                } else {
                    task.sendNum = task.sendNum - 1;
                    startSend(task);
                }
            }

            @Override
            public void onWriteFailure(BleDevice device, BleException exception) {
                if (callback != null) {
                    try {
                        callback.onSendFail(task.bluetoothId, task.data, task.sendNum, exception.getDescription());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                sendNext(device);
            }
        });
    }

    /**
     * 发送下一个
     */
    private static void sendNext(BleDevice device) {
        Task t = queue.poll();
        if (t == null) {
            isRunning.set(false);
            BleManager.getInstance().disconnect(device);
        } else {
            startSend(t);
        }
    }

    private static class Task {
        String bluetoothId;
        byte[] data;
        int sendNum;

        public Task(String bluetoothId, byte[] data, int sendNum) {
            this.bluetoothId = bluetoothId;
            this.data = data;
            this.sendNum = sendNum;
        }
    }

    public interface Callback {

        default void onSendSuccess(String bluetoothId) {

        }

        void onSendFail(String bluetoothId, byte[] data, int sendNum, String message);
    }
}
