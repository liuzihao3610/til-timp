package com.tilchina.timp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * 日期工具类
 *
 * @author Administrator
 */
public class DateUtil {

	/**
	 * 给日期添加时分秒
	 *
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static Date endTime(String date) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String shortodate = date + " 23:59:59";
		Date longdate = formatter.parse(shortodate);
		return longdate;
	}

	/**
	 * 给日期添加时分秒
	 *
	 * @param map
	 * @return
	 */
	public static Map<String, Object> addTime(Map<String, Object> map) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (map.get("startTime") != null) {
			String startTime = map.get("startTime").toString().trim();
			if (startTime.length() == 10) {
				startTime = startTime + " 00:00:00";
				map.put("startTime", startTime);
			}
		}
		if (map.get("endTime") != null) {
			String endTime = map.get("endTime").toString().trim();
			if (endTime.length() == 10) {
				endTime = endTime + " 23:59:59";
				map.put("endTime", endTime);
			}
		}
		return map;
	}

	/**
	 * String类型的日期转换成Date
	 *
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static Date stringToDate(String date) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date strtodate = formatter.parse(date);
		return strtodate;
	}

	public static String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = formatter.format(date);
		return str;
	}

	public static String dateToYTD(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String str = formatter.format(date);
		return str;
	}

	public static String dateToMonthString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		String str = formatter.format(date);
		return str;
	}

	public static Date stringToDateL(String date) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date strtodate = formatter.parse(date);
		return strtodate;
	}


	/**
	 * String类型的日期转换成Date
	 *
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static Date stringToDateCode(String date) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date strtodate = formatter.parse(date);
		return strtodate;
	}

	public static String dateToStringCode(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = formatter.format(date);
		str += new Random().nextInt(9999);
		return str;
	}

	public static String dateToStringCode(String prefix, Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String str = String.format("%s%s", prefix, formatter.format(date));
		return str;
	}

	public static String dateToStringYearCode(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		String str = formatter.format(date);
		return str;
	}

	public static String tkey(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = formatter.format(date);
		return str;
	}

	/**
	 * 获取前n天的日期转为字符串
	 *
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public static String getPastDate(int n) throws RuntimeException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -n);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		Date monday = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String preMonday = sdf.format(monday);
		return preMonday;
	}

	/**
	 * 获取前n天的日期
	 *
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public static Date getStartDate(int n) throws RuntimeException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -n);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		Date date = cal.getTime();
		return date;
	}


	/**
	 * 获取某月最后一天日期
	 *
	 * @param month 当前时间前后几个月
	 * @return
	 * @throws Exception
	 */
	public static Date getMonthEndDay(int month) throws RuntimeException {
		Calendar cal = Calendar.getInstance();
		/*	cal.set(Calendar.MONTH, month);*/
		cal.add(Calendar.MONTH, month);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		cal.roll(Calendar.DAY_OF_MONTH, -1);
		Date date = new Date(cal.getTimeInMillis());
		return date;
	}

	/**
	 * 获取某月第一天日期
	 *
	 * @param month 当前时间前后几个月
	 * @return
	 * @throws Exception
	 */
	public static Date getMonth(int month) throws RuntimeException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, month);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		Date date = new Date(cal.getTimeInMillis());
		return date;
	}

	/**
	 * 获取上个月一号日期
	 * @return
	 * @throws Exception
	 */
/*	public static Date getLastMonth() throws Exception{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MINUTE,0);
		Date date = new Date(cal.getTimeInMillis());
		return date;
	}*/

	/**
	 * 获取后n天的日期
	 *
	 * @param n
	 * @return
	 * @throws Exception
	 */
	public static Date getDate(Date date, int n) throws RuntimeException {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, n);
		Date dat = c.getTime();
		return dat;
	}

	public static Date formatDate(Date objVal, String string) {
		return null;
	}

	public static int getMonth(Date date) throws RuntimeException {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH) + 1;
		return month;
	}

	public static Date getMonthStartAndEnd(Date date, int i) throws RuntimeException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (i == 0) {
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			Date startDate = new Date(cal.getTimeInMillis());
			return startDate;
		} else {
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 11);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MINUTE, 59);
			cal.roll(Calendar.DAY_OF_MONTH, -1);
			Date endDate = new Date(cal.getTimeInMillis());
			return endDate;
		}
	}

	public static void main(String[] args) {

		try {
			System.out.println(dateToString(getMonthStartAndEnd(new Date(), 1)));
			System.out.println(dateToString(getMonthStartAndEnd(new Date(), 0)));
			System.out.println(differentDays(getMonthStartAndEnd(new Date(), 0), getMonthStartAndEnd(new Date(), 1)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * endDate比start多的天数
	 *
	 * @param start
	 * @param endDate
	 * @return
	 */
	public static Integer differentDays(Date start, Date endDate) throws RuntimeException {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(start);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2)   //同一年
		{
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
				{
					timeDistance += 366;
				} else    //不是闰年
				{
					timeDistance += 365;
				}
			}

			return timeDistance + (day2 - day1);
		} else    //不同年
		{
			System.out.println("判断day2 - day1 : " + (day2 - day1));
			return day2 - day1;
		}
	}

	/**
	 * 时间比较，如果前者比后者早，则返回true，否则返回false。
	 *
	 * @param time1
	 * @param time2
	 * @return
	 * @throws Exception
	 */
	public static boolean compare(Date time1, Date time2) {
		return time1.getTime() - time2.getTime() <= 0;

	}

}
