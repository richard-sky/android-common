package com.richard.library.context.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * <pre>
 * Description : 日期工具类
 * Author : admin-richard
 * Date : 2016/5/12 16:27
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2016/5/12 16:27     admin-richard         new file.
 * </pre>
 * <p>
 * HH:mm    15:44
 * h:mm a    3:44 下午
 * HH:mm z    15:44 CST
 * HH:mm Z    15:44 +0800
 * HH:mm zzzz    15:44 中国标准时间
 * HH:mm:ss    15:44:40
 * yyyy-MM-dd    2016-08-12
 * yyyy-MM-dd HH:mm    2016-08-12 15:44
 * yyyy-MM-dd HH:mm:ss    2016-08-12 15:44:40
 * yyyy-MM-dd HH:mm:ss zzzz    2016-08-12 15:44:40 中国标准时间
 * EEEE yyyy-MM-dd HH:mm:ss zzzz    星期五 2016-08-12 15:44:40 中国标准时间
 * yyyy-MM-dd HH:mm:ss.SSSZ    2016-08-12 15:44:40.461+0800
 * yyyy-MM-dd'T'HH:mm:ss.SSSZ    2016-08-12T15:44:40.461+0800
 * yyyy.MM.dd G 'at' HH:mm:ss z    2016.08.12 公元 at 15:44:40 CST
 * K:mm a    3:44 下午
 * EEE, MMM d, ''yy    星期五, 八月 12, '16
 * hh 'o''clock' a, zzzz    03 o'clock 下午, 中国标准时间
 * yyyyy.MMMMM.dd GGG hh:mm aaa    02016.八月.12 公元 03:44 下午
 * EEE, d MMM yyyy HH:mm:ss Z    星期五, 12 八月 2016 15:44:40 +0800
 * yyMMddHHmmssZ    160812154440+0800
 * yyyy-MM-dd'T'HH:mm:ss.SSSZ    2016-08-12T15:44:40.461+0800
 * EEEE 'DATE('yyyy-MM-dd')' 'TIME_NOT_VALID('HH:mm:ss')' zzzz    星期五 DATE(2016-08-12) TIME_NOT_VALID(15:44:40) 中国标准时间
 */
public final class DateUtil {

    //默认系统时区id
    private static TimeZone defaultTimeZone = TimeZone.getTimeZone("Asia/Shanghai");

    private static final ThreadLocal<Map<String, SimpleDateFormat>> SDF_THREAD_LOCAL =
            new ThreadLocal<Map<String, SimpleDateFormat>>() {
                @Override
                protected Map<String, SimpleDateFormat> initialValue() {
                    return new HashMap<>();
                }
            };

    public static void setDefaultTimeZone(@NonNull TimeZone timeZone) {
        DateUtil.defaultTimeZone = timeZone;
    }

    public static SimpleDateFormat getSafeDateFormat(String pattern) {
        return getSafeDateFormat(pattern, defaultTimeZone);
    }

    public static SimpleDateFormat getSafeDateFormat(String pattern, TimeZone timeZone) {
        return getSafeDateFormat(pattern, null, timeZone);
    }

    public static SimpleDateFormat getSafeDateFormat(String pattern, Locale locale) {
        return getSafeDateFormat(pattern, locale, defaultTimeZone);
    }

