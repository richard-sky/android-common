package com.richard.library.basic.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.richard.library.context.AppContext;

import org.greenrobot.eventbus.EventBus;


/**
 * <pre>
 * Description : 用于EventBus不能跨进程通信的问题，该方式是通过广播来间接发送EventBus消息
 * Author : admin-richard
 * Date : 2019-06-11 17:40
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-11 17:40      admin-richard         new file.
 * </pre>
 */
public class CrossProcessReceiver extends BroadcastReceiver {

    //该广播注册Action
    public static final String ACTION_CROSS_PROCESS_RECEIVER = "$action.cross.process.receiver.CrossProcessReceiver$";
    //event 数据key
    public static final String KEY_EVENT_DATA = "key_event_data";
    //event type
    public static final String KEY_EVENT_TYPE = "key_event_type";

    /**
     * 初始化广播
     */
    public static void init(Context applicationContext) {
        CrossProcessReceiver receiver = new CrossProcessReceiver();
        IntentFilter filter = new IntentFilter(ACTION_CROSS_PROCESS_RECEIVER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applicationContext.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }else{
            applicationContext.registerReceiver(receiver, filter);
        }
    }

    /**
     * 发送跨进程广播通知
     */
    public static void sendCrossProcessReceiverMessage(String eventBusType) {
        sendCrossProcessReceiverMessage(new EventData(eventBusType));
    }

    /**
     * 发送跨进程广播通知
     */
    @SuppressWarnings("all")
    public static void sendCrossProcessReceiverMessage(EventData eventData) {
        if (eventData == null) {
            return;
        }

        Intent intent = new Intent(ACTION_CROSS_PROCESS_RECEIVER);
        intent.putExtra(KEY_EVENT_DATA, eventData);
        AppContext.get().sendBroadcast(intent);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }

        if (!ACTION_CROSS_PROCESS_RECEIVER.equals(intent.getAction())) {
            return;
        }

        String keyEventType;
        EventData eventData = (EventData) intent.getSerializableExtra(KEY_EVENT_DATA);
        if (eventData == null) {
            if ((keyEventType = intent.getStringExtra(KEY_EVENT_TYPE)) == null) {
                return;
            }
            eventData = new EventData<>(keyEventType, intent.getExtras());
        }

        EventBus.getDefault().post(eventData);
    }
}
