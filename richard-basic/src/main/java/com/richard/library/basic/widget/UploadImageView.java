package com.richard.library.basic.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.adapter.BasicBindingAdapter;
import com.richard.library.basic.basic.adapter.BasicViewHolder;
import com.richard.library.basic.databinding.ItemUploadImageBinding;
import com.richard.library.basic.dto.ItemDTO;
import com.richard.library.basic.widget.list.SRecyclerView;
import com.richard.library.context.util.DensityUtilKt;
import com.richard.library.context.util.FileUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 * Description : 批量上传图片控件
 * Author : admin-richard
 * Date : 2022/6/20 11:45
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2022/6/20 11:45     admin-richard         new file.
 * </pre>
 */
public class UploadImageView extends FrameLayout {

    private ImageAdapter imageAdapter;
    private int maxSize = 1;//可上传的最大数量


    public UploadImageView(@NonNull Context context) {
        super(context);
        this.init(null);
    }

    public UploadImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public UploadImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UploadImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        int column = 3;
        int dividerSize = DensityUtilKt.dp2px(3, getContext());
        int dividerColor = Color.TRANSPARENT;
        int itemViewHeight = DensityUtilKt.dp2px(78, getContext());
        boolean isShowDelete = false;

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.UploadImageView);
            column = typedArray.getInt(R.styleable.UploadImageView_android_columnCount, column);
            dividerSize = typedArray.getDimensionPixelSize(R.styleable.UploadImageView_uiv_divider_size, dividerSize);
            dividerColor = typedArray.getColor(R.styleable.UploadImageView_uiv_divider_color, dividerColor);
            itemViewHeight = typedArray.getDimensionPixelSize(R.styleable.UploadImageView_uiv_item_view_height, itemViewHeight);
            isShowDelete = typedArray.getBoolean(R.styleable.UploadImageView_uiv_show_delete, false);
            maxSize = typedArray.getInt(R.styleable.UploadImageView_uiv_max_size, 1);
            typedArray.recycle();
        }

        SRecyclerView listView = new SRecyclerView(getContext());
        listView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        listView.setLayoutType(1);
        listView.setScrollDirection(1);
        listView.setColumn(column);
        listView.setDividerSize(dividerSize);
        listView.setDividerColor(dividerColor);
        listView.notifyAttrChanged();

        this.addView(listView);

        imageAdapter = new ImageAdapter(itemViewHeight, isShowDelete, maxSize);
        listView.setAdapter(imageAdapter);
    }


    /**
     * 设置图片列表数据
     *
     * @param imageUrlList 必填 图片地址（本地路径或者网络图片地址）
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setImageList(List<String> imageUrlList) {
        this.setImageList(imageUrlList, null);
    }

    /**
     * 设置图片列表数据
     *
     * @param imageUrlList  必填 图片地址（本地路径或者网络图片地址）
     * @param submitUrlList 选填 最终提交给云端的图片地址（和imageUrlList对应）
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setImageList(List<String> imageUrlList, List<String> submitUrlList) {
        if (submitUrlList != null && imageUrlList.size() != submitUrlList.size()) {
            throw new RuntimeException("imageUrlList和submitUrlList必须完全对应，数量一致");
        }

        this.reset();

        boolean isMaxSize;
        for (int i = 0, size = imageUrlList.size(); i < size; i++) {
            if (submitUrlList != null) {
                isMaxSize = this.addImageItem(imageUrlList.get(i), submitUrlList.get(i));
            } else {
                isMaxSize = this.addImageItem(imageUrlList.get(i), null);
            }

            if (isMaxSize) {
                break;
            }
        }

        imageAdapter.notifyDataSetChanged();
    }

    /**
     * 添加图片
     *
     * @param imageUrlList 必填 图片地址（本地路径或者网络图片地址）
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addImageList(List<String> imageUrlList) {
        this.addImageList(imageUrlList, null);
    }

    /**
     * 添加图片
     *
     * @param imageUrlList  必填 图片地址（本地路径或者网络图片地址）
     * @param submitUrlList 选填 最终提交给云端的图片地址
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addImageList(List<String> imageUrlList, List<String> submitUrlList) {
        if (submitUrlList != null && imageUrlList.size() != submitUrlList.size()) {
            throw new RuntimeException("imageUrlList和submitUrlList必须完全对应，数量一致");
        }

        boolean isMaxSize;
        for (int i = 0, size = imageUrlList.size(); i < size; i++) {
            if (submitUrlList != null) {
                isMaxSize = this.addImageItem(imageUrlList.get(i), submitUrlList.get(i));
            } else {
                isMaxSize = this.addImageItem(imageUrlList.get(i), null);
            }

            if (isMaxSize) {
                break;
            }
        }

        imageAdapter.notifyDataSetChanged();
    }

    /**
     * 添加图片
     *
     * @param imageUrl 必填 图片地址（本地路径或者网络图片地址）
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addImage(String imageUrl) {
        this.addImage(imageUrl, null);
    }

    /**
     * 添加图片
     *
     * @param imageUrl  必填 图片地址（本地路径或者网络图片地址）
     * @param submitUrl 选填 最终提交给云端的图片地址
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addImage(String imageUrl, String submitUrl) {
        this.addImageItem(imageUrl, submitUrl);
        imageAdapter.notifyDataSetChanged();
    }

    /**
     * 新增一条上传图片item项
     *
     * @param imageUrl  必填 图片地址（本地路径或者网络图片地址）
     * @param submitUrl 选填 最终提交给云端的图片地址
     * @return 是否已达到数量上限
     */
    private boolean addImageItem(String imageUrl, String submitUrl) {
        if (imageAdapter.getCurrentUploadItem() == null) {
            ItemDTO<String> itemDTO = new ItemDTO<>("", imageUrl, submitUrl);
            itemDTO.setItemType(ImageAdapter.ITEM_TYPE_IMAGE);
            List<ItemDTO<String>> data = imageAdapter.getData();
            data.add(data.size() - 1, itemDTO);
        } else {
            imageAdapter.getCurrentUploadItem().setImageURL(imageUrl);
            imageAdapter.getCurrentUploadItem().setData(submitUrl);
        }

        return imageAdapter.onUpdateList();
    }

    /**
     * 获取上传图片url
     */
    public List<String> getUploadImageUrl() {
        List<String> urlList = new ArrayList<>();
        for (int i = 0, size = imageAdapter.getData().size() - 1; i < size; i++) {
            urlList.add(imageAdapter.getData().get(i).getImageURL());
        }
        return urlList;
    }

    /**
     * 获取用于提交给云端的图片地址
     */
    public List<String> getSubmitImageUrl() {
        List<String> urlList = new ArrayList<>();
        for (int i = 0, size = imageAdapter.getData().size() - 1; i < size; i++) {
            String url = imageAdapter.getData().get(i).getData();
            if (TextUtils.isEmpty(url)) {
                continue;
            }
            urlList.add(url);
        }
        return urlList;
    }

    /**
     * 重置
     */
    @SuppressLint("NotifyDataSetChanged")
    public void reset() {
        imageAdapter.reset();
    }

    /**
     * 获取当前还能上传的最大数量
     */
    public int getCanUploadSize() {
        return imageAdapter.getCanUploadSize();
    }

    /**
     * 获取最大可上传的数量
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 设置最大可上传的数量
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        imageAdapter.setEnabled(enabled);
    }

    /**
     * 设置回调事件
     */
    public void setCallback(ImageAdapter.Callback callback) {
        imageAdapter.setCallback(callback);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (imageAdapter != null) {
            imageAdapter.setCallback(null);
        }
        super.onDetachedFromWindow();
    }

    /**
     * 图片列表数据适配
     */
    public static class ImageAdapter extends BasicBindingAdapter<ItemDTO<String>> {

        private static final int ITEM_TYPE_IMAGE = 0;
        private static final int ITEM_TYPE_ADD = 1;

        private Callback callback;
        private ItemDTO<String> addImageItem;
        private ItemDTO<String> currentUploadItem;

        private final int itemViewHeight;
        private final boolean isShowDelete;
        private final int maxSize;
        private boolean isEnabled = true;

        private ImageAdapter(int itemViewHeight, boolean isShowDelete, int maxSize) {
            this.itemViewHeight = itemViewHeight;
            this.isShowDelete = isShowDelete;
            this.maxSize = maxSize;

            addImageItem = new ItemDTO<>("", "");
            addImageItem.setItemType(ImageAdapter.ITEM_TYPE_ADD);
            this.completeLoadOne(addImageItem, true);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_upload_image;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void convert(BasicViewHolder holder, ItemDTO<String> itemInfo, int position) {
            ItemUploadImageBinding binding = (ItemUploadImageBinding) holder.getBinding();
            ViewGroup.LayoutParams lp = binding.getRoot().getLayoutParams();
            if (lp.height != itemViewHeight) {
                lp.height = itemViewHeight;
                binding.getRoot().setLayoutParams(lp);
            }

            switch (itemInfo.getItemType()) {
                case ITEM_TYPE_IMAGE:
                    binding.ivDelete.setVisibility(isShowDelete ? VISIBLE : GONE);
                    binding.tvLabel.setVisibility(GONE);
                    binding.ivVideoPlay.setVisibility(FileUtil.isVideo(itemInfo.getImageURL()) ? VISIBLE : GONE);

                    if (callback != null) {
                        callback.loadImage(binding.rivImage, itemInfo.getImageURL());
                    }

                    binding.ivDelete.setOnClickListener((v) -> {
                        if (!isEnabled || position >= getItemCount()) return;
                        getData().remove(position);
                        onUpdateList();
                        notifyDataSetChanged();
                        if (callback != null) {
                            callback.onClickDelete(itemInfo, position);
                        }
                    });

                    holder.itemView.setOnClickListener((v) -> {
                        if (!isEnabled) return;
                        currentUploadItem = itemInfo;
                        if (callback != null) {
                            callback.onClickImage(itemInfo, position);
                        }
                    });
                    break;
                case ITEM_TYPE_ADD:
                    binding.ivVideoPlay.setVisibility(GONE);
                    binding.ivDelete.setVisibility(GONE);
                    binding.tvLabel.setVisibility(VISIBLE);
                    binding.rivImage.setImageResource(R.color.light_gray);
                    holder.itemView.setOnClickListener((v) -> {
                        if (!isEnabled) return;
                        currentUploadItem = null;
                        if (callback != null) {
                            callback.onClickAddImage();
                        }
                    });
                    break;
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
            notifyDataSetChanged();
        }

        /**
         * 重置
         */
        @SuppressLint("NotifyDataSetChanged")
        public void reset() {
            List<ItemDTO<String>> data = getData();
            if (data.size() <= 1 && data.contains(addImageItem)) {
                return;
            }

            data.clear();
            data.add(addImageItem);
            this.notifyDataSetChanged();
        }

        /**
         * 当更新列表时调用
         *
         * @return 图片列表数量是否超过最大限制
         */
        public boolean onUpdateList() {
            List<ItemDTO<String>> data = getData();
            boolean isContainsAdd = data.contains(addImageItem);
            int imageCount = isContainsAdd ? data.size() - 1 : data.size();
            if (imageCount >= maxSize) {
                data.remove(addImageItem);
                return true;
            }

            if (!isContainsAdd) {
                data.add(addImageItem);
            }

            return false;
        }

        /**
         * 获取当前还能上传的最大数量
         */
        public int getCanUploadSize() {
            List<ItemDTO<String>> data = getData();
            boolean isContainsAdd = data.contains(addImageItem);
            int imageCount = isContainsAdd ? data.size() - 1 : data.size();
            return Math.max(maxSize - imageCount, 0);
        }

        /**
         * 获取当前选择重新上传的item
         * 为null时代表是即将新添加上传展品
         */
        public ItemDTO<String> getCurrentUploadItem() {
            return currentUploadItem;
        }

        public void setCallback(Callback callback) {
            this.callback = callback;
        }

        public interface Callback {

            /**
             * 点击删除按钮时
             */
            default void onClickDelete(ItemDTO<String> itemInfo, int position) {
            }

            /**
             * 加载图片
             *
             * @param imageView 图片展示控件
             * @param url       图片加载url
             */
            void loadImage(ImageView imageView, String url);

            /**
             * 点击图片时
             */
            void onClickImage(ItemDTO<String> itemInfo, int position);

            /**
             * 点击添加图片按钮
             */
            void onClickAddImage();
        }
    }
}
