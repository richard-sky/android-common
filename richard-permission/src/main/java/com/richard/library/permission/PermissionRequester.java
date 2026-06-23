package com.richard.library.permission;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import permissions.dispatcher.PermissionUtils;

/**
 * <pre>
 * Description : 权限请求
 * Author : admin-richard
 * Date : 2022/10/10 13:43
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/10/10 13:43      admin-richard         new file.
 * </pre>
 */
public class PermissionRequester {

    public static Builder with(FragmentManager manager) {
        return new Builder(null, manager);
    }

    public static Builder with(Fragment fragment) {
        return new Builder(fragment.getContext(), fragment.getChildFragmentManager());
    }

    public static Builder with(AppCompatActivity activity) {
        return new Builder(activity, activity.getSupportFragmentManager());
    }

    private void request(FragmentManager manager, String[] permissions, PEvent pEvent,
                         ShowRationale showRationale, OnDenied onDenied, OnNeverAskAgain onNeverAskAgain) {
        PermissionHandler.request(manager, permissions, pEvent, showRationale, onDenied, onNeverAskAgain);
    }

    public static class Builder {
        private Context context;
        private final FragmentManager manager;
        private String[] permissions;
        private ShowRationale showRationale;
        private OnDenied onDenied;
        private OnNeverAskAgain onNeverAskAgain;

        public Builder(Context context, FragmentManager manager) {
            this.context = context;
            this.manager = manager;
        }

        public Builder permission(String... permission) {
            this.permissions = permission;
            return this;
        }

        public Builder showRationale(ShowRationale showRationale) {
            this.showRationale = showRationale;
            return this;
        }

        public Builder onDenied(OnDenied onDenied) {
            this.onDenied = onDenied;
            return this;
        }

        public Builder onNeverAskAgain(OnNeverAskAgain onNeverAskAgain) {
            this.onNeverAskAgain = onNeverAskAgain;
            return this;
        }

        public void request() {
            this.request(null);
        }

        public void request(GrantedEvent event) {
            this.request((PEvent) event);
        }

        public void request(PEvent pEvent) {
            if (context == null) {
                List<Fragment> fragments = manager.getFragments();
                if (!fragments.isEmpty()) {
                    context = fragments.get(0).getContext();
                }
            }

            if (context != null) {
                if (PermissionUtils.hasSelfPermissions(context, permissions)) {
                    if (pEvent != null) {
                        if (GrantedEvent.class.isAssignableFrom(pEvent.getClass())) {
                            pEvent.run();
                            ((GrantedEvent) pEvent).onGranted(
                                    Arrays.asList(permissions),
                                    new ArrayList<>(),
                                    true
                            );
                        } else {
                            pEvent.run();
                        }

                    }
                    return;
                }
            }

            new PermissionRequester().request(manager, permissions, pEvent, showRationale, onDenied, onNeverAskAgain);
        }
    }

}
