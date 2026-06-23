package com.richard.library.basic.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.basic.util.DrawableUtil;
import com.richard.library.context.AppContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <br>Description : 标签栏
 * <br>Author : Richard
 * <br>Date : 2017/12/26 12:17
 * <br>Changelog:
 * <br>Version            Date            Author              Detail
 * <br> ----------------------------------------------------------------------
 * <br>1.0        2017/12/26 12:17       Administrator      new file.
 */
public class TabBarView extends RadioGroup {

    private OnTabBarCheckedChangeListener mOnTabBarCheckedChangeListener;

    private final List<Item> data = new ArrayList<>();
    private final List<RadioButton> radioButtonList = new ArrayList<>();

    //字体大小
    private float uncheckTextSize;
    private float checkedTextSize;
    //未选中字体颜色
    private int unCheckTextColor;
    //选中字体颜色
    private int checkedTextColor;
    //RadioGroup 背景颜色
    private int contentBackColor;
    //item未选中背景颜色
    private int itemUncheckBackColor;
    //item选中背景颜色
    private int itemCheckedBackColor;
    //item背景资源id
    private int itemBackDrawable;
    //item项的drawablePadding属性值
    private float itemDrawablePadding;
    //间隔视图的宽度
    private float blankViewWidth;
    //间隔视图的背景颜色
    private int blankViewColor;
    //对radiuButton是否对总宽度均分占位
    private boolean isAverage;
    //radiuButton的左内边距和右内边距
    private float paddingLeftRight;
    //默认选中的tabbar item 下标位置
    private int defaultCheckedItemPosition = -1;
    //选中状态是否为底部条样式
    private boolean isBottomBarStyle;
    //当tbv_is_bottom_bar_style为true时的bottomBar高度值
    private float whenBottomBarStyleHeight;
    //当tbv_is_bottom_bar_style为true时的bottomBar宽度值
    private float whenBottomBarStyleWidth;
    //圆角弧度
    private float radius;
    //四个边角弧度
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;
    //以|分隔的多个文本数据项
    private String texts;

    //是否将圆角参数只应用到整个TabBarView的圆角弧度，默认为应用到TabBarView 整个view圆角弧度和TabBarView的子项item圆角弧度
    private boolean isApplyContentRadius = false;

    //是否将border参数应用到整个TabBarView的边框，默认只应用到TabBarView的子项item边框
    private boolean isApplyContentBorder = true;

    //边框线大小
    private float contentBorderLineSize;
    //边框线颜色
    private int contentBorderLineColor;
    //字体是否加粗
    private boolean uncheckTextIsBold;
    private boolean checkedTextIsBold;
    //红点drawable
    private Drawable iconMessageRedPoint;

    public TabBarView(Context context) {
        super(context);
        initView(null);
    }

