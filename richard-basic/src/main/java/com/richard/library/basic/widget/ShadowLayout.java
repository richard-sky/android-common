package com.richard.library.basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richard.library.basic.R;
import com.richard.library.context.util.DensityUtilKt;

/**
 * 带箭头的气泡和阴影效果的layout
 * 用法
 * <ShadowLayout
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * android:padding="16dp" //必须设置足够的padding才可以绘制三角形和阴影
 * app:background_color="#FF4081"    //背景颜色
 * app:direction="left"    //三角形方向
 * app:offset="-40dp"    //三角形相对偏移量 0默认居中
 * app:radius="4dp"    //圆角大小
 * app:shadow_color="#999999"    //阴影颜色
 * app:shadow_size="4dp">    //阴影大小
 * <p>
 * //你的气泡内的内容布局
 * <p>
 * </ShadowLayout>
 */
public class ShadowLayout extends FrameLayout {

    public static final int NONE = 0;
    public static final int LEFT = 1;
    public static final int TOP = 2;
    public static final int RIGHT = 3;
    public static final int BOTTOM = 4;

    @IntDef({NONE, LEFT, TOP, RIGHT, BOTTOM})
    public @interface Direction {
    }

    //三角形位置偏移量(默认居中)
    private int offset;
    private Paint borderPaint;
    private Path path;
    private RectF rect;
    //三角形的底边中心点
    private Point datumPoint;

    //圆角大小
    private int radius;
    //三角形的方向
    @Direction
    private int direction;
    //背景颜色
    private int backgroundColor;
    //阴影颜色
    private int shadowColor;
    //阴影尺寸
    private int shadowSize;
    //阴影X轴偏移量
    private int shadowDx;
    //阴影Y轴偏移量
    private int shadowDy;


