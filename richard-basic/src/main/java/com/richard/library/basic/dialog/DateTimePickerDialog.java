package com.richard.library.basic.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.BasicDialog;
import com.richard.library.context.util.DateUtil;
import com.richard.library.basic.widget.NavigationBar;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.StringUtilKt;

import java.util.Calendar;
import java.util.Date;

/**
 * @author: Richard
 * @createDate: 2023/7/20 10:26
 * @version: 1.0
 * @description: 日期时间选择
 */
public class DateTimePickerDialog extends BasicDialog {

    private NavigationBar navigationBar;
    private DatePicker datePicker;
    private DatePicker dateEndPicker;
    private TimePicker timePicker;
    private Builder builder;

    private DateTimePickerDialog(Context context) {
        super(context);
    }

    private DateTimePickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void initLayoutView() {

    }

    @Override
    public void initData() {
        switch (builder.pickerType) {
            case PickerType.DATE:
                setContentView(R.layout.layout_date_picker);
                datePicker = findViewById(R.id.date_picker);
                break;
            case PickerType.TIME:
                setContentView(R.layout.layout_time_picker);
                timePicker = findViewById(R.id.time_picker);
                timePicker.setIs24HourView(true);
                if (AppContext.isScreenPortrait()) {
                    setMarginLeftRight(20);
                } else {
                    setWidth(300);
                }
                break;
            case PickerType.DATE_TIME:
                setContentView(R.layout.layout_date_time_picker);
                datePicker = findViewById(R.id.date_picker);
                timePicker = findViewById(R.id.time_picker);
                timePicker.setIs24HourView(true);

                LinearLayout layout = findViewById(R.id.content_root);
                if (AppContext.isScreenPortrait()) {
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                }
                break;
            case PickerType.DATE_BETWEEN:
                setContentView(R.layout.layout_date_between_picker);
                datePicker = findViewById(R.id.date_picker);
                dateEndPicker = findViewById(R.id.date_picker_end);
                ((LinearLayout) findViewById(R.id.content_root)).setOrientation(AppContext.isScreenPortrait()
                        ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
                break;
        }

        navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setTitle(StringUtilKt.defaultIfEmpty(builder.title, "时间选择"));

        if (builder.time == null || builder.time.length == 0) {
            builder.time = new long[]{System.currentTimeMillis(), System.currentTimeMillis()};
        } else if (builder.time.length < 2) {
            builder.time = new long[]{builder.time[0], builder.time[0]};
        }

        if (datePicker != null) {
            datePicker.setVisibility(View.VISIBLE);
            datePicker.init(
                    DateUtil.getYear(builder.time[0])
                    , DateUtil.getMonth(builder.time[0]) - 1
                    , DateUtil.getDay(builder.time[0])
                    , null
            );

            if (builder.minTime > 0) {
                datePicker.setMinDate(builder.minTime);
            }

            if (builder.maxTime > 0) {
                datePicker.setMaxDate(builder.maxTime);
            }
        }

        if (dateEndPicker != null) {
            dateEndPicker.setVisibility(View.VISIBLE);
            dateEndPicker.init(
                    DateUtil.getYear(builder.time[1])
                    , DateUtil.getMonth(builder.time[1]) - 1
                    , DateUtil.getDay(builder.time[1])
                    , null
            );

            if (builder.minTime > 0) {
                dateEndPicker.setMinDate(builder.minTime);
            }

            if (builder.maxTime > 0) {
                dateEndPicker.setMaxDate(builder.maxTime);
            }
        }

        if (timePicker != null) {
            timePicker.setVisibility(View.VISIBLE);
            timePicker.setCurrentHour(DateUtil.getHourFor24(new Date(builder.time[0])));
            timePicker.setCurrentMinute(DateUtil.getMin(new Date(builder.time[0])));
        }
    }

    @Override
    public void bindListener() {
        navigationBar.setLeftTextViewClickListener((v) -> {
            dismiss();
        });

        navigationBar.setRightTextViewClickListener((v) -> {
            long startTimeMill = 0;
            long endTimeMill = 0;

            switch (builder.pickerType) {
                case PickerType.DATE:
                    startTimeMill = getTimeMill(datePicker, null);
                    endTimeMill = startTimeMill;
                    break;
                case PickerType.TIME:
                    startTimeMill = getTimeMill(null, timePicker);
                    endTimeMill = startTimeMill;
                    break;
                case PickerType.DATE_TIME:
                    startTimeMill = getTimeMill(datePicker, timePicker);
                    endTimeMill = startTimeMill;
                    break;
                case PickerType.DATE_BETWEEN:
                    startTimeMill = getTimeMill(datePicker, null);
                    endTimeMill = getTimeMill(dateEndPicker, null);
                    break;
                default:
                    getUIView().showMsg("不支持当前设置的PickerType");
                    return;
            }

            if (startTimeMill > endTimeMill) {
                getUIView().showMsg("开始日期不能大于结束日期");
                return;
            }

            dismiss();
            if (builder.mCallback == null) {
                return;
            }

            builder.mCallback.onSelectTime(
                    startTimeMill
                    , DateUtil.formatTimeStamp(builder.dateFormat, startTimeMill)
                    , endTimeMill
                    , DateUtil.formatTimeStamp(builder.dateFormat, endTimeMill)
            );
        });
    }

    /**
     * 获取选择的时间戳
     */
    private long getTimeMill(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();

        if (datePicker != null) {
            calendar.set(Calendar.YEAR, datePicker.getYear());
            calendar.set(Calendar.MONTH, datePicker.getMonth());
            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        }

        if (timePicker != null) {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        }

        return calendar.getTimeInMillis();
    }

    private void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public static class Builder {

        private String title;//dialog 标题
        private long[] time;//当前选择日期时间戳,若pickerType是DATE_BETWEEN，则[0]:是指定的开始时间,[1]:是指定的结束时间
        private long minTime;//最小能选择的时间戳
        private long maxTime;//最大能选择的时间戳
        private Callback mCallback;//选择时间后结果回调
        private String dateFormat = "yyyy-MM-dd";//选择时间后回调的时间的格式
        private int pickerType = PickerType.DATE;//选择类型

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTime(long... time) {
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

        public Builder setPickerType(int pickerType) {
            this.pickerType = pickerType;
            return this;
        }

        public Builder setCallback(Callback callback) {
            mCallback = callback;
            return this;
        }

        public DateTimePickerDialog build(Context context) {
            DateTimePickerDialog dialog = new DateTimePickerDialog(context);
            dialog.setBuilder(this);
            return dialog;
        }
    }

    /**
     * 选择日期或时间类型
     */
    public interface PickerType {
        int DATE = 0;
        int TIME = 1;
        int DATE_TIME = 2;
        int DATE_BETWEEN = 3;
    }

    public interface Callback {

        /**
         * 选择时间回调
         *
         * @param startTime       开始时间戳
         * @param startFormatTime 开始时间（格式化之后）
         * @param endTime         结束时间戳(未选择结束时间时该值同startTime)
         * @param endFormatTime   结束时间（格式化之后）(未选择结束时间时该值同startFormatTime)
         */
        void onSelectTime(long startTime, String startFormatTime, long endTime, String endFormatTime);
    }
}
