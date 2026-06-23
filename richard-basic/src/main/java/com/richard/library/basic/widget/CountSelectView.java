package com.richard.library.basic.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.richard.library.basic.R;
import com.richard.library.basic.util.DrawableUtil;
import com.richard.library.context.util.DensityUtilKt;
import com.richard.library.context.util.MathUtil;

/**
 * <pre>
 * Description : 数量选择控件
 * Author : admin-richard
 * Date : 2019-06-17 18:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-06-17 18:57     admin-richard         new file.
 * </pre>
 */
public class CountSelectView extends LinearLayout implements TextWatcher {

    private CustomEditText numEditText;
    private ImageView reduceImage;
    private ImageView addImage;

    private int reduceIcon;
    private int addIcon;
    private int textColor;
    private int textSize;
    private boolean textBold;
    private int textMinWidth;
    private int borderColor;
    private int borderSize;
    private int bgColor;
    private int iconTint = Color.TRANSPARENT;

    //当前数量
    private double currentNum = 0;//当前数值，默认为0
    private double stepNum = 1;//加减步长,默认为1
    private Double minNum = 0D;//最小数值
    private Double maxNum;//最大数值
    private Callback mCallback;
    private TriggerEventValidate triggerEventValidate;
    private boolean isMinAutoGoneReduce = false;//数量到达最低数值时是否自动隐藏减按钮


    public CountSelectView(Context context) {
        super(context);
        this.init(null);
    }

    public CountSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public CountSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CountSelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        super.setOrientation(HORIZONTAL);
        super.setGravity(Gravity.CENTER);

