package com.richard.library.basic.widget.webview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.richard.library.basic.R;
import com.richard.library.context.util.DensityUtilKt;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


/**
 * web 进度条
 */
public class ProgressBarView extends FrameLayout {

    private int mColor;
    private Paint mPaint;
    private ValueAnimator mValueAnimator;
    private int targetWidth = 0;

    public ProgressBarView(Context context) {
        this(context, null);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint();
        mColor = ContextCompat.getColor(getContext(), R.color.blue);

        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);

        this.post(new Runnable() {
            @Override
            public void run() {
                targetWidth = getMeasuredWidth();
            }
        });
    }

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
    }

    public void setColor(String color) {
        this.setColor(Color.parseColor(color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == MeasureSpec.AT_MOST) {
            w = Math.min(w, targetWidth);
        }
        if (hMode == MeasureSpec.AT_MOST) {
            h = DensityUtilKt.dp2px(2,getContext());
        }
        this.setMeasuredDimension(w, h);

    }

    private float currentProgress = 0f;

    @Override
    protected void onDraw(Canvas canvas) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawRect(0, 0, currentProgress / 100 * (float) this.getWidth(), this.getHeight(), mPaint);
    }

    public void show() {
        if (getVisibility() == View.GONE) {
            this.setVisibility(View.VISIBLE);
            currentProgress = 0f;
            startAnim(-1, true);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.targetWidth = getMeasuredWidth();
    }

    public void setProgress(float progress) {
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        }
        /*if (progress < 90f)
            return;*/
        startAnim(progress, false);
    }

    public void hide() {
        TAG = FINISH;
    }

    private int TAG = 0;

    public static final int UN_START = 0;
    public static final int STARTED = 1;
    public static final int FINISH = 2;


    private float target = 0f;

    private float weightDuration(float value, float current) {
        if (value > 70 && value < 85) {
            return 1.5f;
        } else if (value > 85) {
            return 0.8f;
        }
        return small(value, current);
    }

    private float small(float value, float current) {
        float poor = Math.abs(value - current);
        if (poor < 25) {
            return 4f;
        } else if (poor > 25 && poor < 50) {
            return 3f;
        } else {
            return 2f;
        }
    }

    private void startAnim(float value, boolean isAuto) {
        if (target == value)
            return;
        if (value < currentProgress && value != -1)
            return;

        float v = (isAuto) ? 90f : value;
        if (mValueAnimator != null && mValueAnimator.isStarted()) {
            mValueAnimator.cancel();
        }
        currentProgress = currentProgress == 0f ? 0.00000001f : currentProgress;
        mValueAnimator = ValueAnimator.ofFloat(currentProgress, v);
        mValueAnimator.setInterpolator(new LinearInterpolator());

        long duration = (long) Math.abs((v / 100f * targetWidth) - (currentProgress / 100f * targetWidth));


        /*默认每个像素8毫秒*/
        mValueAnimator.setDuration(isAuto ? duration * 4 : (long) (duration * weightDuration(v, currentProgress)));
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener);
        mValueAnimator.addListener(mAnimatorListenerAdapter);
        mValueAnimator.start();
        TAG = STARTED;
        target = v;

    }

    private final ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float t = (float) animation.getAnimatedValue();
            ProgressBarView.this.currentProgress = t;
            ProgressBarView.this.invalidate();

        }
    };

    private final AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            doEnd();
        }
    };

    private void doEnd() {
        if (TAG == FINISH && currentProgress == 100f) {
            setVisibility(GONE);
            currentProgress = 0f;
        }
        TAG = UN_START;
    }

    public void reset() {
        currentProgress = 0;
        if (mValueAnimator != null && mValueAnimator.isStarted())
            mValueAnimator.cancel();
    }

    public void setProgress(int newProgress) {
        setProgress(Float.valueOf(newProgress));

    }


    public LayoutParams offerLayoutParams() {
        return new LayoutParams(-1, DensityUtilKt.dp2px(2,getContext()));
    }
}
