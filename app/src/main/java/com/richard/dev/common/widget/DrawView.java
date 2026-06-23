package com.richard.dev.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.richard.library.context.util.DensityUtilKt;

/**
 * @author: Administrator
 * @createDate: 2022/4/21 14:26
 * @version: 1.0
 * @description: 描述
 */
public class DrawView extends View {

    private Paint paint;

    public DrawView(Context context) {
        super(context);
        this.init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#008577"));
        paint.setStrokeWidth(DensityUtilKt.dp2px(2));
        paint.setTextSize(DensityUtilKt.sp2px(25));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制纯色背景
//        canvas.drawARGB(255,150,100,100);
//        canvas.drawPaint(paint);
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.DARKEN);

//        //绘制弧形
//        canvas.drawArc(20, 20, 300, 300, 0, 270, true, paint);
//        RectF oval = new RectF(320, 20, 600, 300);
//        canvas.drawArc(oval, 90, 320, true, paint);
//
//        //绘制bitmap
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.hudie);
////        canvas.drawBitmap(bitmap, 20, 320, paint);
//
//        Matrix matrix = new Matrix();
//        matrix.setScale(0.6F,0.6F,1600,20);
//        canvas.drawBitmap(bitmap,matrix,paint);
//
//        Rect srcRect = new Rect(0,0,300,300);
//        Rect dstRect = new Rect(0,0,600,600);
//        canvas.drawBitmap(bitmap,srcRect,dstRect,paint);
//
//        //绘制圆形
//        canvas.drawCircle(150,450,130,paint);
//
//        //绘制线条
//        canvas.drawLine(300,400,600,600,paint);
//
//        //批量绘制线条，要绘制的点的数组，如[x0,y0,x1,y1,x2,y2...]
//        canvas.drawLines(new float[]{700,100,1400,200, 700,300,700,400},paint);

        //绘制椭圆
//        canvas.drawOval(20,20,300,200,paint);

        //可控制绘制方向的绘制
        Path path = new Path();
        path.moveTo(300, 200);
        path.lineTo(100, 600);
        path.lineTo(500, 600);
        path.close();
        canvas.drawPath(path, paint);

        //绘制点
//        canvas.drawPoint(20,20,paint);
//        canvas.drawPoints(new float[]{700,100,1400,200, 700,300,700,400},paint);

        //绘制文字
//        canvas.drawText("绘制",20,100,paint);

    }
}
