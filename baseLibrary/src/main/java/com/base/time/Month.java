package com.base.time;

import java.util.Date;

/**
 * com.xh.time author:liuhuiliang email:825378291@qq.com instruction: 2018-6-12
 * 上午11:38:56
 **/
public class Month {
    /**
     * instruction:获取月份简称 2018-6-12 上午11:42:31
     *
     * @param date
     * @return
     */
    public static String referred(Date date) {
        if (date == null)
            date = new Date();
        return String.format("%tb", date);
    }

    /**
     * instruction:获取月份简称 2018-6-12 上午11:42:31
     *
     * @return
     */
    public static String referred(long millis) {
        return referred(TimeUtil.mill2ate(millis));
    }

    /**
     * instruction:获取月份全称 2018-6-12 上午11:42:51
     *
     * @param date
     * @return
     */
    public static String fullName(Date date) {
        if (date == null)
            date = new Date();
        return String.format("%tB", date);
    }

    /**
     * instruction:获取月份全称 2018-6-12 上午11:42:51
     *
     * @return
     */
    public static String fullName(long millis) {
        return fullName(TimeUtil.mill2ate(millis));
    }

    /**
     * instruction:获得月份 01-12 2018-6-12 上午11:42:16
     *
     * @param date
     * @return
     */
    public static String month(Date date) {
        if (date == null)
            date = new Date();
        return String.format("%tm", date);
    }

    /**
     * instruction:获得月份 01-12 2018-6-12 上午11:42:16
     *
     * @return
     */
    public static String month(long millis) {
        return month(TimeUtil.mill2ate(millis));
    }

    /**
     * instruction:获得月份 01-12 2018-6-12 上午11:42:16
     *
     * @param date
     * @return
     */
    public static int month2int(Date date) {
        if (date == null)
            date = new Date();
        return Integer.valueOf(month(date)) + 1;
    }

    /**
     * instruction:获得月份 01-12 2018-6-12 上午11:42:16
     *
     * @return
     */
    public static int month2int(long millis) {
        return month2int(TimeUtil.mill2ate(millis));
    }

    /**
     * instruction:第一天是礼拜几 2018-6-12 下午12:28:59
     *
     * @param date
     * @return
     */
    public static int firstWeek(Date date) {
        if (date == null)
            date = new Date();
        int day = Day.dayOnMonth(date);
        int week = Week.weekOnMonth(date);
        return 7 + (week - day % 7);
    }
}