    public static SimpleDateFormat getSafeDateFormat(String pattern, Locale locale, TimeZone timeZone) {
        locale = locale == null ? Locale.getDefault() : locale;
        timeZone = timeZone == null ? defaultTimeZone : timeZone;

        Map<String, SimpleDateFormat> sdfMap = SDF_THREAD_LOCAL.get();
        String key = String.format("%s-%s-%s", locale.getLanguage(), timeZone.getID(), pattern);

        SimpleDateFormat simpleDateFormat = sdfMap.get(key);
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(pattern, locale);
            simpleDateFormat.setTimeZone(timeZone);
            sdfMap.put(key, simpleDateFormat);
        }
        return simpleDateFormat;
    }

    /**
     * 获取默认格式的日期格式SimpleDateFormat
     */
    private static SimpleDateFormat getDefaultFormat() {
        return getSafeDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Return whether it is leap year.
     *
     * @param year The year.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isLeapYear(final int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * 获取当前年月日时分秒（yyyy-MM-dd HH:mm:ss）
     */
    public static String getCurrentYMDHMS() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前年月日（yyyy-MM-dd）
     */
    public static String getCurrentYMD() {
        return getCurrentTime("yyyy-MM-dd");
    }

    /**
     * 获取当前时分秒（HH:mm:ss）
     */
    public static String getCurrentHMS() {
        return getCurrentTime("HH:mm:ss");
    }

    /**
     * 把符合日期格式的字符串转换为日期类型
     *
     * @param dateStr 标准日期串
     * @return Date
     */
    public static Date stringToDate(String dateStr, String format) {
        try {
            return getSafeDateFormat(format).parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取某年某月的天数
     *
     * @param year  int
     * @param month int 月份[1-12]
     * @return int
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回日期的年
     *
     * @param dateStr Date
     * @return int
     */
    public static String getYear(String dateStr) {
        return formatDateStr("yyyy", dateStr);
    }

    /**
     * 返回日期的年
     *
     * @param timeStamp Date
     * @return int
     */
    public static int getYear(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 返回日期的月份，1-12
     *
     * @param dateStr Date
     * @return int
     */
    public static String getMonth(String dateStr) {
        return formatDateStr("MM", dateStr);
    }

    /**
     * 返回日期的月份，1-12
     *
     * @param timeStamp Date
     * @return int
     */
    public static int getMonth(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 返回日期的日（几号）
     *
     * @param timeStamp Date
     * @return int
     */
    public static int getDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得当前年份
     *
     * @return int
     */
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获得当前月份
     *
     * @return int
     */
    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获得今天是几号
     *
     * @return int
     */
    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当天是星期几
     */
    public static int getCurrentWeek() {
        return getWeekInt(getCurrentTime());
    }

    /**
     * 获取指定时间的小时（12小时制）
     */
    public static int getHourFor12(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR);
    }

    /**
     * 获取指定时间的小时（24小时制）
     */
    public static int getHourFor24(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取指定时间的分
     */
    public static int getMin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }


    /**
     * 获取前N天或者后N天的时间戳
     *
     * @param amount 负数为前N天，正数为后n天
     * @return
     */
    public static long getDayTimeStamp(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTimeInMillis();
    }


    /**
     * 获取指定时间的前n天或者后n天
     *
     * @param timeStamp 指定时间
     * @param amount    负数为前N天，正数为后n天
     * @return
     */
    public static long getDayTimeStamp(long timeStamp, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTimeInMillis();
    }

    /**
     * 计算前一天和后一天的日期
     *
     * @param dateStr 当前选择的日期(格式必须得是yyyy-MM-dd)
     * @param amount  负数为前N天，正数为后n天
     */
    public static String getDay(String dateStr, int amount) {
        return formatTimeStamp("yyyy-MM-dd", getDayTimeStamp(getMillis(dateStr), amount));
    }

    /**
     * 获取一天的开始时间
     *
     * @param dateStr 需转换的日期串，格式：yyyy-MM-dd
     * @return 返回格式：yyyy-MM-dd 00:00:00
     */
    public static String convertDayStartDate(String dateStr) {
        return DateUtil.convertFormat(dateStr, "yyyy-MM-dd", "yyyy-MM-dd 00:00:00");
    }

    /**
     * 获取一天的结束时间
     *
     * @param dateStr 需转换的日期串，格式：yyyy-MM-dd
     * @return 返回格式：yyyy-MM-dd 23:59:59
     */
    public static String convertDayEndDate(String dateStr) {
        return DateUtil.convertFormat(dateStr, "yyyy-MM-dd", "yyyy-MM-dd 23:59:59");
    }

    /**
     * 获取指定时间的星期几(根据西方国家习惯,周日是一周的第一天)
     *
     * @return (0 - 6)
     */
    public static int getWeekIntByTheWest(long timeStamp) {
        int week = getWeekInt(timeStamp);
        return week == 7 ? 0 : week;
    }

    /**
     * 获取指定时间的星期几(一周是周一开始)
     *
     * @return (1 - 7)
     */
    public static int getWeekInt(long timeStamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        int weekNum = cal.get(Calendar.DAY_OF_WEEK);
        switch (weekNum) {
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            case Calendar.SUNDAY:
                return 7;
            default:
                return -1;
        }
    }

    /**
     * 获取指定时间星期几字符串
     */
    public static String getWeek(long timeStamp) {
        String weekStr = "";
        int week = getWeekInt(timeStamp);
        switch (week) {
            case 1:
                weekStr = "周一";
                break;
            case 2:
                weekStr = "周二";
                break;
            case 3:
                weekStr = "周三";
                break;
            case 4:
                weekStr = "周四";
                break;
            case 5:
                weekStr = "周五";
                break;
            case 6:
                weekStr = "周六";
                break;
            case 7:
                weekStr = "周日";
                break;
        }
        return weekStr;
    }

    /**
     * 获取两个时间段的天数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public static long getBetweenDateTotalDay(long startTime, long endTime) {
        if (startTime > endTime) {
            startTime += endTime;
            endTime = startTime - endTime;
            startTime -= endTime;
        }
        return (endTime - startTime) / (1000 * 3600 * 24);
    }

    /**
     * 获取两个时间段的小时数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public static int getBetweenDateTotalHour(long startTime, long endTime) {
        if (startTime > endTime) {
            startTime += endTime;
            endTime = startTime - endTime;
            startTime -= endTime;
        }
        return (int) ((endTime - startTime) / (60 * 60 * 1000));
    }

    /**
     * 获取两个时间段的分钟数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    public static int getBetweenDateTotalMin(long startTime, long endTime) {
        if (startTime > endTime) {
            startTime += endTime;
            endTime = startTime - endTime;
            startTime -= endTime;
        }
        return (int) ((endTime - startTime) / (60 * 1000));
    }


    /**
     * 获取两个时间段的所有天的日期
     *
     * @param startTimeStamp 开始时间
     * @param endTimeStamp   结束时间
     */
    public static List<Date> getBetweenDate(long startTimeStamp, long endTimeStamp) {
        List<Date> dateList = new ArrayList<>();

        Calendar tempStart = Calendar.getInstance();
        tempStart.setTimeInMillis(startTimeStamp);
        tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTimeInMillis(endTimeStamp);

        dateList.add(new Date(startTimeStamp));
        while (tempStart.before(tempEnd)) {
            dateList.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        dateList.add(new Date(endTimeStamp));
        return dateList;
    }

    /**
     * 根据当前系统时间获取偏移量后的获取时间
     *
     * @param hourOffset 时偏移量，正数：后几小时、负数：前几小时
     * @param minOffset  分偏移量，正数：后几分钟、负数：前几分钟
     * @param secOffset  秒偏移量，正数：后几秒、负数：前几秒
     */
    public static long getCalcTime(int hourOffset, int minOffset, int secOffset) {
        return getCalcTime(-1, hourOffset, minOffset, secOffset);
    }

    /**
     * 根据指定时间和指定的偏移量获取时间
     *
     * @param timeStamp  时间戳
     * @param hourOffset 时偏移量，正数：后几小时、负数：前几小时
     * @param minOffset  分偏移量，正数：后几分钟、负数：前几分钟
     * @param secOffset  秒偏移量，正数：后几秒、负数：前几秒
     */
    public static long getCalcTime(long timeStamp, int hourOffset, int minOffset, int secOffset) {
        Calendar cal = Calendar.getInstance();
        if (timeStamp >= 0) {
            cal.setTimeInMillis(timeStamp);
        }
        cal.add(Calendar.HOUR_OF_DAY, hourOffset);
        cal.add(Calendar.MINUTE, minOffset);
        cal.add(Calendar.SECOND, secOffset);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当前时间的时间戳
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间
     *
     * @param format 时间格式（MM-dd HH:mm）
     */
    public static String getCurrentTime(String format) {
        return getSafeDateFormat(format).format(new Date());
    }

    /**
     * 日期转换成毫秒
     *
     * @param dateStr (只能是yyyy-MM-dd)
     */
    public static long getMillis(String dateStr) {
        return getMillis(dateStr, null);
    }


    /**
     * 日期转换成毫秒
     *
     * @param dateStr 日期字符串
     * @param format  日期格式串
     */
    public static long getMillis(String dateStr, String format) {
        format = format == null ? "yyyy-MM-dd" : format;
        try {
            return getSafeDateFormat(format).parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 时间转换为指定时区的时间
     *
     * @param srcTime        选填 原时间串（为null时使用当前系统时间）
     * @param srcTimeFormat  选填 原时间格式串(默认UTC 时间格式，yyyy-MM-dd'T'HH:mm:ss.SSS)
     * @param resultTimezone 选填 返回时间时区(默认UTC 时间格式)
     */
    public static String convertTime(String srcTime, String srcTimeFormat, TimeZone resultTimezone, String resultTimeFormat) {
        return formatTimeStamp(resultTimeFormat, convertTime(srcTime, srcTimeFormat, resultTimezone));
    }

    /**
     * 时间转换为指定时区的时间
     *
     * @param srcTime        选填 原时间串（为null时使用当前系统时间）
     * @param srcTimeFormat  选填 原时间格式串(默认UTC 时间格式，yyyy-MM-dd'T'HH:mm:ss.SSS)
     * @param resultTimezone 选填 返回时间时区(默认UTC 时间格式)
     */
    public static long convertTime(String srcTime, String srcTimeFormat, TimeZone resultTimezone) {
        if (TextUtils.isEmpty(srcTimeFormat)) {
            srcTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        }

        // 如果传入参数异常，使用本地时间
        SimpleDateFormat sdf = getSafeDateFormat(srcTimeFormat, resultTimezone);
        if (srcTime != null) {
            try {
                Date date = sdf.parse(srcTime);
                if (date != null) {
                    return date.getTime();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //返回当前时间
        sdf.getCalendar().setTime(new Date());
        return sdf.getCalendar().getTimeInMillis();
    }

    /**
     * 将时间格式A转换成时间格式B，A和B必须为标准的时间格式
     *
     * @param dateStr       日期串
     * @param dateStrFormat dateStr的格式HH:MM:SS
     * @param convertFormat 需转换成格式，如“yyyy-MM-DD hh:mm”等
     */
    public static String convertFormat(String dateStr, String dateStrFormat, String convertFormat) {
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }
        try {
            Date date = getSafeDateFormat(dateStrFormat).parse(dateStr);
            return getSafeDateFormat(convertFormat).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 转时间串为Date类型
     *
     * @param dateStr 时间串
     * @param format  时间串格式
     */
    public static Date convertToDate(String dateStr, String format) {
        try {
            if (TextUtils.isEmpty(format)) {
                return getSafeDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
            }
            return getSafeDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将日期类型换为指定格式的日期字符串
     *
     * @param format 时间格式（如MM-dd）
     * @param date   日期
     */
    public static String formatDate(String format, Date date) {
        return getSafeDateFormat(format).format(date);
    }

    /**
     * 将时间戳转换为指定格式的日期字符串
     *
     * @param format    时间格式（如MM-dd）
     * @param timestamp 日期格式System.currentTimeMillis()
     */
    public static String formatTimeStamp(String format, long timestamp) {
        return getSafeDateFormat(format).format(timestamp);
    }

    /**
     * 根据格式得到相应的日期
     *
     * @param format  时间格式（如MM-dd）
     * @param dateStr 日期
     */
    public static String formatDateStr(String format, String dateStr) {
        try {
            Date date = getSafeDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
            return getSafeDateFormat(format).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取时分秒为0的日期时间戳
     *
     * @param timeStamp 时间戳
     * @param amount    负数为前N天，正数为后n天
     */
    public static long getDateForZeroHMS(long timeStamp, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        cal.add(Calendar.DATE, amount);
        cal.set(Calendar.HOUR_OF_DAY, 0); //这是将当天的【时】设置为0
        cal.set(Calendar.MINUTE, 0); //这是将当天的【分】设置为0
        cal.set(Calendar.SECOND, 0); //这是将当天的【秒】设置为0
        return cal.getTimeInMillis();
    }
}
