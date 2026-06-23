package com.richard.library.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.PermissionUtils;

/**
 * <pre>
 * Description : 权限处理Dialog
 * Author : admin-richard
 * Date : 2022/10/10 10:40
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/10/10 10:40      admin-richard         new file.
 * </pre>
 */
public class PermissionHandler extends AppCompatDialogFragment {

    private String[] permissions;
    private PEvent pEvent;
    private ShowRationale showRationale;
    private OnDenied onDenied;
    private OnNeverAskAgain onNeverAskAgain;

    private ActivityResultLauncher<String[]> launcher;


    public static void request(FragmentManager manager, String[] permissions, PEvent pEvent,
                               ShowRationale showRationale, OnDenied onDenied, OnNeverAskAgain onNeverAskAgain) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("permissions", permissions);
        PermissionHandler dialog = new PermissionHandler();
        dialog.setArguments(bundle);
        dialog.setPEvent(pEvent);
        dialog.setShowRationale(showRationale);
        dialog.setOnDenied(onDenied);
        dialog.setOnNeverAskAgain(onNeverAskAgain);
        dialog.show(manager, String.valueOf(System.currentTimeMillis()));
    }

    public void setPEvent(PEvent pEvent) {
        this.pEvent = pEvent;
    }

    public void setShowRationale(ShowRationale showRationale) {
        this.showRationale = showRationale;
    }

    public void setOnDenied(OnDenied onDenied) {
        this.onDenied = onDenied;
    }

    public void setOnNeverAskAgain(OnNeverAskAgain onNeverAskAgain) {
        this.onNeverAskAgain = onNeverAskAgain;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        if (manager.isDestroyed()) {
            return;
        }
        super.show(manager, tag);
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(true);
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0f;
            params.width = 1;
            params.height = 1;
            window.setAttributes(params);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), activityResultCallback);

        Bundle bundle = getArguments();
        if (bundle != null) {
            permissions = bundle.getStringArray("permissions");
            this.handleAndroid13();
            this.validatePermission();
        } else {
            dismiss();
        }
    }

    /**
     * 处理兼容适配android 13 权限
     */
    private void handleAndroid13() {
        if (permissions == null || permissions.length == 0) {
            return;
        }

        HashSet<Object> permissionSet = new HashSet<>(Arrays.asList(permissions));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissionSet.remove("android.permission.READ_MEDIA_IMAGES");
            permissionSet.remove("android.permission.READ_MEDIA_AUDIO");
            permissionSet.remove("android.permission.READ_MEDIA_VIDEO");
            permissions = permissionSet.toArray(new String[]{});
            return;
        }

        if (!permissionSet.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && !permissionSet.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }
        permissionSet.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionSet.remove(Manifest.permission.READ_EXTERNAL_STORAGE);

        permissionSet.add(Manifest.permission.READ_MEDIA_IMAGES);
        permissionSet.add(Manifest.permission.READ_MEDIA_AUDIO);
        permissionSet.add(Manifest.permission.READ_MEDIA_VIDEO);

        permissions = permissionSet.toArray(new String[]{});
    }


    private final ActivityResultCallback<Map<String, Boolean>> activityResultCallback = new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            int grantedNum = 0;
            List<String> grantedList = new ArrayList<>();
            List<String> deniedList = new ArrayList<>();
            for (String p : permissions) {
                if (Boolean.TRUE.equals(result.get(p))) {
                    grantedNum++;
                    grantedList.add(p);
                } else {
                    deniedList.add(p);
                }
            }

            if (grantedNum > 0) {
                if (pEvent != null) {
                    if (GrantedEvent.class.isAssignableFrom(pEvent.getClass())) {
                        pEvent.run();
                        ((GrantedEvent) pEvent).onGranted(grantedList, deniedList, grantedNum >= permissions.length);
                    } else if (grantedNum >= permissions.length) {
                        pEvent.run();
                    }
                }
                dismiss();
                return;
            }

            if (!PermissionUtils.shouldShowRequestPermissionRationale(PermissionHandler.this, permissions)) {
                //无法获得权限，需要去系统设置里手动授权（用户选择不再提示）
                if (onNeverAskAgain != null) {
                    onNeverAskAgain.onNeverAskAgain();
                } else {
                    Toast.makeText(getContext(), "请到系统设置中为该应用授予相关权限", Toast.LENGTH_SHORT).show();
                }
            } else {
                //当前用户拒绝了权限
                if (onDenied != null) {
                    onDenied.onDenied();
                }
            }
            dismiss();
        }
    };

    /**
     * 验证并申请权限
     */
    private void validatePermission() {
        if (PermissionUtils.hasSelfPermissions(getContext(), permissions)) {
            //执行对应事件
            if (pEvent != null) {
                pEvent.run();
            }
            dismiss();
            return;
        }
        if (showRationale != null && PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
            //提示用户该权限申请的原因
            showRationale.showRationale(new PermissionRequestImpl(this, launcher, onDenied, permissions));
        } else {
            launcher.launch(permissions);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (launcher != null) {
            launcher.unregister();
        }
        super.onDismiss(dialog);
    }

    private final static class PermissionRequestImpl implements PermissionRequest {

        private final WeakReference<PermissionHandler> weakTarget;
        private final String[] permissions;
        private final OnDenied onDenied;
        private final ActivityResultLauncher<String[]> launcher;


        public PermissionRequestImpl(PermissionHandler target, ActivityResultLauncher<String[]> launcher, OnDenied onDenied, String[] permissions) {
            this.weakTarget = new WeakReference<>(target);
            this.launcher = launcher;
            this.onDenied = onDenied;
            this.permissions = permissions;
        }

        @Override
        public void proceed() {
            PermissionHandler target = weakTarget.get();
            if (target == null) {
                return;
            }
            if (target.getActivity() == null || target.getActivity().isFinishing()) {
                target.dismiss();
                return;
            }
            launcher.launch(permissions);
        }

        @Override
        public void cancel() {
            PermissionHandler target = weakTarget.get();
            if (target == null) {
                return;
            }
            if (target.getActivity() == null || target.getActivity().isFinishing()) {
                target.dismiss();
                return;
            }
            //当前用户拒绝了权限
            if (onDenied != null) {
                onDenied.onDenied();
            }
            target.dismiss();
        }
    }
}
