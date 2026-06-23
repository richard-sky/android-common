package com.richard.library.basic.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.richard.library.context.util.DateUtil;

import java.text.DecimalFormat;

/**
 * <pre>
 * Description : 系统日期选择框
 * Author : admin-richard
 * Date : 2019-07-15 17:02
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-07-15 17:02      admin-richard         new file.
 * </pre>
 */
public class XDatePickerDialog extends DatePickerDialog {


    @RequiresApi(api = Build.VERSION_CODES.N)
    public XDatePickerDialog(@NonNull Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public XDatePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public XDatePickerDialog(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    public XDatePickerDialog(@NonNull Context context, int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
    }

    public static class Builder {

        private long time;//当前选择日期时间戳
        private long minTime;//最小能选择的时间戳
        private long maxTime;//最大能选择的时间戳
        private Callback mCallback;//选择时间后结果回调
        private String dateFormat = "yyyy-MM-dd";//选择时间后回调的时间的格式

        public Builder setTime(long time) {
            this.time = time;
            return this;
        }

        public Builder setMinTime(long minTime) {
            this.minTime = minTime;
            return this;
        }

        public Builder setMaxTime(long maxTime) {
            this.maxTime = maxTime;
            return this;
        }

        public Builder setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public Builder setCallback(Callback callback) {
            mCallback = callback;
            return this;
        }

        public XDatePickerDialog build(Context context) {
            if (time <= 0) {
                time = System.currentTimeMillis();
            }

            XDatePickerDialog xDatePickerDialog = new XDatePickerDialog(
                    context
                    , mOnDateSetListener
                    , DateUtil.getYear(time)
                    , DateUtil.getMonth(time) - 1
                    , DateUtil.getDay(time)
            );

            if (minTime > 0) {
                xDatePickerDialog.getDatePicker().setMinDate(minTime);
            }

            if (maxTime > 0) {
                xDatePickerDialog.getDatePicker().setMaxDate(maxTime);
            }

            return xDatePickerDialog;
        }

        private final OnDateSetListener mOnDateSetListener = new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (mCallback != null) {
                    String dateStr = formatNum(year)
                            .concat("-")
                            .concat(formatNum(month + 1))
                            .concat("-")
                            .concat(formatNum(dayOfMonth));

                    mCallback.onSelectedTime(
                            DateUtil.getMillis(dateStr)
                            , DateUtil.convertFormat(dateStr, "yyyy-MM-dd", dateFormat)
                    );
                }
            }
        };


        /**
         * 格式化数字格式
         *
         * @param num 数字
         */
        private String formatNum(Object num) {
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.applyPattern("00");
            return decimalFormat.format(num);
        }
    }

    public interface Callback {
        /**
         * 选择之后的时间
         *
         * @param dateTime   时间戳
         * @param formatDate 格式化之后的日期(yyyy-MM-dd)
         */
        void onSelectedTime(long dateTime, String formatDate);
    }
}
