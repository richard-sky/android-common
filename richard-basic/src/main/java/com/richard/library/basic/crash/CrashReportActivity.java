package com.richard.library.basic.crash;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.library.basic.databinding.ActivityCrashReportBinding;
import com.richard.library.context.AppContext;

/**
 * <pre>
 * Description : Crash日志
 * Author : admin-richard
 * Date : 2022/3/17 14:15
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/3/17 14:15      admin-richard         new file.
 * </pre>
 */
public class CrashReportActivity extends BasicScaffoldActivity {

    private ActivityCrashReportBinding binding;

    private Throwable throwable;
    private String logContent;

    public static void start(Throwable throwable, String logContent) {
        Intent intent = new Intent(AppContext.get(), CrashReportActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("throwable", throwable);
        intent.putExtra("logContent", logContent);
        AppContext.get().startActivity(intent);
    }

    @Override
    public void initLayoutView() {
        binding = ActivityCrashReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void initData() {
        super.navigationbar.setVisibility(View.VISIBLE);
        super.navigationbar.setTitle("Crash日志");
        super.navigationbar.setTitleTextViewShow(true);
        super.navigationbar.setRightText("复制");
        super.navigationbar.setRightTextViewShow(true);

        throwable = (Throwable) getIntent().getSerializableExtra("throwable");
        logContent = getIntent().getStringExtra("logContent");

        this.updateLogUI();
    }

    @Override
    public void bindListener() {
        super.navigationbar.getRightTextView().setOnClickListener((v) -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", binding.tvLog.getText());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);

            getUIView().showMsg("复制成功");
        });
    }

    /**
     * 更新显示Crash日志内容
     */
    private void updateLogUI() {
        binding.tvPath.setText(String.format("日志保存位置：%s", CrashHandler.getInstance().getLogFile().getAbsolutePath()));
        binding.tvLog.setText(logContent);
    }
}
