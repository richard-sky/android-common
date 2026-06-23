package com.richard.library.basic.basic;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;
import java.util.Objects;

/**
 * @author: Richard
 * @createDate: 2025/11/11 15:32
 * @version: 1.0
 * @description: 实现Navigation to fragment功能的FragmentActivity基类
 */
public abstract class BasicNavigationActivity<B extends ViewDataBinding> extends BasicBindingActivity<B> implements FragmentController {

    private static final String TAG = "BasicNavigationActivity";
    private static final String KEY_CURRENT_FRAGMENT = "current_fragment";

    private String currentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initFragmentManager(savedInstanceState);
    }

    private void initFragmentManager(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // 处理异常退出恢复
            currentFragmentTag = savedInstanceState.getString(KEY_CURRENT_FRAGMENT);
            this.restoreFragments();
        } else {
            // 显示默认Fragment
            this.showDefaultFragment();
        }
    }

    /**
     * 还原fragment
     */
    private void restoreFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (!fragments.isEmpty()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            boolean showFlag = false;

            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (fragment != null) {
                    if (!showFlag && Objects.equals(fragment.getTag(), currentFragmentTag)) {
                        ft.show(fragment);
                        showFlag = true;
                    } else {
                        ft.hide(fragment);
                    }
                }
            }
            ft.commit();
        }
    }

    /**
     * 显示默认Fragment
     */
    private void showDefaultFragment() {
        IFragment defaultFragment = getDefaultFragment();
        if (defaultFragment != null) {
            this.navigateTo(defaultFragment);
        }
    }

    @Override
    public void navigateTo(Class<? extends IFragment> fragmentClass, Bundle args) {
        try {
            IFragment fragment = fragmentClass.newInstance();
            if (args != null && fragment instanceof Fragment f) {
                f.setArguments(args);
            }
            this.navigateTo(fragment);
        } catch (Exception e) {
            Log.e(TAG, "navigateTo: Failed to create fragment instance", e);
        }
    }

    @Override
    public void navigateTo(IFragment fragment) {
        try {
            String tag = fragment.getClass().getSimpleName();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // 设置动画
//            transaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);

            // 隐藏当前Fragment
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            // 检查目标Fragment是否已存在
            Fragment targetFragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (targetFragment != null) {
                transaction.show(targetFragment);
            } else {
                transaction.add(getContainerId(), (Fragment) fragment, tag);
                transaction.addToBackStack(tag);
            }

            transaction.commit();
            currentFragmentTag = tag;
        } catch (Exception e) {
            Log.e(TAG, "navigateTo Failed: ", e);
        }
    }

    @Override
    public void goBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
            this.updateCurrentFragmentTag();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void goHome() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        this.updateCurrentFragmentTag();
    }

    @Override
    public IFragment getCurrentFragment() {
        if (currentFragmentTag != null) {
            return (IFragment) getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CURRENT_FRAGMENT, currentFragmentTag);
    }

    @Override
    public void onBackPressed() {
        IFragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.onBackPressed()) {
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 更新当前显示的fragment标识
     */
    private void updateCurrentFragmentTag() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCount > 0) {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(backStackCount - 1);
            currentFragmentTag = entry.getName();
        }
    }

    /**
     * 获取fragment 容器id
     */
    protected abstract int getContainerId();

    /**
     * 获取默认fragment
     */
    protected abstract IFragment getDefaultFragment();
}
