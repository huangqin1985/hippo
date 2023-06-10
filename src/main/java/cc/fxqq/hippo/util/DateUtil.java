package cc.fxqq.hippo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT1 = "yyyy-MM-dd HH:mm";
	public static final String FORMAT2 = "yyyy-MM-dd";
	public static final String FORMAT3 = "yyyy-MM";
	public static final String FORMAT4 = "HH:mm:ss";
	
	public static final String WEEK_1 = "yyyy-MM-dd";
	public static final String MONTH_1 = "yyyy-MM";
	public static final String DAY_1 = "yyyy-MM-dd";
	
	private static String[] WEEK_LABELS = { "日", "一", "二", "三", "四", "五", "六" };
	
	public static String getUTC8Time(Date time, Integer timeZone) {
		return addHour(time, 8 - timeZone);
	}
	
	public static Integer getTimeDiffForUTC8(Integer timeZone) {
		return 8 - timeZone;
	}
	
	public static boolean isValidDate(String str) {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		
		try {
			df.parse(str);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isValidDatetime(String str) {
		DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
		
		try {
			df.parse(str);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	public static Date parseDate(String str) {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		
		try {
			return df.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date parseDatetime(String str) {
		DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
		
		try {
			return df.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date parse(String str, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		
		try {
			return df.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String format(Date date, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		
		return df.format(date);
	}
	
	public static String getDate(String date) {
		Date d = parseDatetime(date);
		return formatDate(d);
	}
	
	public static String formatDate(Date date) {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		
		return df.format(date);
	}
	
	public static String formatDatetime(Date date) {
		DateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
		
		return df.format(date);
	}
	
	public static String formatDatetimeString(String dateStr, String pattern) {
		Date date = parseDatetime(dateStr);
		DateFormat df = new SimpleDateFormat(pattern);
		
		return df.format(date);
	}
	
	/**
	 * 获取当周第一天日期
	 * @return
	 */
	public static String getStartDateStrOfWeek() {
		return getStartDateStrOfWeek(new Date());
	}
	
	public static String getStartDateStrOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return formatDate(cal.getTime());
	}
	
	public static String getEndDateStrOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		return formatDate(cal.getTime());
	}
	
	public static String getEndDateStrOfWeek() {
		return getEndDateStrOfWeek(new Date());
	}
	
	public static String getStartDateStrOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		return formatDate(cal.getTime());
	}
	
	public static String getStartDateStrOfMonth() {
		return getStartDateStrOfMonth(new Date());
	}
	
	public static String getEndDateStrOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
		int maxCurrentMonthDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, maxCurrentMonthDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		return formatDate(cal.getTime());
	}
	
	public static String getEndDateStrOfMonth() {
		return getEndDateStrOfMonth(new Date());
	}
	
	public static Date getEndDateOfWeek() {
		return getEndDateOfWeek(new Date());
	}
	
	public static boolean inThisWeek(Date date) {
		Date d = new Date();
		Date start = getStartDateOfWeek(d);
		Date end = getEndDateOfWeek(d);
		
		return start.compareTo(date) <= 0 && end.compareTo(date) >= 0;
	}
	
	public static Date getEndDateOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date getStartDateOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date getStartDateOfWeek() {
		return getStartDateOfWeek(new Date());
	}
	
	public static String formatForChWeek(Date date) { 
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd 周"); 
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String result = sf.format(date);
       
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return result + WEEK_LABELS[w];
    }
	
	public static Date getEndDateOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
		int maxCurrentMonthDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, maxCurrentMonthDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 获取当月最后一天
	 * @return
	 */
	public static Date getEndDateOfMonth() {
		return getEndDateOfMonth(new Date());
	}
	
	public static Date getStartDateOfMonth(Date date) {
		
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 获取当月第一天
	 * @return
	 */
	public static Date getStartDateOfMonth() {
		return getStartDateOfMonth(new Date());
	}
	
	/**
	 * 
	 * @param date
	 * @param hour
	 * @return
	 */
	public static String addHour(Date date, Integer hour) {
		
		if (date == null) {
			throw new NullPointerException("date is null");
		}
		if (hour == null) {
			throw new NullPointerException("hour is null");
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hour);
		
		return formatDatetime(cal.getTime());
	}
	
	/**
	 * 获取两个日期之间秒数
	 * @return
	 */
	public static String getSecondDuration(String startDateStr, String endDateStr) {
		
		if (StringUtils.isEmpty(startDateStr)) {
			throw new NullPointerException("startDate is null");
		}
		if (StringUtils.isEmpty(endDateStr)) {
			throw new NullPointerException("endDate is null");
		}
		Date startDate = parseDatetime(startDateStr);
		Date endDate = parseDatetime(endDateStr);
		
		int duration = (int)(endDate.getTime() - startDate.getTime()) / 1000;
		
		return getDayDurationDesc(duration);
	}
	
	/**
	 * 
	 * @param seconds
	 * @return
	 */
	public static String getDayDurationDesc(int time) {
		
	    String strTime = null;
	    int days = time / (60 * 60 * 24);
	    int hours = (time % (60 * 60 * 24)) / (60 * 60);
	    int minutes = (time % (60 * 60)) / 60;
	    int seconds = time % 60;
	    if (days > 0) {
	        strTime = days + "天" + (hours != 0 ? hours + "小时" : "");
	    } else if (hours > 0) {
	        strTime = hours + "小时" + (minutes != 0 ? minutes + "分" : "");
	    } else if (minutes > 0) {
	        strTime = minutes + "分" + (seconds != 0 ? seconds + "秒" : "");
	    } else {
	        strTime = seconds != 0 ? seconds + "秒" : "";
	    }
	    return strTime;
	}
	
	public static String getHourDurationDesc(int time) {
		
	    String strTime = null;
	    int hours = (time % (60 * 60 * 24)) / (60 * 60);
	    int minutes = (time % (60 * 60)) / 60;
	    int seconds = time % 60;
	    if (hours > 0) {
	        strTime = hours + "小时" + (minutes != 0 ? minutes + "分" : "") 
	        		+ (seconds != 0 ? seconds + "秒" : "");
	    } else if (minutes > 0) {
	        strTime = minutes + "分" + (seconds != 0 ? seconds + "秒" : "");
	    } else {
	        strTime = seconds != 0 ? seconds + "秒" : "";
	    }
	    return strTime;
	}
	
	public static void main(String[] args) {
		System.out.println(getHourDurationDesc(59));
		System.out.println(getHourDurationDesc(3599));
		System.out.println(getHourDurationDesc(26682));

		System.out.println(getHourDurationDesc(60 * 60 * 24 + 3601));
	}
}
