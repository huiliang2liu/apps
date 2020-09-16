package com.base.time;

import java.util.Calendar;
import java.util.Date;

/**
 * com.xh.time author:liuhuiliang email:825378291@qq.com instruction: 2018-6-12
 * 上午10:47:38
 **/
public class Year {
    /**
     * instruction:获得年简称 2016就是16 2018-6-12 上午11:22:26
     *
     * @param date
     * @return
     */
    public static String referred(Date date) {
        if (date == null)
            date = new Date();
        return String.format("%ty", date);
    }

    /**
     * instruction:获得年简称 2016就是16 2018-6-12 上午11:27:26
     *
     * @param millis
     * @return
     */
    public static String referred(long millis) {
        return referred(TimeUtil.mill2ate(millis));
    }

    /**
     * instruction:获得年全称 2016 2018-6-12 上午11:22:52
     *
     * @param date
     * @return
     */
    public static String fullName(Date date) {
        if (date == null)
            date = new Date();
        return String.format("%tY", date);
    }

    /**
     * instruction:获得年全称 2016 2018-6-12 上午11:22:52
     *
     * @return
     */
    public static String fullName(long millis) {
        return fullName(TimeUtil.mill2ate(millis));
    }

    /**
     * 获得年 instruction: 2018-6-12 上午11:24:39
     *
     * @param date
     * @return
     */
    public static int year(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获得年 instruction: 2018-6-12 上午11:24:39
     *
     * @return
     */
    public static int year(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * instruction:判断闰年 2018-6-12 上午11:32:17
     *
     * @param year
     * @return
     */
    public static boolean leapYear(int year) {
        if (year % 100 == 0) {
            if (year % 400 == 0)
                return true;
        } else if (year % 4 == 0)
            return true;
        return false;
    }

    /**
     * instruction:判断闰年 2018-6-12 上午11:32:17
     *
     * @return
     */
    public static boolean leapYear(Date date) {
        return leapYear(year(date));
    }

    /**
     * instruction:判断闰年 2018-6-12 上午11:32:17
     *
     * @return
     */
    public static boolean leapYear(long date) {
        return leapYear(year(date));
    }

    /**
     * instruction:转化为生肖 2018-6-12 上午11:33:13
     *
     * @param year
     * @return
     */
    public static String chineseZodiac(int year) {

        String[] shengxiaos = {"鼠", "牛", "虎", "免", "龙", "蛇", "马", "羊", "猴",
                "鸡", "狗", "猪"};
        String shengxiao;
        int m = Math.abs(year - 2008) % 12;
        if (year >= 2008) {
            shengxiao = shengxiaos[m];
        } else {
            if (m == 0) {
                m = 12;
            }
            shengxiao = shengxiaos[12 - m];
        }
        return shengxiao;
    }

    /**
     * instruction:转化为生肖 2018-6-12 上午11:33:13
     *
     * @return
     */
    public static String chineseZodiac(Date date) {
        return chineseZodiac(year(date));
    }

    /**
     * instruction:转化为生肖 2018-6-12 上午11:33:13
     *
     * @return
     */
    public static String chineseZodiac(long millis) {
        return chineseZodiac(year(millis));
    }
}
