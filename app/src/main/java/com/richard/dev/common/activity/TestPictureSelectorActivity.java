package com.richard.dev.common.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.richard.dev.common.R;
import com.richard.dev.common.databinding.ActivityPictureSelectorBinding;
import com.richard.library.basic.basic.BasicBindingActivity;
import com.richard.library.basic.util.ImageLoader;
import com.richard.library.mediaselector.MediaSelector;

import java.util.ArrayList;

/**
 * <pre>
 * Description : 本地相册选择
 * Author : admin-richard
 * Date : 2022/11/3 10:14
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/11/3 10:14      admin-richard         new file.
 * </pre>
 */
@Route(path = "/test/picture/selector")
public class TestPictureSelectorActivity extends BasicBindingActivity<ActivityPictureSelectorBinding> {

    @Override
    public void initLayoutView() {
        setContentView(R.layout.activity_picture_selector);
    }

    @Override
    public void initData() {
        navigationbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindListener() {
        binding.setEvent(this);
    }

    public void onClickAlbumSelector() {
        MediaSelector.create(this)
                .openGallery(SelectMimeType.ofAll())
                .setCropEngine(MediaSelector.defaultCropFileEngine(this))
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        ImageLoader.get().load(binding.ivImage, result.get(0).getAvailablePath());
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }
}
