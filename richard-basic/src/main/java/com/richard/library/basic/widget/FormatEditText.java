package com.richard.library.basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatEditText;

import com.richard.library.basic.R;
import com.richard.library.context.util.DensityUtilKt;

/**
 * <pre>
 * Description : 可格式化的EditText
 * Author : admin-richard
 * Date : 2019-05-16 16:23
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-16 16:23     admin-richard         new file.
 * </pre>
 */
public class FormatEditText extends AppCompatEditText implements TextWatcher {

    private boolean shouldStopChange = false;

    //字体大小
    private float inputTextSize;
    //hint字体大小
    private float hintTextSize;
    //文本最大长度
    private int maxLength;
    //分隔数量(每达到几个字符就分隔)
    private int splitNum;
    //分隔符
    private String splitFlag;

    public FormatEditText(Context context) {
        super(context);
        initView(null);
    }

    public FormatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public FormatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }


    private void initView(AttributeSet attrs) {
        inputTextSize = DensityUtilKt.sp2px(17, getContext());
        hintTextSize = DensityUtilKt.sp2px(15, getContext());
        splitNum = 0;
        splitFlag = "";

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FormatEditText);
            inputTextSize = (typedArray.getDimension(R.styleable.FormatEditText_fet_inputTextSize, inputTextSize));
            hintTextSize = (typedArray.getDimension(R.styleable.FormatEditText_fet_hintTextSize, hintTextSize));
            maxLength = typedArray.getInteger(R.styleable.FormatEditText_fet_maxLength, -1);
            splitNum = typedArray.getInteger(R.styleable.FormatEditText_fet_splitNum, splitNum);
            splitFlag = typedArray.getString(R.styleable.FormatEditText_fet_splitFlag);

            //重新设置maxLength
            if (maxLength >= 0) {
                maxLength = TextUtils.isEmpty(splitFlag) ? maxLength : (int) (maxLength + Math.floor(maxLength / splitNum));
                setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            } else {
                setFilters(new InputFilter[0]);
            }

            typedArray.recycle();
            typedArray = null;
        }

        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintTextSize);
        this.format(super.getText());
    }

    @Override
    protected void onAttachedToWindow() {
        this.addTextChangedListener(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        this.removeTextChangedListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintTextSize);
        } else {
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, inputTextSize);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        this.format(s);
    }


    private void format(Editable editable) {
        if (shouldStopChange) {
            shouldStopChange = false;
            return;
        }

        if (splitNum <= 0 || TextUtils.isEmpty(splitFlag)) {
            return;
        }

        shouldStopChange = true;

        String str = editable.toString().trim().replaceAll(splitFlag, "");
        int len = str.length();
        int courPos;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(str.charAt(i));
            if (i != len - 1 && (i + 1) % splitNum == 0) {
                builder.append(splitFlag);
            }
        }
        courPos = builder.length();
        setText(builder.toString());
        setSelection(courPos);
    }


    /**
     * 获取去掉分隔符后字符串
     */
    public String getOriginText() {
        if (TextUtils.isEmpty(splitFlag)) {
            return super.getText().toString();
        }
        return super.getText().toString().replaceAll(splitFlag, "");
    }

}