    public ShadowLayout(@NonNull Context context) {
        super(context);
        this.init(context, null);
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        backgroundColor = Color.WHITE;
        shadowColor = getResources().getColor(R.color.shadow);
        shadowSize = DensityUtilKt.dp2px(4, getContext());
        radius = getResources().getDimensionPixelSize(R.dimen.radius_value);
        direction = NONE;
        offset = 0;
        shadowDx = 0;
        shadowDy = 0;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout);
            backgroundColor = ta.getColor(R.styleable.ShadowLayout_sl_background_color, backgroundColor);
            shadowColor = ta.getColor(R.styleable.ShadowLayout_sl_shadow_color, shadowColor);
            shadowSize = ta.getDimensionPixelSize(R.styleable.ShadowLayout_sl_shadow_size, shadowSize);
            radius = ta.getDimensionPixelSize(R.styleable.ShadowLayout_sl_radius, radius);
            shadowDx = ta.getDimensionPixelSize(R.styleable.ShadowLayout_sl_shadow_dx, shadowDx);
            shadowDy = ta.getDimensionPixelSize(R.styleable.ShadowLayout_sl_shadow_dy, shadowDy);
            direction = ta.getInt(R.styleable.ShadowLayout_sl_direction, NONE);
            offset = ta.getDimensionPixelOffset(R.styleable.ShadowLayout_sl_offset, 0);
            ta.recycle();
        }

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        this.updateParams();

        path = new Path();
        rect = new RectF();
        datumPoint = new Point();

        if (!isInEditMode()) {
            setWillNotDraw(false);
            //关闭硬件加速
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        if (getPaddingStart() <= 0
                && getPaddingLeft() <= 0
                && getPaddingTop() <= 0
                && getPaddingBottom() <= 0
                && getPaddingRight() <= 0
                && getPaddingEnd() <= 0) {
            int defaultPadding = DensityUtilKt.dp2px(10, getContext());
            setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);
        }
    }

    private void updateParams() {
        borderPaint.setColor(backgroundColor);
        borderPaint.setShadowLayer(shadowSize, shadowDx, shadowDy, shadowColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isInEditMode()) {
            return;
        }

        rect.left = getPaddingLeft();
        rect.top = getPaddingTop();
        rect.right = w - getPaddingRight();
        rect.bottom = h - getPaddingBottom();

        switch (direction) {
            case LEFT:
                datumPoint.x = getPaddingLeft();
                datumPoint.y = h / 2;
                break;
            case TOP:
                datumPoint.x = w / 2;
                datumPoint.y = getPaddingTop();
                break;
            case RIGHT:
                datumPoint.x = w - getPaddingRight();
                datumPoint.y = h / 2;
                break;
            case BOTTOM:
            default:
                datumPoint.x = w / 2;
                datumPoint.y = h - getPaddingBottom();
                break;
        }

        if (offset != 0) {
            applyOffset();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (datumPoint.x > 0 && datumPoint.y > 0)
            switch (direction) {
                case LEFT:
                    drawLeftTriangle(canvas);
                    break;
                case TOP:
                    drawTopTriangle(canvas);
                    break;
                case RIGHT:
                    drawRightTriangle(canvas);
                    break;
                case BOTTOM:
                    drawBottomTriangle(canvas);
                    break;
                case NONE:
                default:
                    drawNone(canvas);
            }
    }

    private void drawLeftTriangle(Canvas canvas) {
        int triangularLength = getPaddingLeft();
        if (triangularLength == 0) {
            return;
        }

        path.addRoundRect(rect, radius, radius, Path.Direction.CCW);
        path.moveTo(datumPoint.x, datumPoint.y - triangularLength / 2F);
        path.lineTo(datumPoint.x - triangularLength / 2F, datumPoint.y);
        path.lineTo(datumPoint.x, datumPoint.y + triangularLength / 2F);
        path.close();
        canvas.drawPath(path, borderPaint);
    }

    private void drawNone(Canvas canvas) {
        path.addRoundRect(rect, radius, radius, Path.Direction.CCW);
        path.close();
        canvas.drawPath(path, borderPaint);
    }

    private void drawTopTriangle(Canvas canvas) {
        int triangularLength = getPaddingTop();
        if (triangularLength == 0) {
            return;
        }

        path.addRoundRect(rect, radius, radius, Path.Direction.CCW);
        path.moveTo(datumPoint.x + triangularLength / 2F, datumPoint.y);
        path.lineTo(datumPoint.x, datumPoint.y - triangularLength / 2F);
        path.lineTo(datumPoint.x - triangularLength / 2F, datumPoint.y);
        path.close();
        canvas.drawPath(path, borderPaint);
    }

    private void drawRightTriangle(Canvas canvas) {
        int triangularLength = getPaddingRight();
        if (triangularLength == 0) {
            return;
        }

        path.addRoundRect(rect, radius, radius, Path.Direction.CCW);
        path.moveTo(datumPoint.x, datumPoint.y - triangularLength / 2F);
        path.lineTo(datumPoint.x + triangularLength / 2F, datumPoint.y);
        path.lineTo(datumPoint.x, datumPoint.y + triangularLength / 2F);
        path.close();
        canvas.drawPath(path, borderPaint);
    }

    private void drawBottomTriangle(Canvas canvas) {
        int triangularLength = getPaddingBottom();
        if (triangularLength == 0) {
            return;
        }

        path.addRoundRect(rect, radius, radius, Path.Direction.CCW);
        path.moveTo(datumPoint.x + triangularLength / 2F, datumPoint.y);
        path.lineTo(datumPoint.x, datumPoint.y + triangularLength / 2F);
        path.lineTo(datumPoint.x - triangularLength / 2F, datumPoint.y);
        path.close();
        canvas.drawPath(path, borderPaint);
    }

    private void applyOffset() {
        switch (direction) {
            case LEFT:
            case RIGHT:
                datumPoint.y += offset;
                break;
            case TOP:
            case BOTTOM:
            default:
                datumPoint.x += offset;
                break;
        }
    }

    //---------------------------------------------------------------------------------------------

    /**
     * 通知更新生效设置得属性
     */
    public void notifyUpdateAttrs() {
        this.updateParams();
        applyOffset();
        invalidate();
    }

    /**
     * 设置阴影Y轴偏移量
     */
    public void setShadowDy(int shadowDy) {
        this.shadowDy = shadowDy;
    }

    /**
     * 阴影X轴偏移量
     */
    public void setShadowDx(int shadowDx) {
        this.shadowDx = shadowDx;
    }

    /**
     * 设置阴影尺寸
     */
    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
    }

    /**
     * 设置阴影颜色
     */
    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    /**
     * 设置背景颜色
     */
    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * 设置圆角大小（px）
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * 设置三角形的方向
     */
    public void setDirection(@Direction int direction) {
        this.direction = direction;
    }

    /**
     * 设置三角形偏移位置
     *
     * @param offset 偏移量
     */
    public void setTriangleOffset(int offset) {
        this.offset = offset;
    }


}
