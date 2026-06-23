package com.richard.dev.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.richard.library.context.AppContext;
import com.richard.library.context.util.DensityUtilKt;

/**
 * @author: Administrator
 * @createDate: 2022/4/22 11:08
 * @version: 1.0
 * @description: 描述
 */
public class LineChartView extends View {

    private int chartMargin;
    private int chartWidth;
    private int chartHeight;

    private Paint linePaint;
    private Paint textPaint;
    private Path path;

    public LineChartView(Context context) {
        super(context);
        this.init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private void init() {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.parseColor("#008577"));
        linePaint.setStrokeWidth(DensityUtilKt.dp2px(2));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#008577"));
        textPaint.setStrokeWidth(DensityUtilKt.dp2px(2));
        textPaint.setTextSize(DensityUtilKt.sp2px(14));

        path = new Path();
        chartMargin = DensityUtilKt.dp2px(20);
        chartWidth = AppContext.getScreenWidth() - (chartMargin * 2);
        chartHeight = DensityUtilKt.dp2px(300);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path.moveTo(chartMargin, chartMargin);
        path.rLineTo(0, chartHeight);
        path.rLineTo(chartWidth, 0);

        canvas.drawPath(path, linePaint);

    }
}
