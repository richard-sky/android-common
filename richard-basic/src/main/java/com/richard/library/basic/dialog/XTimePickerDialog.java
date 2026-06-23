package com.richard.library.basic.dialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import com.richard.library.context.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 * Description : 系统时间选择框
 * Author : admin-richard
 * Date : 2019-07-15 17:02
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-07-15 17:02      admin-richard         new file.
 * </pre>
 */
public class XTimePickerDialog extends TimePickerDialog {


    public XTimePickerDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
    }

    public XTimePickerDialog(Context context, int themeResId, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, themeResId, listener, hourOfDay, minute, is24HourView);
    }

    public static class Builder {

        private long time;//当前选择日期时间戳
        private Callback mCallback;//选择时间后结果回调
        private String dateFormat = "HH:mm:ss";//选择时间后回调的时间的格式

        public Builder setTime(long time) {
            this.time = time;
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

        public XTimePickerDialog build(Context context) {
            if (time <= 0) {
                time = System.currentTimeMillis();
            }

            return new XTimePickerDialog(
                    context
                    , mOnDateSetListener
                    , DateUtil.getHourFor24(new Date(time))
                    , DateUtil.getMin(new Date(time))
                    , true
            );
        }

        private final OnTimeSetListener mOnDateSetListener = new OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (mCallback == null) {
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);

                mCallback.onSelectedTime(
                        calendar.getTimeInMillis()
                        , DateUtil.formatTimeStamp(dateFormat,calendar.getTimeInMillis())
                );
            }
        };
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
