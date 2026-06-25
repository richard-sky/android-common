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
import com.richard.dev.common.databinding.ItemMainFuncBinding;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.basic.basic.adapter.BasicBindingAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.basic.dto.ItemDTO;
import com.richard.library.basic.web.WebDialog;
import com.richard.library.context.AppContext;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BasicBindingActivity<ActivityMainBinding> {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ListAdapter adapter;


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

        if(AppContext.isScreenPortrait()){
            binding.srvView.setColumn(2);
            binding.srvView.notifyAttrChanged();
        }else {
            binding.srvView.setColumn(6);
            binding.srvView.notifyAttrChanged();
        }

        adapter = new ListAdapter();
        binding.srvView.setAdapter(adapter);
        adapter.completeLoad(this.getData());
    }

    @Override
    public void bindListener() {
        adapter.setOnItemClickListener((itemInfo, position) -> {
            switch (itemInfo.getData()) {
                case "test":
                    ARouter.getInstance()
                            .build("/test/test")
                            .navigation();
                    break;
                case "ftp":
                    TestFTPActivity.start(getContext());
                    break;
                case "cron":
                    Cron4jActivity.start(getContext());
                    break;
                case "tree_list":
                    TestTreeActivity.start(getContext());
                    break;
                case "arouter":
                    ARouter.getInstance()
                            .build("/test/second")
                            .withString("name", "Arouter 路由框架")
                            .withSerializable("obj", new ItemDTO<String>("Arou", "22"))
                            .navigation();
                    break;
                case "permission":
                    ARouter.getInstance()
                            .build("/test/permission")
                            .navigation();
                    break;
                case "mvp":
                    ARouter.getInstance()
                            .build("/test/mvp")
                            .navigation();
                    break;
                case "request":
                    ARouter.getInstance()
                            .build("/test/request")
                            .navigation();
                    break;
                case "mvvm":
                    ARouter.getInstance()
                            .build("/test/mvvm")
                            .navigation();
                    break;
                case "activity_result":
                    activityResultLauncher.launch(new Intent(getContext(), TestResultActivity.class));
                    break;
                case "pinned_list":
                    ARouter.getInstance()
                            .build("/test/pinned")
                            .navigation();
                    break;
                case "invoke_js":
//            WebActivity.start(getContext(), null, "file:///android_asset/index.html", new UserJavaScript());
                    WebDialog.start(
                            getSupportFragmentManager()
                            , "js调用java"
                            , "file:///android_asset/index.html"
                            , new UserJavaScriptMethod()
                    );
                    break;
                case "adapter_binding":
                    ARouter.getInstance()
                            .build("/test/adapterBinding")
                            .navigation();
                    break;
                case "activity_binding":
                    ARouter.getInstance()
                            .build("/test/bindingActivity")
                            .navigation();
                    break;
                case "slide_recyclerview":
                    ARouter.getInstance()
                            .build("/test/slide")
                            .navigation();
                    break;
                case "ffmpeg":
                    ARouter.getInstance()
                            .build("/test/ffmpeg")
                            .navigation();
                    break;
                case "system_media_code":
                    ARouter.getInstance()
                            .build("/test/hard/coding")
                            .navigation();
                    break;
                case "printer":
                    ARouter.getInstance()
                            .build("/test/printer")
                            .navigation();
                    break;
                case "media_selector":
                    ARouter.getInstance()
                            .build("/test/picture/selector")
                            .navigation();
                    break;
            }
        });
    }

    /**
     * 功能列表数据
     */
    private List<ItemDTO<String>> getData() {
        List<ItemDTO<String>> data = new ArrayList<>();
        data.add(new ItemDTO<>("测试", "test", true));
        data.add(new ItemDTO<>("测试FTP", "ftp", true));
        data.add(new ItemDTO<>("Cron定时任务", "cron", true));
        data.add(new ItemDTO<>("树形列表", "tree_list", true));
        data.add(new ItemDTO<>("Arouter路由", "arouter", true));
        data.add(new ItemDTO<>("Permission", "permission", true));
        data.add(new ItemDTO<>("MVP架构示例", "mvp", true));
        data.add(new ItemDTO<>("网络请求", "request", true));
        data.add(new ItemDTO<>("MVVM架构示例", "mvvm", true));
        data.add(new ItemDTO<>("Activity Result", "activity_result", true));
        data.add(new ItemDTO<>("Pinned List", "pinned_list", true));
        data.add(new ItemDTO<>("JavaScript调用", "invoke_js", true));
        data.add(new ItemDTO<>("Adapter Binding", "adapter_binding", true));
        data.add(new ItemDTO<>("Activity Binding", "activity_binding", true));
        data.add(new ItemDTO<>("侧滑RecyclerView", "slide_recyclerview", true));
        data.add(new ItemDTO<>("FFMpeg", "ffmpeg", true));
        data.add(new ItemDTO<>("系统MediaCodec硬编解码", "system_media_code", true));
        data.add(new ItemDTO<>("小票/标签打印", "printer", true));
        data.add(new ItemDTO<>("本地相册/视频/音频选择", "media_selector", true));

        return data;
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

    /**
     * 功能列表数据适配
     */
    private static class ListAdapter extends BasicBindingAdapter<ItemDTO<String>> {

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_main_func;
        }

        @Override
        protected void convert(BasicViewHolder holder, ItemDTO<String> itemInfo, int position) {
            ItemMainFuncBinding binding = holder.getBinding();
            binding.btn.setText(itemInfo.getName());
        }
    }
}