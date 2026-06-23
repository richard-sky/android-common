package com.richard.dev.common.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityTestFtpBinding;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.context.util.JsonKt;
import com.richard.library.context.util.ThreadUtil;
import com.richard.library.net.ftp.FileTransferClient;
import com.richard.library.net.ftp.FileTransferFactory;

import org.apache.commons.net.ftp.FTPFile;

import java.util.List;

public class TestFTPActivity extends BasicBindingActivity<ActivityTestFtpBinding> {

    private FileTransferClient currentClient;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TestFTPActivity.class));
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_test_ftp);
    }

    @Override
    public void initData() {

    }

    @Override
    public void bindListener() {
        binding.btnFtp.setOnClickListener(v -> useFTP());
        binding.btnFtps.setOnClickListener(v -> useFTPS());
        binding.btnTftp.setOnClickListener(v -> useTFTP());
    }


    private void useFTP() {
        currentClient = FileTransferFactory.createFTPClient("ftp://ftp.dlptest.com/", "dlptest.com", "3D6XZV9MKdhM5fF");
        ThreadUtil.getCachedPool().submit(() -> {
            try {
                currentClient.connect();
                List<FTPFile> files = currentClient.listFiles("/");
                Log.w("testtt", JsonKt.toJson(files));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void useFTPS() {

    }

    private void useTFTP() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentClient != null) {
            currentClient.disconnect();
        }
    }
}
