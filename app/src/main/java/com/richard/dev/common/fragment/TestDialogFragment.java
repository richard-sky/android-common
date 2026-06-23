package com.richard.dev.common.fragment;

import android.content.Context;
import android.graphics.Color;

import androidx.fragment.app.FragmentManager;

import com.richard.dev.common.R;
import com.richard.library.basic.basic.BasicScaffoldDialogFragment;

/**
 * @ProjectName: App开发通用库
 * @Package: com.richard.dev.common
 * @ClassName: TestDialogFragment
 * @CreateDate: 2022/3/10 10:48
 * @Author: Richard
 * @Version: 1.0
 * @Description: 描述
 */
public class TestDialogFragment extends BasicScaffoldDialogFragment {

    public static void start(FragmentManager manager) {
        TestDialogFragment fragment = new TestDialogFragment();
        fragment.showSingle(manager);
    }

    @Override
    public void initLayoutView() {
        setContentView(R.layout.dialog_test);
    }

    @Override
    public void initData() {
        super.navigationbar.notifyUpdateRadius(Color.RED);
        //super.setSize(200, 200);

//        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                getUIView().showMsg("eeeeeee");
//            }
//        });
    }

    @Override
    public void bindListener() {
        Context context = getContext();
    }
}
