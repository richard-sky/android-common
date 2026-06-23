package com.richard.library.bluetooth;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

/**
 * @author: Richard
 * @createDate: 2025/5/9 15:05
 * @version: 1.0
 * @description: 加载框
 */
class LoadingDialog {

    private final Context context;
    private Dialog dialog;
    private TextView tvMessage;

    public LoadingDialog(Context context) {
        this.context = context;
        this.init();
    }

    private void init() {
        dialog = new Dialog(context, R.style.loading_bluetooth_dialog);
        dialog.setContentView(R.layout.dialog_bluetooth_loading);
        tvMessage = dialog.findViewById(R.id.tv_message);
    }

    public void show() {
        this.show("请稍等");
    }

    public void show(String message) {
        tvMessage.setText(message);
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
