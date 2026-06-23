package com.richard.dev.common.activity;

import android.Manifest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.richard.dev.common.databinding.ActivityPermissionBinding;
import com.richard.library.basic.basic.BasicScaffoldActivity;
import com.richard.library.basic.dialog.PromptDialog;
import com.richard.library.context.AppContext;
import com.richard.library.permission.PermissionRequester;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * @CreateDate: 2022/3/14 11:36
 * @Author: Administrator
 * @Version: 1.0
 * @Description: 描述
 */
@Route(path = "/test/permission")
@RuntimePermissions
public class TestPermissionActivity extends BasicScaffoldActivity {

    private ActivityPermissionBinding binding;

    @Override
    public void initLayoutView() {
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void initData() {
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void bindListener() {
        binding.bvGet1.setOnClickListener((v) -> {
            TestPermissionActivityPermissionsDispatcher.saveFileWithPermissionCheck(this, AppContext.get().getCacheDir());
        });

        binding.bvGet2.setOnClickListener((v) -> {
            PermissionRequester.with(this)
//                    .setPermission(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_VIDEO)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .showRationale(request -> new PromptDialog.Builder(getContext())
                            .setTitle("权限申请")
                            .setMessage("该操作需要存储权限，立即去申请?")
                            .setLeftBtn("取消", (dialogInterface, view) -> {
                                dialogInterface.dismiss();
                                request.cancel();
                            })
                            .setRightBtn("去申请", (dialogInterface, view) -> {
                                dialogInterface.dismiss();
                                request.proceed();
                            })
                            .create()
                            .show())
                    .onDenied(() -> getUIView().showMsg("您取消了授权"))
                    .onNeverAskAgain(() -> getUIView().showMsg("请到系统设置中为该应用授权"))
                    .request(() -> getUIView().showMsg("保存文件成功"));
        });
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void saveFile(File file) {
        getUIView().showMsg("保存文件成功");
    }

    /**
     * 选择了不再询问时回调
     */
    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onNeverGrant() {
        getUIView().showMsg("您已拒绝存储权限，请到系统设置中开启该权限");
    }

    /**
     * 拒绝了权限时回调
     */
    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onDenied() {
        getUIView().showMsg("您已拒绝存储权限，请到系统设置中开启该权限");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TestPermissionActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
