package com.richard.library.basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.richard.library.basic.R;

/**
 * <pre>
 * Description : 倒计时按钮（短信验证码）
 * Author : admin-richard
 * Date : 2017/8/4 11:58
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/8/4 11:58     admin-richard         new file.
 * </pre>
 */
public class CountDownTimerButton extends AppCompatTextView {

    private CountDownTimer timer;

    //按钮原始文本
    private String originalText;
    //开始跑秒后的文本模板
    private String runningText;
    //停止跑秒后的文本
    private String stopText;
    //倒计时秒数
    private int secondValue;


    public CountDownTimerButton(Context context) {
        super(context);
        init(null);
    }

    public CountDownTimerButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CountDownTimerButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownTimerButton);
            originalText = typedArray.getString(R.styleable.CountDownTimerButton_cdtb_original_text);
            runningText = typedArray.getString(R.styleable.CountDownTimerButton_cdtb_running_text);
            stopText = typedArray.getString(R.styleable.CountDownTimerButton_cdtb_stop_text);
            secondValue = typedArray.getInt(R.styleable.CountDownTimerButton_cdtb_second_value, 60);
            typedArray.recycle();
            typedArray = null;
        }

        originalText = TextUtils.isEmpty(originalText) ? "获取验证码" : originalText;
        runningText = TextUtils.isEmpty(runningText) ? "已发送(%d)" : runningText;
        if (!(!TextUtils.isEmpty(runningText) && runningText.contains("%d"))) {
            runningText = runningText.concat("(%d)");
        }

        stopText = TextUtils.isEmpty(stopText) ? "重新发送" : stopText;
        secondValue = secondValue == 0 ? 60 : secondValue;

        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setText(originalText);
        this.setGravity(Gravity.CENTER);

        //初始化计时器
        timer = new CountDownTimer(secondValue * 1000L, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                setText(String.format(runningText, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                setEnabled(true);
                setText(stopText);
            }
        };
    }

    /**
     * 开始倒计时
     */
    public void startTimer() {
        if (timer != null && isEnabled()) {
            setEnabled(false);
            timer.start();
        }
    }

    /**
     * 停止倒计时
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopTimer();
        super.onDetachedFromWindow();
    }
}
