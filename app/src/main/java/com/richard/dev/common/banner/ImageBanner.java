package com.richard.dev.common.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.richard.library.basic.widget.banner.BasicIndicatorBanner;
import com.richard.library.basic.util.ImageLoader;

/**
 * @author: admin-richard
 * @createDate: 2022/8/23 11:14
 * @version: 1.0
 * @description: 描述
 */
public class ImageBanner extends BasicIndicatorBanner<String, ImageBanner> {

    public ImageBanner(Context context) {
        super(context);
        this.init();
    }

    public ImageBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    private void init(){
        setOnNextIntervalTime(new OnNextIntervalTime<String>() {
            @Override
            public long getNextIntervalTime(String itemInfo, int position) {
                return 5;
            }
        });
    }

    @Override
    public View onCreateItemView(String itemInfo, int position) {
        Log.d("testtt","onCreateItemView : " + position);
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageLoader.get().load(imageView, itemInfo);
        return imageView;
    }
}
