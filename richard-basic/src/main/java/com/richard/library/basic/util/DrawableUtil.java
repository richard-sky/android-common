package com.richard.library.basic.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;

/**
 * <pre>
 * Description : Drawable生成
 * Author : admin-richard
 * Date : 2018/6/20 11:34
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/6/20 11:34     admin-richard         new file.
 * </pre>
 */
public class DrawableUtil {

    /**
     * 生成selector
     *
     * @param stateAttr   动作属性
     * @param stateColor1 状态1颜色
     * @param stateColor2 状态2颜色
     * @param radius      圆角弧度
     * @return
     */
    public static StateListDrawable generatorSelector(@AttrRes int stateAttr, @ColorInt int stateColor1, @ColorInt int stateColor2, float radius) {
        return generatorSelector(stateAttr, stateColor1, stateColor2, radius, radius, radius, radius);
    }


    /**
     * 生成Drawable
     *
     * @param color  背景颜色
     * @param radius 圆角弧度
     * @return
     */
    public static GradientDrawable generatorGradientDrawable(@ColorInt int color, float radius) {
        return generatorGradientDrawable(color, radius, radius, radius, radius);
    }

    /**
     * 生成边框线Drawable
     *
     * @param borderColor 边框线颜色
     * @param borderWidth 边框线大小
     * @param radius      边框圆角弧度
     * @return
     */
    public static GradientDrawable generatorBorderDrawable(@ColorInt int borderColor, int borderWidth, float radius) {
        return generatorBorderDrawable(borderColor, borderWidth, radius, radius, radius, radius);
    }


    /**
     * 生成两个状态的Drawable
     *
     * @param stateAttr         动作属性
     * @param stateColor1       状态1颜色
     * @param stateColor2       状态2颜色
     * @param topLeftRadius     左上角弧度
     * @param topRightRadius    右上角弧度
     * @param bottomRightRadius 左下角弧度
     * @param bottomLeftRadius  右下角弧度
     */
    public static StateListDrawable generatorSelector(@AttrRes int stateAttr, @ColorInt int stateColor1, @ColorInt int stateColor2, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-stateAttr}, generatorGradientDrawable(stateColor1, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius));
        stateListDrawable.addState(new int[]{stateAttr}, generatorGradientDrawable(stateColor2, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius));

        return stateListDrawable;
    }


    /**
     * 生成未按下和按下后的状态的Drawable
     *
     * @param stateAttr         动作属性
     * @param backColor         背景颜色
     * @param borderLineSize    边框线大小
     * @param borderLineColor   边框线颜色
     * @param topLeftRadius     左上角圆角度
     * @param topRightRadius    右上角圆角度
     * @param bottomLeftRadius  左下角圆角度
     * @param bottomRightRadius 右上角圆角度
     */
    public static StateListDrawable generatorSelector(
            @AttrRes int stateAttr
            , int backColor
            , float borderLineSize
            , int borderLineColor
            , float topLeftRadius
            , float topRightRadius
            , float bottomLeftRadius
            , float bottomRightRadius
    ) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        //topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius
        float[] radius = new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius};

        //未按下的状态
        GradientDrawable unPressDrawable = generatorGradientDrawable(backColor, radius, borderLineSize, borderLineColor);

        //按下的状态
        GradientDrawable pressDrawable = generatorGradientDrawable(backColor, radius, borderLineSize, borderLineColor);
        pressDrawable.setAlpha(0xdf);

        stateListDrawable.addState(new int[]{-stateAttr}, unPressDrawable);
        stateListDrawable.addState(new int[]{stateAttr}, pressDrawable);

        return stateListDrawable;
    }

    /**
     * 生成shape
     *
     * @param backColor       背景颜色
     * @param radius          四个边角弧度
     * @param borderLineSize  边框大小
     * @param borderLineColor 边框颜色
     */
    public static GradientDrawable generatorGradientDrawable(int backColor, float radius, float borderLineSize, int borderLineColor) {
        float[] radii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        return generatorGradientDrawable(
                backColor
                , radii
                , borderLineSize
                , borderLineColor
        );
    }

    /**
     * 生成shape
     *
     * @param backColor 背景颜色
     * @param radius    四个边角弧度
     */
    public static GradientDrawable generatorGradientDrawable(int backColor, float[] radius, float borderLineSize, int borderLineColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(backColor);

        if (isRound(radius)) {
            gradientDrawable.setCornerRadius(radius[0]);
        } else {
            gradientDrawable.setCornerRadii(radius);
        }

        if (borderLineSize > 0) {
            gradientDrawable.setStroke((int) borderLineSize, borderLineColor);
        }
        return gradientDrawable;
    }


    /**
     * 生成单个Drawable
     *
     * @param color             背景颜色
     * @param topLeftRadius     左上角弧度
     * @param topRightRadius    右上角弧度
     * @param bottomRightRadius 左下角弧度
     * @param bottomLeftRadius  右下角弧度
     * @return
     */
    public static GradientDrawable generatorGradientDrawable(@ColorInt int color, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        //topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius
        float[] radii = new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius};

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);

        if (isRound(radii)) {
            gradientDrawable.setCornerRadius(topLeftRadius);
        } else {
            gradientDrawable.setCornerRadii(radii);
        }

        return gradientDrawable;
    }


    /**
     * 判断所有边角是否都为圆角
     *
     * @param radius 边角弧度值数组
     */
    public static boolean isRound(float[] radius) {
        int length = radius.length;
        float firstItem = radius[0];
        for (int i = 1; i < length; i++) {
            if (firstItem != radius[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 生成边框线
     *
     * @param borderColor       边框线颜色
     * @param borderWidth       边框线大小
     * @param topLeftRadius     左上角弧度
     * @param topRightRadius    右上角弧度
     * @param bottomRightRadius 左下角弧度
     * @param bottomLeftRadius  右下角弧度
     * @return
     */
    public static GradientDrawable generatorBorderDrawable(@ColorInt int borderColor, int borderWidth, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        //topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius
        float[] radii = new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius};

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(borderWidth, borderColor);

        if (isRound(radii)) {
            gradientDrawable.setCornerRadius(topLeftRadius);
        } else {
            gradientDrawable.setCornerRadii(radii);
        }

        return gradientDrawable;
    }


    /**
     * 生成字体颜色stateList
     *
     * @param stateAttr   动作属性
     * @param stateColor1 未选中颜色
     * @param stateColor2 选中的颜色
     * @return
     */
    public static ColorStateList generatorColorDrawable(@AttrRes int stateAttr, @ColorInt int stateColor1, @ColorInt int stateColor2) {
        int[][] states = new int[2][];
        states[0] = new int[]{-stateAttr};
        states[1] = new int[]{stateAttr};

        int[] colors = new int[]{stateColor1, stateColor2};

        return new ColorStateList(states, colors);
    }

}
