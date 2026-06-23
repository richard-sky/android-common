package com.richard.dev.common.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.alibaba.android.arouter.launcher.ARouter;
import com.richard.dev.common.R;
import com.richard.dev.common.UserJavaScriptMethod;
import com.richard.dev.common.databinding.ActivityMainBinding;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.basic.dto.ItemDTO;
import com.richard.library.basic.web.WebDialog;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BasicBindingActivity<ActivityMainBinding> {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
        navigationbar.setTitle("公共库测试");
        navigationbar.setTitleTextViewShow(true);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResultCallback);
    }

    @Override
    public void bindListener() {
        //ARouter路由库测试
        binding.btnTestArouter.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/second")
                    .withString("name", "Arouter 路由框架")
                    .withSerializable("obj", new ItemDTO<String>("Arou", "22"))
                    .navigation();
        });

        //动态权限获取测试
        binding.btnPermission.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/permission")
                    .navigation();
        });

        //Test MVP
        binding.btnTestMvp.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/mvp")
                    .navigation();
        });

        //Test request
        binding.btnTestRequest.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/request")
                    .navigation();
        });

        //Test mvvm
        binding.btnTestMvvm.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/mvvm")
                    .navigation();
        });

        //Test activity result
        binding.btnTestResult.setOnClickListener((v) -> {
            activityResultLauncher.launch(new Intent(getContext(), TestResultActivity.class));
        });

        //Test pinned
        binding.btnTestPinned.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/pinned")
                    .navigation();
        });

        //Test javascript
        binding.btnTestJs.setOnClickListener((v) -> {
//            WebActivity.start(getContext(), null, "file:///android_asset/index.html", new UserJavaScript());
            WebDialog.start(
                    getSupportFragmentManager()
                    , "js调用java"
                    , "file:///android_asset/index.html"
                    , new UserJavaScriptMethod()
            );
        });

        //Test adapter binding
        binding.btnTestAdapterBinding.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/adapterBinding")
                    .navigation();
        });

        //Test activity binding
        binding.btnTestActivityBinding.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/bindingActivity")
                    .navigation();
        });

        //Test activity binding
        binding.btnSlide.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/slide")
                    .navigation();
        });

        //FFMpeg
        binding.btnFfmpeg.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/ffmpeg")
                    .navigation();
        });

        //系统MediaCodec硬编解码
        binding.btnHardCoding.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/hard/coding")
                    .navigation();
        });

        //小票/标签打印
        binding.btnPrinter.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/printer")
                    .navigation();
        });

        //本地相册选择
        binding.btnPictureSelector.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/picture/selector")
                    .navigation();
        });

        //树形列表
        binding.btnTestTree.setOnClickListener(v -> {
            TestTreeActivity.start(getContext());
        });

        //cron定时任务
        binding.btnTestCron.setOnClickListener(v -> {
            Cron4jActivity.start(getContext());
        });

        //Test FTP
        binding.btnTestFtp.setOnClickListener((v) -> {
            TestFTPActivity.start(getContext());
        });

        //Test
        binding.btnTest.setOnClickListener((v) -> {
            ARouter.getInstance()
                    .build("/test/test")
                    .navigation();
        });
    }

    @Override
    protected void onDestroy() {
        activityResultLauncher.unregister();
        super.onDestroy();
    }

    private final ActivityResultCallback<ActivityResult> activityResultCallback = new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() == null) {
                return;
            }
            getUIView().showMsg(result.getData().getStringExtra("result"));
        }
    };
}