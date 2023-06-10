package cc.fxqq.hippo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.consts.FundType;
import cc.fxqq.hippo.consts.ReportTypeEnum;
import cc.fxqq.hippo.dao.AccountMapper;
import cc.fxqq.hippo.dao.ext.ReportExtMapper;
import cc.fxqq.hippo.dao.ext.TradeFundExtMapper;
import cc.fxqq.hippo.dao.ext.TradeOrderExtMapper;
import cc.fxqq.hippo.dto.template.OrderDayDTO;
import cc.fxqq.hippo.dto.template.Pager;
import cc.fxqq.hippo.dto.template.ReportDTO;
import cc.fxqq.hippo.entity.param.ReportParam;
import cc.fxqq.hippo.entity.param.TradeOrderParam;
import cc.fxqq.hippo.entity.result.FundSumResult;
import cc.fxqq.hippo.entity.result.OrderDayResult;
import cc.fxqq.hippo.task.ReportTask;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;
import cc.fxqq.hippo.util.PageUtil;

@Service
public class ReportService {

	@Autowired
	private ReportExtMapper reportExtMapper;
	
	@Autowired
	private TradeFundExtMapper tradeFundExtMapper;
	
	@Autowired
	private TradeOrderExtMapper tradeOrderExtMapper;

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private ReportTask reportTask;
	
	/**
	 * 
	 * @param account
	 * @param equity
	 */
	public void updateReportStatus(Integer account, BigDecimal equity,
			BigDecimal profit, BigDecimal margin, Date serverTime) {
		reportTask.updateReportStatus(ReportTypeEnum.WEEK.getValue(),
				account, equity, profit, margin, serverTime);
	}
	
	/**
	 * 
	 * @param account
	 * @param balance
	 */
	public void updateHistoryReport(Integer account, BigDecimal balance) {
		reportTask.setHistoryReport(ReportTypeEnum.WEEK.getValue(), account, balance);
	}
	
	public void updateCurrentHistoryReport(Integer account, BigDecimal balance) {
		Date startDate = DateUtil.getStartDateOfWeek();
		String date = DateUtil.formatDatetime(startDate);
		
		reportTask.setHistoryReport(ReportTypeEnum.WEEK.getValue(), account, balance, date);
	}

	/**
	 * 
	 * @param account
	 * @return
	 */
	public ReportDTO querySummary(Integer account) {
		ReportDTO report = new ReportDTO();
		
		BigDecimal deposit = DecimalUtil.get(tradeFundExtMapper.selectDeposit(account, null, null));
		BigDecimal withdraw = DecimalUtil.get(tradeFundExtMapper.selectWithdraw(account, null, null));
		report.setDeposit(deposit);
		report.setWithdraw(withdraw);
		
		return report;
	}
	
	public Pager<ReportDTO> queryReportList(ReportParam query) {
		Pager<ReportDTO> pager = PageUtil.selectPage(reportExtMapper, query,
				item -> {
					ReportDTO dto = new ReportDTO();
					BigDecimal preBalance = item.getPreBalance();
					BigDecimal realProfit = item.getRealProfit();
					dto.setLots(item.getLots());
					dto.setOrderNum(item.getOrderNum());
					dto.setStartDate(item.getStartDate());
					dto.setEndDate(item.getEndDate());
					dto.setBalance(item.getBalance());
					dto.setRealProfit(realProfit);
					dto.setPreBalance(preBalance);
					if (item.getDeposit().compareTo(BigDecimal.ZERO) > 0) {
						dto.setDeposit(item.getDeposit());
					}
					if (item.getWithdraw().compareTo(BigDecimal.ZERO) > 0) {
						dto.setWithdraw(item.getWithdraw());
					}
					
					Date start = DateUtil.parseDate(item.getStartDate());

					if (StringUtils.equals(ReportTypeEnum.WEEK.getValue(), item.getType())) {
						dto.setDescription(
								DateUtil.format(start, DateUtil.WEEK_1));
					} else if (StringUtils.equals(ReportTypeEnum.MONTH.getValue(), item.getType())) {
						dto.setDescription(
								DateUtil.format(start, DateUtil.MONTH_1));
					} else if (StringUtils.equals(ReportTypeEnum.DAY.getValue(), item.getType())) {
						dto.setDescription(
								DateUtil.format(start, DateUtil.DAY_1));
					}
					
					BigDecimal maxRealProfit = item.getMaxRealProfit();
					dto.setMaxRealProfit(maxRealProfit);
					BigDecimal minRealProfit = item.getMinRealProfit();
					dto.setMinRealProfit(minRealProfit);
					BigDecimal maxProfit = item.getMaxProfit();
					dto.setMaxProfit(maxProfit);
					BigDecimal minProfit = item.getMinProfit();
					dto.setMinProfit(minProfit);
					BigDecimal maxEquity = item.getMaxEquity();
					dto.setMaxEquity(maxEquity);
					BigDecimal minEquity = item.getMinEquity();
					dto.setMinEquity(minEquity);
					
					BigDecimal maxMargin = item.getMaxMargin();
					dto.setMaxMargin(maxMargin);
					
					BigDecimal minMarginRate = item.getMinMarginRate();
					if (minMarginRate != null) {
						dto.setMinMarginRateStr(minMarginRate + "%");
					}
					
					dto.setThisWeek(DateUtil.inThisWeek(DateUtil.parseDate(item.getStartDate())));
					
					TradeOrderParam orderParam = new TradeOrderParam();
					orderParam.setAccountId(item.getAccountId());
					orderParam.setCloseStartDate(item.getStartDate());
					orderParam.setCloseEndDate(item.getEndDate());
					List<OrderDayResult> dayResults = tradeOrderExtMapper.selectGroupByDay(orderParam);
					
					BigDecimal weekBalance = item.getPreBalance();
					List<OrderDayDTO> dayLists = Lists.newArrayList();
					for (OrderDayResult result : dayResults) {
						OrderDayDTO day = new OrderDayDTO();
						
						day.setStartDate(result.getDate());
						day.setEndDate(result.getDate());
						day.setLots(DecimalUtil.get(result.getLots()));
						day.setOrderNum(result.getOrderNum());
						day.setRealProfit(result.getRealProfit());
						day.setDescription(DateUtil.formatForChWeek(DateUtil.parseDate(result.getDate())));
						
						BigDecimal dayBalance = DecimalUtil.add(weekBalance, result.getRealProfit());
						day.setBalance(dayBalance);
						weekBalance = dayBalance;
						dayLists.add(day);
					}
					dto.setDayResults(dayLists);
					
					return dto;
				});
		return pager;
	}
	
}