    public TabBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }


    private void initView(AttributeSet attrs) {
        this.setOrientation(HORIZONTAL);

        iconMessageRedPoint = ContextCompat.getDrawable(getContext(), R.mipmap.icon_message_red_point);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TabBarView);

            uncheckTextSize = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_uncheck_text_size, this.sp2px(getContext(), 14));
            checkedTextSize = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_checked_text_size, this.sp2px(getContext(), 14));
            unCheckTextColor = typedArray.getColor(R.styleable.TabBarView_tbv_uncheck_text_color, Color.parseColor("#9accbd"));
            checkedTextColor = typedArray.getColor(R.styleable.TabBarView_tbv_checked_text_color, Color.parseColor("#0d6fb7"));
            radius = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_radius, this.dp2px(getContext(), 4));
            topLeftRadius = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_topLeftRadius, this.dp2px(getContext(), 0));
            topRightRadius = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_topRightRadius, this.dp2px(getContext(), 0));
            bottomLeftRadius = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_bottomLeftRadius, this.dp2px(getContext(), 0));
            bottomRightRadius = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_bottomRightRadius, this.dp2px(getContext(), 0));
            contentBackColor = typedArray.getColor(R.styleable.TabBarView_tbv_content_back_color, Color.parseColor("#2e000000"));
            itemUncheckBackColor = typedArray.getColor(R.styleable.TabBarView_tbv_item_uncheck_back_color, Color.parseColor("#00000000"));
            itemCheckedBackColor = typedArray.getColor(R.styleable.TabBarView_tbv_item_checked_back_color, Color.parseColor("#ffffff"));
            itemBackDrawable = typedArray.getResourceId(R.styleable.TabBarView_tbv_item_back_drawable, 0);
            itemDrawablePadding = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_item_drawable_padding, -1);
            blankViewWidth = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_blank_view_width, 0);
            blankViewColor = typedArray.getColor(R.styleable.TabBarView_tbv_blank_view_color, Color.TRANSPARENT);
            isAverage = typedArray.getBoolean(R.styleable.TabBarView_tbv_width_isAverage, false);
            paddingLeftRight = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_padding_left_right, this.dp2px(getContext(), 18));
            defaultCheckedItemPosition = typedArray.getInteger(R.styleable.TabBarView_tbv_default_checked_item_position, -1);
            isBottomBarStyle = typedArray.getBoolean(R.styleable.TabBarView_tbv_is_bottom_bar_style, false);
            whenBottomBarStyleWidth = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_whenBottomBarStyleWidth, -1);
            whenBottomBarStyleHeight = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_whenBottomBarStyleHeight, this.dp2px(getContext(), 2));
            texts = typedArray.getString(R.styleable.TabBarView_tbv_texts);
            isApplyContentRadius = typedArray.getBoolean(R.styleable.TabBarView_tbv_isApplyContentRadius, false);
            isApplyContentBorder = typedArray.getBoolean(R.styleable.TabBarView_tbv_isApplyContentBorder, true);
            contentBorderLineSize = typedArray.getDimensionPixelSize(R.styleable.TabBarView_tbv_content_border_line_size, 0);
            contentBorderLineColor = typedArray.getColor(R.styleable.TabBarView_tbv_content_border_line_color, Color.TRANSPARENT);
            uncheckTextIsBold = typedArray.getBoolean(R.styleable.TabBarView_tbv_uncheck_text_is_bold, false);
            checkedTextIsBold = typedArray.getBoolean(R.styleable.TabBarView_tbv_checked_text_is_bold, false);

            if (contentBorderLineSize < 1) {
                contentBorderLineSize = 1;
            }

            typedArray.recycle();
            typedArray = null;
        } else {
            uncheckTextSize = this.sp2px(getContext(), 14);
            checkedTextSize = this.sp2px(getContext(), 14);
            unCheckTextColor = Color.parseColor("#9accbd");
            checkedTextColor = Color.parseColor("#0d6fb7");
            radius = this.dp2px(getContext(), 4);
            contentBackColor = Color.parseColor("#2e000000");
            itemUncheckBackColor = Color.parseColor("#00000000");
            itemCheckedBackColor = Color.parseColor("#ffffff");
            itemDrawablePadding = -1;
            blankViewWidth = 0;
            blankViewColor = Color.TRANSPARENT;
            isAverage = false;
            paddingLeftRight = this.dp2px(getContext(), 12);
            whenBottomBarStyleWidth = -1;
            whenBottomBarStyleHeight = this.dp2px(getContext(), 2);
        }

        if (topLeftRadius <= 0 && topRightRadius <= 0 && bottomLeftRadius <= 0 && bottomRightRadius <= 0) {
            topLeftRadius = radius;
            topRightRadius = radius;
            bottomLeftRadius = radius;
            bottomRightRadius = radius;
        }

        //设置选项数据
        if (!TextUtils.isEmpty(texts)) {
            this.setDataText(Arrays.asList(texts.split("\\|")));
        }

        this.setOnCheckedChangeListener((group, checkedId) -> {
            if (mOnTabBarCheckedChangeListener != null) {
                RadioButton radioButton = findViewById(checkedId);
                if (radioButton != null) {
                    Integer position = (Integer) radioButton.getTag();
                    mOnTabBarCheckedChangeListener.checked(data.get(position), position);
                }
            }
        });


        //初始化默认选中位置
        if (null != this.data
                && defaultCheckedItemPosition >= 0
                && defaultCheckedItemPosition < this.data.size()) {
            this.checkItem(defaultCheckedItemPosition);
        }
    }

    /**
     * 设置数据
     */
    public void setDataText(List<String> data) {
        List<Item> dataList = new ArrayList<>();
        for (String item : data) {
            dataList.add(new Item(item, item));
        }
        this.setData(dataList);
    }

    /**
     * 设置数据
     */
    public void setData(List<Item> data) {
        this.data.clear();
        this.radioButtonList.clear();
        this.removeAllViews();
        this.data.addAll(data);
        this.setBackground(this.generatorGradientDrawable(
                contentBackColor
                , topLeftRadius
                , topRightRadius
                , bottomLeftRadius
                , bottomRightRadius
                , isApplyContentBorder ? contentBorderLineSize : 0
                , contentBorderLineColor
        ));

        if (isApplyContentBorder) {
            this.setPadding(
                    (int) contentBorderLineSize
                    , (int) contentBorderLineSize
                    , (int) contentBorderLineSize
                    , (int) contentBorderLineSize
            );
        }

        int size = this.data.size();
        for (int i = 0; i < size; i++) {
            Item item = this.data.get(i);

            RadioButton radioButton = this.generatorRadioButton(
                    item.getText()
                    , i
                    , isAverage
                    , paddingLeftRight
                    , i == 0 || !isApplyContentRadius ? topLeftRadius : 0
                    , !isApplyContentRadius && i > 0 && i <= size - 1 || (!isApplyContentRadius && i == 0) || i == size - 1 ? topRightRadius : 0
                    , i == 0 || !isApplyContentRadius ? bottomLeftRadius : 0
                    , !isApplyContentRadius && i > 0 && i <= size - 1 || (!isApplyContentRadius && i == 0) || i == size - 1 ? bottomRightRadius : 0
            );

            this.addView(radioButton);
            this.radioButtonList.add(radioButton);
            if (i < size - 1 && blankViewWidth > 0) {
                this.addView(this.generatorBlankView(blankViewColor, blankViewWidth));
            }
        }
    }


    /**
     * 设置监听事件
     */
    public void setOnTabBarCheckedChangeListener(OnTabBarCheckedChangeListener onTabBarCheckedChangeListener) {
        mOnTabBarCheckedChangeListener = onTabBarCheckedChangeListener;
    }


    /**
     * 选中下标为position的radioButton
     */
    public void checkItem(int position) {
        if (this.getChildCount() > 0) {
            ((RadioButton) (findViewWithTag(position))).setChecked(true);
        }
    }

    /**
     * 设置红点可见性
     *
     * @param position item下标
     * @param isShow   是否显示
     */
    public void setShowRedPoint(int position, boolean isShow) {
        if (this.getChildCount() > 0) {
            RadioButton radioButton = findViewWithTag(position);
            radioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, isShow ? iconMessageRedPoint : null, null);
        }
    }

    /**
     * 清空所有红点显示
     */
    public void clearFullRedPoint() {
        for (int index = 0; index < this.getChildCount(); index++) {
            this.setShowRedPoint(index, false);
        }
    }


    /**
     * 获取已选中的RadioButton位置
     */
    public int getCheckedItemPosition() {
        View view = findViewById(getCheckedRadioButtonId());
        return view == null ? -1 : (int) view.getTag();
    }

    /**
     * 获取已选中的RadioButton item
     */
    public Item getCheckedItem() {
        int position = getCheckedItemPosition();
        if (position < 0) {
            return null;
        }
        return data.get(position);
    }

    /**
     * 根据下标位置获取对应button
     *
     * @param position 下标位置
     */
    public RadioButton getButton(int position) {
        if (this.radioButtonList.size() > position) {
            return this.radioButtonList.get(position);
        }
        return null;
    }

    /**
     * 获取RadioGroup下的所有RadioButton
     */
    public List<RadioButton> getRadioButtonList() {
        return this.radioButtonList;
    }


    /**
     * 设置对应下标位置button的文本
     *
     * @param position   下标位置
     * @param buttonText 文本
     */
    public void setButtonText(int position, String buttonText) {
        this.setButtonText(position, new Item(buttonText));
    }

    /**
     * 设置对应下标位置button的文本
     *
     * @param position   下标位置
     * @param buttonItem 按钮文本项
     */
    public void setButtonText(int position, Item buttonItem) {
        RadioButton radioButton = this.getButton(position);
        if (radioButton == null) {
            return;
        }
        this.data.remove(position);
        this.data.add(position, buttonItem);
        radioButton.setText(buttonItem.getText());
    }


    /**
     * 生成RadioButton
     *
     * @param text
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    private RadioButton generatorRadioButton(
            String text
            , int position
            , boolean isAverage
            , float paddingLeftRight
            , float topLeftRadius
            , float topRightRadius
            , float bottomLeftRadius
            , float bottomRightRadius
    ) {
        RadioButton radioButton = new RadioButton(getContext());

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                Field field = radioButton.getClass().getSuperclass().getDeclaredField("mButtonDrawable");
                field.setAccessible(true);
                field.set(radioButton, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            radioButton.setButtonDrawable(null);
        }

        if (!isAverage) {
            radioButton.setPadding(
                    (int) paddingLeftRight
                    , radioButton.getTop()
                    , (int) paddingLeftRight
                    , radioButton.getBottom()
            );
        }

        LayoutParams rlp;
        if (isAverage) {
            rlp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            rlp.weight = 1;
        } else {
            rlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        radioButton.setLayoutParams(rlp);

        radioButton.setTag(position);
        radioButton.setGravity(Gravity.CENTER);
        radioButton.setText(text);
        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, uncheckTextSize);
        radioButton.setTextColor(generatorTextColorDrawable(unCheckTextColor, checkedTextColor));
        radioButton.getPaint().setFakeBoldText(uncheckTextIsBold);

        if (itemDrawablePadding > 0) {
            radioButton.setCompoundDrawablePadding((int) itemDrawablePadding);
        }

        if (isBottomBarStyle) {
//            if (!isAverage) {
//                rlp.width = MeasuredUtil.getMeasuredWidth(radioButton);
//                radioButton.setLayoutParams(rlp);
//            }

            if (itemBackDrawable != 0) {
                radioButton.setCompoundDrawablesWithIntrinsicBounds(
                        null
                        , null
                        , null
                        , ContextCompat.getDrawable(getContext(), itemBackDrawable)
                );
            } else {
                radioButton.setCompoundDrawablesWithIntrinsicBounds(
                        null
                        , null
                        , null
                        , generatorItemSelector(
                                itemUncheckBackColor
                                , itemCheckedBackColor
                                , topLeftRadius
                                , topRightRadius
                                , bottomLeftRadius
                                , bottomRightRadius
                        ));
            }
        } else {
            if (itemBackDrawable != 0) {
                radioButton.setBackground(ContextCompat.getDrawable(getContext(), itemBackDrawable));
            } else {
                radioButton.setBackground(this.generatorItemSelector(
                        itemUncheckBackColor
                        , itemCheckedBackColor
                        , topLeftRadius
                        , topRightRadius
                        , bottomLeftRadius
                        , bottomRightRadius
                ));
            }
        }

        radioButton.setOnTouchListener((view, motionEvent) -> {
            RadioButton radioButton1 = findViewWithTag(view.getTag());
            if (TextUtils.isEmpty(radioButton1.getText())) {
                return true;
            }
            return false;
        });

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (uncheckTextSize != checkedTextSize) {
                    if (isChecked) {
                        buttonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, checkedTextSize);
                    } else {
                        buttonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, uncheckTextSize);
                    }
                }

                if (uncheckTextIsBold != checkedTextIsBold) {
                    buttonView.getPaint().setFakeBoldText(isChecked ? checkedTextIsBold : uncheckTextIsBold);
                }
            }
        });

        return radioButton;
    }


    /**
     * 生成竖线
     */
    private View generatorBlankView(int backColor, float width) {
        LinearLayout blankView = new LinearLayout(getContext());

        LayoutParams rlp = new LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
//        rlp.topMargin = this.dp2px(getContext(), 5);
//        rlp.bottomMargin = this.dp2px(getContext(), 5);
        blankView.setLayoutParams(rlp);

        blankView.setBackgroundColor(backColor);

        return blankView;
    }


    /**
     * 生成未选中和选中后的状态的Drawable
     *
     * @param unCheckColor      未选中背景颜色
     * @param checkedColor      未选中背景颜色
     * @param topLeftRadius     左上角弧度
     * @param topRightRadius    右上角弧度
     * @param bottomLeftRadius  左下角弧度
     * @param bottomRightRadius 右下角弧度
     */
    private StateListDrawable generatorItemSelector(
            int unCheckColor
            , int checkedColor
            , float topLeftRadius
            , float topRightRadius
            , float bottomLeftRadius
            , float bottomRightRadius
    ) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(
                new int[]{-android.R.attr.state_checked}
                , generatorGradientDrawable(
                        unCheckColor
                        , topLeftRadius
                        , topRightRadius
                        , bottomLeftRadius
                        , bottomRightRadius
                        , 0
                        , 0
                ));

        stateListDrawable.addState(
                new int[]{android.R.attr.state_checked}
                , generatorGradientDrawable(
                        checkedColor
                        , topLeftRadius
                        , topRightRadius
                        , bottomLeftRadius
                        , bottomRightRadius
                        , 0
                        , 0
                ));


        return stateListDrawable;
    }


    /**
     * 生成单个Drawable
     *
     * @param color             背景颜色
     * @param topLeftRadius     左上角弧度
     * @param topRightRadius    右上角弧度
     * @param bottomLeftRadius  左下角弧度
     * @param bottomRightRadius 右下角弧度
     */
    private GradientDrawable generatorGradientDrawable(
            int color
            , float topLeftRadius
            , float topRightRadius
            , float bottomLeftRadius
            , float bottomRightRadius
            , float borderLineSize
            , int borderLineColor
    ) {
        //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        //topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius
        float[] radii = new float[]{
                topLeftRadius
                , topLeftRadius
                , topRightRadius
                , topRightRadius
                , bottomRightRadius
                , bottomRightRadius
                , bottomLeftRadius
                , bottomLeftRadius
        };

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);

        if (DrawableUtil.isRound(radii)) {
            gradientDrawable.setCornerRadius(topLeftRadius);
        } else {
            gradientDrawable.setCornerRadii(radii);
        }

        if (isBottomBarStyle) {
            if (whenBottomBarStyleWidth > 0) {
                gradientDrawable.setSize((int) whenBottomBarStyleWidth, (int) whenBottomBarStyleHeight);
            } else {
                gradientDrawable.setSize(AppContext.getScreenWidth(getContext()), (int) whenBottomBarStyleHeight);
            }
        }

        if (borderLineSize > 0) {
            gradientDrawable.setStroke((int) borderLineSize, borderLineColor);
        }

        return gradientDrawable;
    }


    /**
     * 生成字体颜色stateList
     *
     * @param unCheckTextColor 未选中颜色
     * @param checkedTextColor 选中的颜色
     * @return
     */
    private ColorStateList generatorTextColorDrawable(int unCheckTextColor, int checkedTextColor) {
        int[][] states = new int[2][];
        states[0] = new int[]{-android.R.attr.state_checked};
        states[1] = new int[]{android.R.attr.state_checked};

        int[] colors = new int[]{unCheckTextColor, checkedTextColor};

        return new ColorStateList(states, colors);
    }


    public interface OnTabBarCheckedChangeListener {
        void checked(Item item, int position);
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    private int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP
                , spVal
                , context.getResources().getDisplayMetrics()
        );
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , dpVal
                , context.getResources().getDisplayMetrics()
        );
    }

    /**
     * item 项
     */
    public static class Item {
        /**
         * item 文本
         */
        private String text;

        /**
         * item 数据项
         */
        private Object data;

        public Item(String text) {
            this.text = text;
        }

        public Item(String text, Object data) {
            this.text = text;
            this.data = data;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
