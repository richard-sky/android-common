package com.richard.library.context.task;

import com.richard.library.context.util.ObjectUtilKt;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author: Richard
 * @createDate: 2024/4/7 18:05
 * @version: 1.0
 * @description: 任务执行时间工具类
 */
final class TaskTimeUtil {

    /**
     * 获取距离下个时间点的秒数
     *
     * @param everyTime 每满整的间隔时间(例子:值为5的时候、并且时间单位为分则执行时间为：10:00、10：05、10：10、10：15...)
     * @param timeUnit  时间单位
     * @return 距离下个时间点的秒数
     */
    public static long getCronEveryDelaySecond(long everyTime, TimeUnit timeUnit) {
        if (everyTime <= 0) {
            return 0;
        }

        long cronDelaySecond = 0;
        Calendar nowTime = Calendar.getInstance();
        int curHour = nowTime.get(Calendar.HOUR_OF_DAY);
        int curMin = nowTime.get(Calendar.MINUTE);
        int curSec = nowTime.get(Calendar.SECOND);

        switch (timeUnit) {
            case SECONDS:
                cronDelaySecond = (60 - curSec) % everyTime;
                break;
            case MINUTES:
                long s = (everyTime - curMin % everyTime) - 1;
                cronDelaySecond = s * 60 + (60 - curSec);
                break;
            case HOURS:
                long m = (everyTime - curHour % everyTime) - 1;
                cronDelaySecond = m * 60 + (60 - curMin - 1) * 60 + (60 - curSec);
                break;
        }

        return cronDelaySecond;
    }

    /**
     * 获取距离下个时间点的秒数
     *
     * @param timeValue 在什么时间执行(例:值为5的时候,并且时间单位为分，则执行时间为:10:05、11:05、12:05...)
     * @param timeUnit  时间单位
     * @return 距离下个时间点的秒数
     */
    public static long getCronDelaySecond(long timeValue, TimeUnit timeUnit) {
        long cronDelaySecond = 0;

        Calendar nowTime = Calendar.getInstance();
        int curHour = nowTime.get(Calendar.HOUR_OF_DAY);
        int curMin = nowTime.get(Calendar.MINUTE);
        int curSec = nowTime.get(Calendar.SECOND);

        int cm = 60 - (curMin + 1);
        int cs = 60 - (curSec + 1);

        switch (timeUnit) {
            case SECONDS:
                long s = timeValue - curSec;
                cronDelaySecond = s < 0 ? s + 60 : s;
                break;
            case MINUTES:
                long m = timeValue - (curMin + 1);
                cronDelaySecond = (m < 0 ? m + 60 : m) * 60 + cs + 1;
                break;
            case HOURS:
                long h = timeValue - (curHour + 1);
                cronDelaySecond = ((h < 0 ? h + 24 : h) * 60 + cm) * 60 + cs + 1;
                break;
        }

        return cronDelaySecond;
    }

    /**
     * 解析cron
     *
     * @param cronTime 执行时间例如:HH:mm:ss 、 mm:ss 、 ss
     * @return [0]:initDelay 秒、[1]:period 秒
     */
    public static long[] parseCron(String cronTime) {
        String[] cron = cronTime.split(":");

        Calendar nowTime = Calendar.getInstance();
        int curHour = nowTime.get(Calendar.HOUR_OF_DAY);
        int curMin = nowTime.get(Calendar.MINUTE);
        int curSec = nowTime.get(Calendar.SECOND);

        TimeUnit timeUnit;
        int hour = -1;
        int min = -1;
        int sec = -1;

        try {
            if (cron.length >= 3) {
                timeUnit = TimeUnit.HOURS;
                hour = ObjectUtilKt.toInt(cron[0]);
                min = ObjectUtilKt.toInt(cron[1]);
                sec = ObjectUtilKt.toInt(cron[2]);
            } else if (cron.length == 2) {
                timeUnit = TimeUnit.MINUTES;
                min = ObjectUtilKt.toInt(cron[0]);
                sec = ObjectUtilKt.toInt(cron[1]);
            } else {
                timeUnit = TimeUnit.SECONDS;
                sec = ObjectUtilKt.toInt(cron[0]);
            }
        } catch (Throwable e) {
            throw new RuntimeException("cronTime 格式错误，例如:HH:mm:ss 、 mm:ss 、 ss");
        }

        long initDelay = 0;
        if (hour >= 0) {
            if (hour == curHour) {
                if (min == curMin) {
                    if (sec >= curSec) {
                        initDelay = TaskTimeUtil.getCronDelaySecond(sec, TimeUnit.SECONDS);
                    } else {
                        initDelay = TaskTimeUtil.getCronDelaySecond(hour, TimeUnit.HOURS);
                        initDelay += min * 60L + sec;
                    }
                } else if (min > curMin) {
                    initDelay = TaskTimeUtil.getCronDelaySecond(min, TimeUnit.MINUTES);
                    initDelay += sec;
                } else {
                    initDelay = TaskTimeUtil.getCronDelaySecond(hour, TimeUnit.HOURS);
                    initDelay += min * 60L + sec;
                }
            } else {
                initDelay = TaskTimeUtil.getCronDelaySecond(hour, TimeUnit.HOURS);
                initDelay += min * 60L + sec;
            }
        } else if (min >= 0) {
            if (min == curMin) {
                if (sec >= curSec) {
                    initDelay = TaskTimeUtil.getCronDelaySecond(sec, TimeUnit.SECONDS);
                } else {
                    initDelay = TaskTimeUtil.getCronDelaySecond(hour, TimeUnit.HOURS);
                    initDelay += min * 60L + sec;
                }
            } else {
                initDelay = TaskTimeUtil.getCronDelaySecond(min, TimeUnit.MINUTES);
                initDelay += sec;
            }
        } else {
            initDelay = TaskTimeUtil.getCronDelaySecond(sec, TimeUnit.SECONDS);
        }

        long period = 0;
        switch (timeUnit) {
            case SECONDS:
                period = 60;
                break;
            case MINUTES:
                period = 60L * 60L;
                break;
            case HOURS:
                period = 24 * 60L * 60L;
                break;
        }

        return new long[]{initDelay, period};
    }
}
