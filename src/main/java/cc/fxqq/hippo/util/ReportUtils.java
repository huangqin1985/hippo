package cc.fxqq.hippo.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.consts.ReportTypeEnum;
import cc.fxqq.hippo.entity.Report;

public class ReportUtils {
	
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		List<Report> list = getDayReportList("2022-08-23 00:00:00");
//		
//		list.stream().forEach(t -> {
//			System.out.println(t.getStartDate() + " - " + t.getEndDate());
//		});
//		System.out.println();
//		list = getMonthReportList("2022-05-23 00:00:00");
//		
//		list.stream().forEach(t -> {
//			System.out.println(t.getStartDate() + " - " + t.getEndDate());
//		});
		
		Calendar cal = Calendar.getInstance();
//		cal.setFirstDayOfWeek(Calendar.MONDAY);
//		
//		cal.set(Calendar.MONTH, 0);
//		cal.setTime(cal.getTime());
//		cal.set(Calendar.DAY_OF_MONTH, 1);
//		
//		cal.setTime(cal.getTime());
//		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
//		
//		Date date = cal.getTime();
//
//		System.out.println(cal.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		
		cal.setTime(DateUtil.parseDate("2023-03-15"));
		
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		cal.add(Calendar.DAY_OF_MONTH, 3);
		Date date = cal.getTime();
		
		System.out.println(DateUtil.formatDate(date));
		
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		cal.add(Calendar.DAY_OF_MONTH, 2);
		date = cal.getTime();
		
