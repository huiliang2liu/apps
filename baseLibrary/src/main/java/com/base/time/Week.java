package com.base.time;

import java.util.Calendar;
import java.util.Date;

/**
 * com.xh.time author:liuhuiliang email:825378291@qq.com instruction: 2018-6-12
 * 上午11:49:00
 **/
public class Week {
    /**
     * 获得星期简称
     *
     * @param date
     * @return
     */
    public static String referred(Date date) {
        if (date == null)
            date = new Date();
        return String.format("%ta", date);
    }

    /**
     * 获得星期简称
     *
     * @param millis
     * @return
     */
    public static String referred(long millis) {
        return referred(TimeUtil.mill2ate(millis));
    }

    /**
     * 获得星期全称
     *
     * @param date
     * @return
     */
    public static String fullName(Date date) {
        if (date == null)
            date = new Date();
        return String.format("%tA", date);
    }

    /**
     * 获得星期全称
     *
     * @param millis
     * @return
     */
    public static String fullName(long millis) {
        return fullName(TimeUtil.mill2ate(millis));
    }

    /**
     * instruction:一年中的第几个礼拜 2018-6-12 上午11:57:07
     *
     * @param date
     * @return
     */
    public static int weekOnYear(Date date) {
        if (date == null)
            date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * instruction:一年中的第几个礼拜 2018-6-12 上午11:57:07
     *
     * @param millis
     * @return
     */
    public static int weekOnYear(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * instruction:一个月的第几个礼拜 2018-6-12 上午11:58:29
     *
     * @param date
     * @return
     */
    public static int weekOnMonth(Date date) {
        if (date == null)
            date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * instruction:一个月的第几个礼拜 2018-6-12 上午11:58:29
     *
     * @param millis
     * @return
     */
    public static int weekOnMonth(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * instruction:礼拜 几2018-6-12 上午11:58:29
     *
     * @param millis
     * @return
     */
    public static int week(long millis) {
        Calendar calendar = Calendar.getInstance();
//		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * instruction:礼拜 几 2018-6-12 上午11:58:29
     *
     * @param date
     * @return
     */
    public static int week(Date date) {
        if (date == null)
            date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }
}