        int radius = getResources().getDimensionPixelSize(R.dimen.radius_value);
        int btnPadding = DensityUtilKt.dp2px(10, getContext());
        reduceIcon = R.mipmap.icon_home_reduce;
        addIcon = R.mipmap.icon_home_adds;
        textColor = getResources().getColor(R.color.text);
        textSize = getResources().getDimensionPixelSize(R.dimen.text_view_textSize);
        textBold = false;
        textMinWidth = DensityUtilKt.dp2px(25, getContext());
        borderColor = Color.TRANSPARENT;
        borderSize = 0;
        bgColor = Color.TRANSPARENT;

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountSelectView);
            reduceIcon = typedArray.getResourceId(R.styleable.CountSelectView_csv_reduce_icon, reduceIcon);
            addIcon = typedArray.getResourceId(R.styleable.CountSelectView_csv_add_icon, addIcon);
            textColor = typedArray.getColor(R.styleable.CountSelectView_android_textColor, textColor);
            textSize = typedArray.getDimensionPixelSize(R.styleable.CountSelectView_android_textSize, textSize);
            textBold = typedArray.getBoolean(R.styleable.CountSelectView_csv_bold, textBold);
            textMinWidth = typedArray.getDimensionPixelSize(R.styleable.CountSelectView_csv_text_min_width, textMinWidth);
            borderColor = typedArray.getColor(R.styleable.CountSelectView_csv_border_color, borderColor);
            borderSize = typedArray.getDimensionPixelSize(R.styleable.CountSelectView_csv_border_size, borderSize);
            bgColor = typedArray.getColor(R.styleable.CountSelectView_csv_bg, bgColor);
            iconTint = typedArray.getColor(R.styleable.CountSelectView_csv_icon_tint, iconTint);
            typedArray.recycle();
        }

        //取消自动获取焦点
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

        reduceImage = new ImageView(getContext());
        reduceImage.setImageResource(reduceIcon);
        reduceImage.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.MATCH_PARENT
        ));
        if (iconTint != Color.TRANSPARENT) {
            reduceImage.setImageTintList(ColorStateList.valueOf(iconTint));
        }
        if (borderSize > 0) {
            reduceImage.setScaleType(ImageView.ScaleType.CENTER);
            reduceImage.setPadding(btnPadding, 0, btnPadding, 0);
        }

        numEditText = new CustomEditText(getContext());
        numEditText.setBackground(null);
        numEditText.setEditNum(false);
        numEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        numEditText.setFilters(new DecimalInputFilter[]{new DecimalInputFilter(3, 0)});
        numEditText.setText("0");
        numEditText.setGravity(Gravity.CENTER);
        numEditText.setMinWidth(textMinWidth);
        numEditText.setTextColor(textColor);
        numEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.MATCH_PARENT
        );
        numEditText.setLayoutParams(lp);
        numEditText.setPadding(
                numEditText.getPaddingLeft()
                , 0
                , numEditText.getPaddingRight()
                , 0
        );

        addImage = new ImageView(getContext());
        addImage.setImageResource(addIcon);
        addImage.setLayoutParams(new ViewGroup.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.normal_button_height)
                , ViewGroup.LayoutParams.MATCH_PARENT
        ));
        if (iconTint != Color.TRANSPARENT) {
            addImage.setImageTintList(ColorStateList.valueOf(iconTint));
        }
        if (borderSize > 0) {
            addImage.setScaleType(ImageView.ScaleType.CENTER);
            addImage.setPadding(btnPadding, 0, btnPadding, 0);
        }

        if (borderSize > 0) {
            this.setBackground(DrawableUtil.generatorGradientDrawable(
                    bgColor
                    , radius
                    , borderSize
                    , borderColor
            ));
        }

        super.addView(reduceImage);
        if (borderSize > 0) {
            super.addView(this.generateLineView(borderSize, borderColor));
        }
        super.addView(numEditText);
        if (borderSize > 0) {
            super.addView(this.generateLineView(borderSize, borderColor));
        }
        super.addView(addImage);

        reduceImage.setOnClickListener((v) -> this.handle(true, false, 0));
        addImage.setOnClickListener((v) -> this.handle(false, false, 0));
    }

    /**
     * 生成线条
     */
    private View generateLineView(int borderSize, int borderColor) {
        LinearLayout view = new LinearLayout(getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(
                borderSize
                , ViewGroup.LayoutParams.MATCH_PARENT
        ));
        view.setBackgroundColor(borderColor);
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!numEditText.isEditNum) {
            return;
        }
        numEditText.setSelection(numEditText.length());
        if (triggerEventValidate == null || !triggerEventValidate.isUserTriggerEvent()) {
            return;
        }

        double value;
        try {
            value = Double.parseDouble(s.toString());
        } catch (Throwable e) {
            value = 0D;
        }

        if (value == currentNum) {
            return;
        }

        handle(value < currentNum, true, value);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onAttachedToWindow() {
        this.numEditText.addTextChangedListener(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        this.numEditText.removeTextChangedListener(this);
        super.onDetachedFromWindow();
    }

    /**
     * 处理点击事件
     *
     * @param isReduce   是否为点击了"减"；否则为点击了"加"
     * @param isInputNum 是否为输入的数量值
     * @param inputNum   输入的具体的数量值(仅isInputNum = true时有效)
     */
    private void handle(boolean isReduce, boolean isInputNum, double inputNum) {
        double finalNum = isReduce
                ? (isInputNum ? inputNum : MathUtil.subtract(-1, currentNum, stepNum).doubleValue())
                : (isInputNum ? inputNum : MathUtil.add(-1, currentNum, stepNum).doubleValue());

        if (minNum != null && minNum >= 0 && finalNum < 0) {
            finalNum = 0;
        }

        if (minNum != null && isReduce && finalNum < minNum) {
            return;
        }

        if (maxNum != null && !isReduce && finalNum > maxNum) {
            return;
        }

        double offsetValue = Math.abs(MathUtil.subtract(-1, currentNum, finalNum).doubleValue());
        if (mCallback != null && !(isReduce && mCallback.validateReduce(offsetValue) || !isReduce && mCallback.validateAdd(offsetValue))) {
            return;
        }

        currentNum = finalNum;

        String currentNumStr = currentNum != 0 ? MathUtil.replaceEndZero(currentNum) : "0";
        numEditText.setText(currentNumStr);

        if (isMinAutoGoneReduce && minNum != null && finalNum <= minNum) {
            reduceImage.setVisibility(View.INVISIBLE);
        } else {
            reduceImage.setVisibility(View.VISIBLE);
        }

        if (mCallback != null) {
            mCallback.onSelectNumber(isReduce, currentNumStr, offsetValue);
        }
    }

    /**
     * 外部业务主动触发调用
     *
     * @param value 变化数组
     */
    public void performSelect(double value) {
        if (value == currentNum) {
            return;
        }

        if (value < currentNum) {
            handle(true, true, value);
        } else {
            handle(false, true, value);
        }
    }

    /**
     * 主动触发减按钮点击事件
     */
    public void performClickReduceButton() {
        if (!reduceImage.isEnabled()) {
            return;
        }
        if (reduceImage.getVisibility() != View.VISIBLE) {
            return;
        }
        reduceImage.performClick();
    }

    /**
     * 主动触发加按钮点击事件
     */
    public void performClickAddButton() {
        if (!addImage.isEnabled()) {
            return;
        }
        if (addImage.getVisibility() != View.VISIBLE) {
            return;
        }
        addImage.performClick();
    }

    /**
     * 获取当前数值
     */
    public double getCurrentNum() {
        return currentNum;
    }

    /**
     * 获取数字控件
     */
    public CustomEditText getNumEditText() {
        return numEditText;
    }

    /**
     * 设置是否可以在数量的EditText中直接编辑数量
     *
     * @param isEditNum 是否可以编辑数量
     */
    public void setEditNum(boolean isEditNum) {
        this.numEditText.setEditNum(isEditNum);
    }

    /**
     * 设置数量控件的点击事件
     */
    public void setNumViewClickListener(OnClickListener clickListener) {
        this.numEditText.setOnClickListener(clickListener);
    }

    /**
     * 设置当前数值
     *
     * @param currentNum 具体数值
     */
    public void setCurrentNum(double currentNum) {
        this.currentNum = currentNum;
        numEditText.setText(currentNum != 0 ? MathUtil.replaceEndZero(currentNum) : "0");
    }

    /**
     * 设置数量到达最低数值时是否自动隐藏减按钮
     */
    public void setMinAutoGoneReduce(boolean minAutoGoneReduce) {
        this.isMinAutoGoneReduce = minAutoGoneReduce;
    }

    /**
     * 设置最小金额
     *
     * @param minNum 最小金额
     */
    public void setMinNum(Double minNum) {
        this.minNum = minNum;
    }

    /**
     * 设置最大金额
     *
     * @param maxNum 最大金额
     */
    public void setMaxNum(Double maxNum) {
        this.maxNum = maxNum;
    }

    /**
     * 设置步长数值
     *
     * @param stepNum 步长
     */
    public void setStepNum(double stepNum) {
        this.stepNum = stepNum;
    }


    /**
     * 设置加按钮是否可用
     */
    public void setAddButtonEnable(boolean enabled) {
        this.addImage.setEnabled(enabled);
        this.addImage.setAlpha(enabled ? 1F : 0.3F);
    }

    /**
     * 设置减按钮是否可用
     */
    public void setReduceButtonEnable(boolean enabled) {
        this.reduceImage.setEnabled(enabled);
        this.reduceImage.setAlpha(enabled ? 1F : 0.3F);
    }

    /**
     * 设置按钮显示状态
     *
     * @param visibility 显示状态
     */
    public void setButtonVisibility(int visibility) {
        this.addImage.setVisibility(visibility);
        this.reduceImage.setVisibility(visibility);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.addImage.setEnabled(enabled);
        this.numEditText.setEnabled(enabled);
        this.reduceImage.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * 设置回调
     *
     * @param callback 回调
     */
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    /**
     * 设置回调
     *
     * @param callback 回调
     */
    public void setCallback(TriggerEventValidate triggerEventValidate, Callback callback) {
        this.mCallback = callback;
        this.triggerEventValidate = triggerEventValidate;
    }

    /**
     * 数量值EditText
     */
    public static class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

        private boolean isEditNum;//是否可编辑数量

        public CustomEditText(Context context) {
            super(context);
            this.init();
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && this.isEditNum && !this.isFocused()) {
                this.setEnabled(true);
//                numEditText.requestFocus();
                showSoftInput(this);
                this.setSelection(this.length());
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_UP && this.hasOnClickListeners()) {
                this.performClick();
                return true;
            }

            return super.dispatchTouchEvent(event);
        }

        private void init() {
            this.setOnFocusChangeListener(View::setEnabled);
        }

        public void setEditNum(boolean isEditNum) {
            this.isEditNum = isEditNum;
            this.setEnabled(false);
        }

        /**
         * 动态显示软键盘
         */
        public void showSoftInput(View view) {
            showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }

        /**
         * Show the soft input.
         *
         * @param view  The view.
         * @param flags Provides additional operating flags.  Currently may be
         *              0 or have the {@link InputMethodManager#SHOW_IMPLICIT} bit set.
         */
        public void showSoftInput(final View view, final int flags) {
            InputMethodManager imm =
                    (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) return;
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            imm.showSoftInput(view, flags, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN
                            || resultCode == InputMethodManager.RESULT_HIDDEN) {
                        toggleSoftInput();
                    }
                }
            });
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        /**
         * 切换键盘显示与否状态
         */
        public void toggleSoftInput() {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) return;
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /**
     * 事件触发验证
     */
    public interface TriggerEventValidate {

        /**
         * 是否属于用户触发事件
         */
        boolean isUserTriggerEvent();
    }

    public interface Callback {
        /**
         * 数量回调
         *
         * @param isReduce    是否为减，true：减,false：加
         * @param result      最终数量值
         * @param offsetValue 该次加或者减偏移量值
         */
        void onSelectNumber(boolean isReduce, String result, double offsetValue);

        /**
         * 验证减操作是否有效
         *
         * @param num 当前即将要减去的数量
         */
        default boolean validateReduce(double num) {
            return true;
        }

        /**
         * 验证加操作是否有效
         *
         * @param num 当前即将要加上的数量
         */
        default boolean validateAdd(double num) {
            return true;
        }
    }

}