		System.out.println(DateUtil.formatDate(date));
	}
	
	/**
	 * 
	 * @param reportType
	 * @return
	 */
	public static Report getCurrentReport(String reportType) {
		if (ReportTypeEnum.WEEK.getValue().equals(reportType)) {
			return getCurrentWeekReport();
		} else if (ReportTypeEnum.MONTH.getValue().equals(reportType)) {
			return getCurrentMonthReport();
		} else if (ReportTypeEnum.DAY.getValue().equals(reportType)) {
			return getCurrentDayReport();
		} else {
			throw new IllegalArgumentException(reportType);
		}
	}
	
	public static Report getCurrentMonthReport() {
		Report item = new Report();

		Calendar curCal = Calendar.getInstance();
		curCal.setTime(new Date());
		// 月第一天
		curCal.set(Calendar.DAY_OF_MONTH, 1);
		Date firstDate = curCal.getTime();
		item.setStartDate(DateUtil.formatDate(firstDate)); // 月最后一天
		int maxCurrentMonthDay = curCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		curCal.set(Calendar.DAY_OF_MONTH, maxCurrentMonthDay);
		Date lastDate = curCal.getTime();
		item.setEndDate(DateUtil.formatDate(lastDate));

		item.setType(ReportTypeEnum.MONTH.getValue());

		return item;
	}
	
	public static Report getCurrentWeekReport() {
		Report item = new Report();

		Calendar curCal = Calendar.getInstance();
		curCal.setFirstDayOfWeek(Calendar.MONDAY);// 以周一为首日 
		curCal.setTime(new Date());
		// 星期1
		curCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		Date monday = curCal.getTime();
		item.setStartDate(DateUtil.formatDate(monday)); // 星期日
		curCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		Date sunday = curCal.getTime();
		item.setEndDate(DateUtil.formatDate(sunday));

		item.setType(ReportTypeEnum.WEEK.getValue());
		
		return item;
	}

	public static Report getCurrentDayReport() {
		Report item = new Report();

		item.setStartDate(DateUtil.formatDate(new Date()));
		item.setEndDate(DateUtil.formatDate(new Date()));

		item.setType(ReportTypeEnum.DAY.getValue());
		
		return item;
	}
	
	/**
	 * 
	 * @param reportType
	 * @param firstCloseDate
	 * @return
	 */
	public static List<Report> getHistoryReport(String reportType, String firstCloseDate) {
		if (ReportTypeEnum.WEEK.getValue().equals(reportType)) {
			return getWeekReportList(firstCloseDate);
		} else if (ReportTypeEnum.MONTH.getValue().equals(reportType)) {
			return getMonthReportList(firstCloseDate);
		} else if (ReportTypeEnum.DAY.getValue().equals(reportType)) {
			return getDayReportList(firstCloseDate);
		} else {
			return Lists.newArrayList();
		}
	}
	
	/**
	 * 
	 * @param firstOpenDate yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static List<Report> getMonthReportList(String firstCloseDate) {
		Date date = DateUtil.parseDatetime(firstCloseDate);
		if (date == null) {
			return Lists.newArrayList();
		}
		
		Date curTime = DateUtil.getStartDateOfMonth();
		Date FisrtTime = DateUtil.getStartDateOfMonth(date);
		
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(curTime);

		Calendar firstCal = Calendar.getInstance();
		firstCal.setTime(FisrtTime); 

		List<Report> result = Lists.newArrayList();
		while (curCal.compareTo(firstCal) >= 0) {
			Report item = new Report();
			String firstDate = DateUtil.formatDate(curCal.getTime());
			item.setStartDate(firstDate);
			String lastDate = DateUtil.getEndDateStrOfMonth(curCal.getTime());
			item.setEndDate(lastDate);
			item.setType(ReportTypeEnum.MONTH.getValue());
			result.add(item);

			curCal.add(Calendar.MONTH, -1);
		}

		return result;
	}
	
	
	/**
	 * 
	 * @param firstCloseDate
	 * @return
	 */
	public static List<Report> getWMonthReportList(String firstCloseDate) {
		// 当月第一个星期五
		Calendar today = Calendar.getInstance();
		
		// 交易起始日
		Date tradeDate = DateUtil.parseDatetime(firstCloseDate);
		
		//计算开始月
		
		Date date = DateUtil.parseDatetime(firstCloseDate);
		if (date == null) {
			return Lists.newArrayList();
		}
		
		Date curTime = DateUtil.getStartDateOfMonth();
		Date FisrtTime = DateUtil.getStartDateOfMonth(date);
		
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(curTime);

		Calendar firstCal = Calendar.getInstance();
		firstCal.setTime(FisrtTime); 

		List<Report> result = Lists.newArrayList();
		while (curCal.compareTo(firstCal) >= 0) {
			Report item = new Report();
			String firstDate = DateUtil.formatDate(curCal.getTime());
			item.setStartDate(firstDate);
			String lastDate = DateUtil.getEndDateStrOfMonth(curCal.getTime());
			item.setEndDate(lastDate);
			item.setType(ReportTypeEnum.MONTH.getValue());
			result.add(item);

			curCal.add(Calendar.MONTH, -1);
		}

		return result;
	}

	/**
	 * 
	 * @param firstOpenDate yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static List<Report> getWeekReportList(String firstCloseDate) {
		Date date = DateUtil.parseDatetime(firstCloseDate);
		if (date == null) {
			return Lists.newArrayList();
		}
		
		Date curTime = DateUtil.getStartDateOfWeek();
		Date FisrtTime = DateUtil.getStartDateOfWeek(date);
		
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(curTime);

		Calendar firstCal = Calendar.getInstance();
		firstCal.setTime(FisrtTime); 
		
		List<Report> result = Lists.newArrayList();
		while (curCal.compareTo(firstCal) >= 0) {
			Report item = new Report();
			String firstDate = DateUtil.formatDate(curCal.getTime());
			item.setStartDate(firstDate);
			String lastDate = DateUtil.getEndDateStrOfWeek(curCal.getTime());
			item.setEndDate(lastDate);
			item.setType(ReportTypeEnum.WEEK.getValue());
			result.add(item);

			curCal.add(Calendar.WEEK_OF_YEAR, -1);
		}

		return result;
	}
	
	/**
	 * 
	 * @param firstCloseDate
	 * @return
	 */
	public static List<Report> getDayReportList(String firstCloseDate) {
		Date date = DateUtil.parseDatetime(firstCloseDate);
		if (date == null) {
			return Lists.newArrayList();
		}
		
		Date curTime = new Date();
		Date FisrtTime = date;
		
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(curTime);

		Calendar firstCal = Calendar.getInstance();
		firstCal.setTime(FisrtTime); 
		
		List<Report> result = Lists.newArrayList();
		while (curCal.compareTo(firstCal) >= 0) {
			Report item = new Report();
			String firstDate = DateUtil.formatDate(curCal.getTime());
			item.setStartDate(firstDate);
			item.setEndDate(firstDate);
			item.setType(ReportTypeEnum.DAY.getValue());
			result.add(item);

			curCal.add(Calendar.DAY_OF_YEAR, -1);
		}

		return result;
	}

}
