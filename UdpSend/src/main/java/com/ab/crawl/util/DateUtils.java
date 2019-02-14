package com.ab.crawl.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author test
 *
 */
public class DateUtils {

	/**
	 * @Fields sdf : 默认SimpleDateFormat
	 */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取UTC 时间
	 * 
	 * @return
	 */
	public static Date getUTCTime() {
		// 取得本地时间：
		Calendar cal = Calendar.getInstance();
		// 取得时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

		// 从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		return cal.getTime();
	}

	/**
	 * 获取指定时间的UTC 时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getUTCTime(Date date) {
		// 取得本地时间：
		Calendar cal = Calendar.getInstance();
		// 取得时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		// 取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		cal.setTime(date);
		// 从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		return cal.getTime();
	}

	/**
	 * 小时计算
	 * 
	 * @param date
	 * @param hourAmount
	 * @return
	 */
	public static Date addHour(Date date, int hourAmount) {
		return addInteger((Date) date, 11, hourAmount);
	}

	/**
	 * 日期计算
	 * 
	 * @param date
	 * @param dateType
	 * @param amount
	 * @return
	 */
	private static Date addInteger(Date date, int dateType, int amount) {
		Date myDate = null;
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(dateType, amount);
			myDate = calendar.getTime();
		}

		return myDate;
	}

	/**
	 * 字符串转日期
	 * 
	 * @param str
	 * @param sdf
	 * @return
	 */
	public static Date str2Date(String str) {
		return str2Date(str,sdf);
	}
	public static Date str2Date(String str, SimpleDateFormat sdf) {
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取时间段(小时)
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static Double getPeriod(Date start, Date end) {
		Long difference = end.getTime() - start.getTime();
		return difference.doubleValue() / (60 * 60 * 1000);
	}

	/**
	 * 格式化枚举定义
	 */
	public enum DateFormats {
		DATETIME_F("yyyy-MM-dd HH:mm:ss SSS"), DATETIME_1("yyyy-MM-dd HH:mm:ss"), DATE_1("yyyy-MM-dd"),
		DATETIME_2("yyyy/MM/dd HH:mm:ss"), DATE_2("yyyy/MM/dd"), TIME("HH:mm:ss"), DateTime_T("yyyy-MM-dd'T'HH:mm:ss");

		String format = "yyyy-MM-dd HH:mm:ss";

		DateFormats(String ft) {
			format = ft;
		}
	}

	/**
	 * 获取现在时间
	 *
	 * @return 当前时间
	 */
	public static Date getNow() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 获取现在时间
	 *
	 * @return 当前时间
	 */
	public static String getNowStr() {
		return formatDate(Calendar.getInstance().getTime(), DateFormats.DATETIME_1);
	}

	/**
	 * 获取现在时间
	 *
	 * @return 当前时间
	 */
	public static String getNowStr(DateFormats dateFormats) {
		return formatDate(Calendar.getInstance().getTime(), dateFormats);
	}

	/**
	 * 获取格式化时间
	 *
	 * @param date   Date
	 * @param format DateFormats
	 * @return 返回格式化后字符串
	 */
	public static String formatDate(Date date, DateFormats format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format.format);
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 秒级加减
	 *
	 * @param curDate
	 * @param seconds
	 * @return
	 */
	public static Date addSeconds(Date curDate, int seconds) {
		long curTime = curDate.getTime();
		curTime += seconds * 1000;
		Date newDate = new Date(curTime);
		return newDate;
	}

	/**
	 * 分级加减
	 *
	 * @param curDate
	 * @param minitus
	 * @return
	 */
	public static Date addMinute(Date curDate, int minitus) {
		long curTime = curDate.getTime();
		curTime += minitus * 60 * 1000;
		Date newDate = new Date(curTime);
		return newDate;
	}

	/**
	 * 日期加减
	 *
	 * @param curDate
	 * @param day
	 * @return
	 */
	public static Date addDay(Date curDate, int day) {
		long curTime = curDate.getTime();
		curTime += day * 24 * 60 * 60 * 1000;
		Date newDate = new Date(curTime);
		return newDate;
	}

	/**
	 * 本地时间转utc时间
	 *
	 * @param timespans
	 * @return
	 */
	public static Date local2Utc(long timespans) {
		Date utcTime = null;
		try {
//            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//            utcTime = sdf.parse(DateUtil.formatDate(localTime, DateUtil.DateFormats.DATETIME_1));
			long localTimeInMillis = timespans;
			/** long时间转换成Calendar */
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(localTimeInMillis);
			/** 取得时间偏移量 */
			int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
			/** 取得夏令时差 */
			int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
			/** 从本地时间里扣除这些差量，即可以取得UTC时间 */
			calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
			/** 取得的时间就是UTC标准时间 */
			utcTime = new Date(calendar.getTimeInMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return utcTime;
	}

	/**
	 * 本地时间转utc时间
	 *
	 * @param localTime
	 * @return
	 */
	public static Date local2Utc(Date localTime) {
		if (localTime == null) {
			return null;
		}
		return local2Utc(localTime.getTime());
	}

	public static Date getMonthFirstDate(Date curDate) {
		if (curDate == null) {
			curDate = new Date();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getYearFirstDate(Date curDate) {
		if (curDate == null) {
			curDate = new Date();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		calendar.add(Calendar.YEAR, 0);
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getPreDate(Date curDate) {
		if (curDate == null) {
			curDate = new Date();
		}
		Date retDate = addDay(curDate, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowStr = sdf.format(retDate);
		try {
			return sdf.parse(nowStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retDate;
	}

	public static Date getNextDate(Date curDate) {
		if (curDate == null) {
			curDate = new Date();
		}
		Date retDate = addDay(curDate, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowStr = sdf.format(retDate);
		try {
			return sdf.parse(nowStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retDate;
	}

	public static Date getCurDate() {
		Date cDate = null;
		try {
			String pattern = "yyyy-MM-dd";// 获取的日期格式
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			Date today = new Date();// 获取当前日期
			String currentDate = df.format(today);// 获取当前日期的字符串
			cDate = df.parse(currentDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cDate;
	}

	public static void main(String[] args) {
		try {
			Date end = sdf.parse("2018-10-15 18:10:12");
			System.out.println(getUTCTime(end));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
