package com.richard.library.mediaselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.luck.picture.lib.basic.PictureSelectionCameraModel;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelectionPreviewModel;
import com.luck.picture.lib.basic.PictureSelectionQueryModel;
import com.luck.picture.lib.basic.PictureSelectionSystemModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.utils.SandboxTransformUtils;
import com.richard.library.mediaselector.engine.GlideEngine;
import com.richard.library.mediaselector.engine.ImageFileCompressEngine;
import com.richard.library.mediaselector.engine.ImageFileCropEngine;

import java.util.ArrayList;

/**
 * <pre>
 * Description : 多媒体文件选择器
 * Author : admin-richard
 * Date : 2022/11/3 14:10
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/11/3 14:10      admin-richard         new file.
 * </pre>
 */
public final class MediaSelector {

    private final PictureSelector pictureSelector;

    private MediaSelector(Activity activity) {
        this(activity, null);
    }

    private MediaSelector(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    public static MediaSelector create(Context context) {
        return new MediaSelector((Activity) context);
    }

    public static MediaSelector create(AppCompatActivity activity) {
        return new MediaSelector(activity);
    }

    public static MediaSelector create(FragmentActivity activity) {
        return new MediaSelector(activity);
    }

    public static MediaSelector create(Fragment fragment) {
        return new MediaSelector(fragment);
    }

    private MediaSelector(Activity activity, Fragment fragment) {
        if (activity != null) {
            pictureSelector = PictureSelector.create(activity);
        } else {
            pictureSelector = PictureSelector.create(fragment);
        }
    }

    /**
     * 选择所需的图像类型，全部或图像、视频或音频
     *
     * @param chooseMode Select the type of images you want，all or images or video or audio
     * @return LocalMedia PictureSelectionModel
     * Use {@link SelectMimeType}
     */
    public PictureSelectionModel openGallery(int chooseMode) {
        PictureSelectorStyle selectorStyle = defaultSelectorUIStyle();
        return new PictureSelectionModel(pictureSelector, chooseMode)
                .setSelectorUIStyle(selectorStyle)
                .setCompressEngine(defaultCompressEngine())
                .setImageEngine(defaultImageEngine())
                .setSandboxFileEngine(defaultSandboxFileEngine())
                .isOriginalControl(true);
    }

    /**
     * 仅使用照相机、图像或视频或音频
     *
     * @param chooseMode only use camera，images or video or audio
     * @return LocalMedia PictureSelectionModel
     * Use {@link SelectMimeType}
     */
    public PictureSelectionCameraModel openCamera(int chooseMode) {
        return new PictureSelectionCameraModel(pictureSelector, chooseMode)
                .setCompressEngine(defaultCompressEngine())
                .setSandboxFileEngine(defaultSandboxFileEngine())
                .isOriginalControl(true);
    }

    /**
     * 选择所需的图像类型，全部或图像、视频或音频
     *
     * @param chooseMode Select the type of images you want，all or images or video or audio
     * @return LocalMedia PictureSelectionSystemModel
     * Use {@link SelectMimeType}
     * <p>
     * openSystemGallery mode only supports some APIs
     * </p>
     */
    public PictureSelectionSystemModel openSystemGallery(int chooseMode) {
        return new PictureSelectionSystemModel(pictureSelector, chooseMode)
                .setCompressEngine(defaultCompressEngine())
                .setSandboxFileEngine(defaultSandboxFileEngine())
                .isOriginalControl(true);
    }

    /**
     * 预览模式可预览图像、视频或音频
     */
    public PictureSelectionPreviewModel openPreview() {
        return new PictureSelectionPreviewModel(pictureSelector)
                .setSelectorUIStyle(defaultSelectorUIStyle())
                .setImageEngine(defaultImageEngine());
    }

    /**
     * 查询所需的图像类型，全部或图像、视频或音频
     *
     * @param selectMimeType query the type of images you want，all or images or video or audio
     * @return LocalMedia PictureSelectionQueryModel
     * Use {@link SelectMimeType}
     * <p>
     * only query {@link LocalMedia} data source
     * </p>
     */
    public PictureSelectionQueryModel dataSource(int selectMimeType) {
        return new PictureSelectionQueryModel(pictureSelector, selectMimeType);
    }

    /**
     * 获取选择器列表
     *
     * @param intent 意图对象
     * @return get Selector  LocalMedia
     */
    public static ArrayList<LocalMedia> obtainSelectorList(Intent intent) {
        if (intent == null) {
            return new ArrayList<>();
        }
        ArrayList<LocalMedia> result = intent.getParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION);
        return result != null ? result : new ArrayList<>();
    }

    /**
     * 获取默认选择器UI样式
     */
    public static PictureSelectorStyle defaultSelectorUIStyle() {
        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
        animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in);
        animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out);

        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        selectorStyle.setWindowAnimationStyle(animationStyle);
        return selectorStyle;
    }

    /**
     * 获取默认图片文件压缩引擎
     */
    public static CompressFileEngine defaultCompressEngine() {
        return new ImageFileCompressEngine();
    }

    /**
     * 获取默认的图片引擎
     */
    public static ImageEngine defaultImageEngine() {
        return GlideEngine.createGlideEngine();
    }

    /**
     * 获取默认图片裁剪引擎
     */
    public static CropFileEngine defaultCropFileEngine(Context context) {
        return defaultCropFileEngine(context, null);
    }

    /**
     * 获取默认图片裁剪引擎
     */
    public static CropFileEngine defaultCropFileEngine(Context context, PictureSelectorStyle selectorStyle) {
        return new ImageFileCropEngine(context, selectorStyle);
    }

    /**
     * 获取默认沙盒文件引擎（适配Android 10及以上版本）
     */
    public static UriToFileTransformEngine defaultSandboxFileEngine() {
        return new UriToFileTransformEngine() {
            @Override
            public void onUriToFileAsyncTransform(Context context, String srcPath, String mineType, OnKeyValueResultCallbackListener call) {
                if (call != null) {
                    String sandboxPath = SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType);
                    call.onCallback(srcPath, sandboxPath);
                }
            }
        };
    }
}
