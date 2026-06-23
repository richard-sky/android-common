package com.richard.library.basic.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.richard.library.context.AppContext;

/**
 * <pre>
 * Description : 图片加载器
 * Author : admin-richard
 * Date : 2021-07-05 15:05
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021-07-05 15:05     admin-richard         new file.
 * </pre>
 */
public final class ImageLoader {

    private ImageLoader() {
    }

    private static final class InstanceHolder {
        static final ImageLoader instance = new ImageLoader();
    }

    public static ImageLoader get() {
        return InstanceHolder.instance;
    }

    /**
     * 加载图片
     *
     * @param imageView  图片控件
     * @param imgRes     资源id
     */
    public void load(ImageView imageView, @DrawableRes int imgRes) {
        Glide.with(imageView.getContext())
                .load(imgRes)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.NORMAL)
                )
                .into(imageView);
    }

    /**
     * 加载图片(无动画效果)
     *
     * @param imageView 图片View
     * @param imgUrl    图片地址
     */
    public void load(ImageView imageView, String imgUrl) {
        this.load(imageView, 0, imgUrl, 0, false);
    }

    /**
     * 加载图片(无动画效果)
     *
     * @param imageView 图片View
     * @param imgUrl    图片地址
     */
    public void load(ImageView imageView, String imgUrl, @DrawableRes int placeHolderRes) {
        this.load(imageView, 0, imgUrl, placeHolderRes, false);
    }

    /**
     * 加载图片(有动画效果)
     *
     * @param imageView 图片View
     * @param imgUrl    图片地址
     */
    public void loadAnim(ImageView imageView, String imgUrl) {
        this.load(imageView, 0, imgUrl, 0, true);
    }

    /**
     * 加载图片(有动画效果)
     *
     * @param imageView 图片View
     * @param imgUrl    图片地址
     */
    public void loadAnim(ImageView imageView, String imgUrl, @DrawableRes int placeHolderRes) {
        this.load(imageView, 0, imgUrl, placeHolderRes, true);
    }

    /**
     * 常规加载图片
     *
     * @param imageView      图片View
     * @param imgUrl         图片地址
     * @param placeHolderRes 占位图资源ID
     * @param errorImageRes  加载错误图片资源ID
     * @param isAnimation    是否需要动画（默认淡入淡出动画）
     */
    public void load(ImageView imageView, @DrawableRes int errorImageRes, String imgUrl, @DrawableRes int placeHolderRes, boolean isAnimation) {
        imgUrl = imgUrl == null ? "" : imgUrl;

        RequestBuilder<Drawable> builder = Glide.with(imageView.getContext()).load(imgUrl);
        if (isAnimation) {
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory
                    .Builder(300)
                    .setCrossFadeEnabled(true)
                    .build();
            builder = builder.transition(DrawableTransitionOptions.with(drawableCrossFadeFactory));
        }

        RequestOptions requestOptions = null;
        if (placeHolderRes != 0) {
            requestOptions = new RequestOptions().placeholder(placeHolderRes);
        }

        if (errorImageRes != 0) {
            if (requestOptions == null) {
                requestOptions = new RequestOptions();
            }
            requestOptions = requestOptions.error(errorImageRes);
        }

        if (requestOptions != null) {
            builder = builder.apply(requestOptions);
        }

        builder.into(imageView);
    }

    /**
     * 加载图片并设置为指定大小
     *
     * @param imageView  图片控件
     * @param imgUrl     图片链接
     * @param widthSize  图片宽度
     * @param heightSize 图片高度
     */
    public void load(ImageView imageView, String imgUrl, int widthSize, int heightSize) {
        Glide.with(imageView.getContext())
                .load(imgUrl == null ? "" : imgUrl)
                .apply(new RequestOptions()
                        .override(widthSize, heightSize)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.NORMAL)
                )
                .into(imageView);
    }

    /**
     * 加载图片并设置为指定大小
     *
     * @param imageView  图片控件
     * @param imgRes     图片链接
     * @param widthSize  图片宽度
     * @param heightSize 图片高度
     */
    public void load(ImageView imageView, @DrawableRes int imgRes, int widthSize, int heightSize) {
        Glide.with(imageView.getContext())
                .load(imgRes)
                .apply(new RequestOptions()
                        .override(widthSize, heightSize)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.NORMAL)
                )
                .into(imageView);
    }

    /**
     * 加载gif
     *
     * @param imageView 图片控件
     * @param imgUrl    图片链接
     */
    public void loadGif(ImageView imageView, String imgUrl) {
        Glide.with(imageView.getContext())
                .asGif()
                .load(imgUrl == null ? "" : imgUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.NORMAL)
                )
                .into(imageView);
    }

    /**
     * 加载gif
     *
     * @param imageView 图片控件
     * @param imgRes    图片资源ID
     */
    public void loadGif(ImageView imageView, @DrawableRes int imgRes) {
        Glide.with(imageView.getContext())
                .asGif()
                .load(imgRes)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.NORMAL)
                )
                .into(imageView);
    }

    /**
     * 加载返回Drawable
     *
     * @param imgUrl 图片加载URL
     * @param target 加载结果回调
     */
    public void load(Context context, String imgUrl, Target<Drawable> target) {
        Glide.with(context)
                .load(imgUrl == null ? "" : imgUrl)
                .into(target);
    }

    /**
     * 清除磁盘缓存
     */
    public void clearDiskCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //清理磁盘缓存 需要在子线程中执行
                Glide.get(AppContext.get()).clearDiskCache();
            }
        }).start();
    }

    /**
     * 清除内存缓存
     */
    public void clearMemory() {
        //清理内存缓存  可以在UI主线程中进行
        Glide.get(AppContext.get()).clearMemory();
    }


    /**
     * 创建RecyclerView滑动停止时加载图片事件
     */
    public RecyclerView.OnScrollListener newScrollLoadEvent() {
        return new RecyclerView.OnScrollListener() {
            Context context;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                context = recyclerView.getContext();
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //当屏幕停止滚动，加载图片
                        try {
                            if (context != null) {
                                Glide.with(context).resumeRequests();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                        try {
                            if (context != null) {
                                Glide.with(context).pauseRequests();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

}
