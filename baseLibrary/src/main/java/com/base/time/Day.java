package com.base.time;

import java.util.Calendar;
import java.util.Date;

/**
 * com.xh.time author:liuhuiliang email:825378291@qq.com instruction: 2018-6-12
 * 下午12:04:07
 **/
public class Day {
	/**
	 * 
	 * instruction:获得日期天 1-31 2018-6-12 下午12:06:49
	 * 
	 * @param date
	 * @return
	 */
	public static String dayOne(Date date) {
		return String.format("%te", date);
	}

	/**
	 * 
	 * instruction:获得日期天 1-31 2018-6-12 下午12:06:49
	 * 
	 * @param millis
	 * @return
	 */
	public static String dayOne(long millis) {
		return dayOne(TimeUtil.mill2ate(millis));
	}

	/**
	 * 
	 * instruction:获得日期天 01-31 2018-6-12 下午12:06:55
	 * 
	 * @param date
	 * @return
	 */
	public static String dayTwo(Date date) {
		return String.format("%td", date);
	}

	/**
	 * 
	 * instruction:获得日期天 01-31 2018-6-12 下午12:06:55
	 * 
	 * @param millis
	 * @return
	 */
	public static String dayTwo(long millis) {
		return dayTwo(TimeUtil.mill2ate(millis));
	}

	/**
	 * 
	 * instruction:一年中的第几天 085 2018-6-12 下午12:07:03
	 * 
	 * @param date
	 * @return
	 */
	public static String dayOnyear(Date date) {
		return String.format("%tj", date);
	}

	/**
	 * 
	 * instruction:一年中的第几天 085 2018-6-12 下午12:07:03
	 * 
	 * @param millis
	 * @return
	 */
	public static String dayOnyear(long millis) {
		return dayOnyear(TimeUtil.mill2ate(millis));
	}

	/**
	 * 
	 * instruction:月中的第几天 2018-6-12 下午12:10:41
	 * 
	 * @param date
	 * @return
	 */
	public static int dayOnMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * instruction:月中的第几天 2018-6-12 下午12:10:41
	 * 
	 * @param millis
	 * @return
	 */
	public static int dayOnMonth(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * instruction:第几天 2018-6-12 下午12:10:41
	 * 
	 * @param date
	 * @return
	 */
	public static int day(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 
	 * instruction:第几天 2018-6-12 下午12:10:41
	 * 
	 * @param millis
	 * @return
	 */
	public static int day(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 
	 * instruction:平移多少天 2018-6-12 下午12:35:34
	 * 
	 * @param millis
	 * @param translationDay
	 * @return
	 */
	public static long translation(long millis, int translationDay) {
		return millis + translationDay * TimeUtil.DAY;
	}

	/**
	 * 
	 * instruction:平移多少天 2018-6-12 下午12:35:34
	 * 
	 * @param date
	 * @param translationDay
	 * @return
	 */
	public static Date translation(Date date, int translationDay) {
		long millis = translation(TimeUtil.date2mill(date), translationDay);
		return TimeUtil.mill2ate(millis);
	}
}
