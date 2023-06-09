package cc.fxqq.hippo.task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cc.fxqq.hippo.consts.FundType;
import cc.fxqq.hippo.consts.ReportTypeEnum;
import cc.fxqq.hippo.dao.AccountMapper;
import cc.fxqq.hippo.dao.ReportMapper;
import cc.fxqq.hippo.dao.ext.ReportExtMapper;
import cc.fxqq.hippo.dao.ext.TradeFundExtMapper;
import cc.fxqq.hippo.dao.ext.TradeOrderExtMapper;
import cc.fxqq.hippo.entity.Account;
import cc.fxqq.hippo.entity.Report;
import cc.fxqq.hippo.entity.param.TradeOrderParam;
import cc.fxqq.hippo.entity.result.FundSumResult;
import cc.fxqq.hippo.entity.result.OrderSumResult;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;
import cc.fxqq.hippo.util.ReportUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReportTask {

	@Autowired
	private TradeOrderExtMapper tradeOrderExtMapper;
	
	@Autowired
	private TradeFundExtMapper tradeFundExtMapper;
	
	@Autowired
	private ReportExtMapper reportExtMapper;

	@Autowired
	private ReportMapper reportMapper;

	@Autowired
	private AccountMapper accountMapper;
	
	/**
	 * 
	 * @param reportType
	 * @param account
	 * @param balance
	 */
	public void updateReportStatus(String reportType, Integer account, 
			BigDecimal equity, BigDecimal currentProfit, BigDecimal margin) {
		String startDate = null;
		String endDate = null;
		if (ReportTypeEnum.WEEK.getValue().equals(reportType)) {
			startDate = DateUtil.getStartDateStrOfWeek();
			endDate = DateUtil.getEndDateStrOfWeek();
		} else {
			return;
		}
		
		Report report = reportExtMapper.selectUnique(account, reportType, startDate);
		
		if (report == null) {
			Report rpt = new Report();
			rpt.setAccountId(account);
			rpt.setType(reportType);
			rpt.setStartDate(startDate);
			rpt.setEndDate(endDate);
			
			Account acc = accountMapper.selectByPrimaryKey(account);
			if (acc != null) {
				rpt.setPreBalance(acc.getBalance());
				rpt.setBalance(acc.getBalance());
			}
			rpt.setPreEquity(equity);
			rpt.setUpdateTime(DateUtil.formatDatetime(new Date()));
			rpt.setCreateTime(DateUtil.formatDatetime(new Date()));
			
			reportMapper.insertSelective(rpt);
		} else {
			report.setEquity(equity);
			
			BigDecimal maxEquity = report.getMaxEquity();
			BigDecimal minEquity = report.getMinEquity();
			
			if (maxEquity == null) {
				report.setMaxEquity(equity);
			} else {
				report.setMaxEquity(DecimalUtil.max(maxEquity, equity));
			}
			if (minEquity == null) {
				report.setMinEquity(equity);
			} else {
				report.setMinEquity(DecimalUtil.min(minEquity, equity));
			}
			
			TradeOrderParam  param = new TradeOrderParam();
			param.setAccountId(account);
			param.setCloseStartDate(startDate);
			param.setCloseEndDate(endDate);
			// 未完成订单利润
			BigDecimal preProfit = report.getEquity().subtract(report.getBalance());
			BigDecimal realProfit = DecimalUtil.get(tradeOrderExtMapper.selectRealProfit(param));
			BigDecimal totalProfit = DecimalUtil.add(realProfit, currentProfit, preProfit);
			
			BigDecimal maxRealProfit = report.getMaxRealProfit();
			BigDecimal minRealProfit = report.getMinRealProfit();
			if (maxRealProfit == null) {
				report.setMaxRealProfit(DecimalUtil.max(BigDecimal.ZERO, totalProfit));
			} else {
				report.setMaxRealProfit(DecimalUtil.max(maxRealProfit, totalProfit));
			}
			if (minRealProfit == null) {
				report.setMinRealProfit(DecimalUtil.min(BigDecimal.ZERO, totalProfit));
			} else {
				report.setMinRealProfit(DecimalUtil.min(minRealProfit, totalProfit));
			}
			
			BigDecimal maxProfit = report.getMaxProfit();
			BigDecimal minProfit = report.getMinProfit();
			if (maxProfit == null) {
				report.setMaxProfit(DecimalUtil.max(BigDecimal.ZERO, currentProfit));
			} else {
				report.setMaxProfit(DecimalUtil.max(maxProfit, currentProfit));
			}
			if (minProfit == null) {
				report.setMinProfit(DecimalUtil.min(BigDecimal.ZERO, currentProfit));
			} else {
				report.setMinProfit(DecimalUtil.min(minProfit, currentProfit));
			}
			
			BigDecimal maxMargin = report.getMaxMargin();
			if (maxMargin == null) {
				report.setMaxMargin(margin);
			} else {
				report.setMaxMargin(DecimalUtil.max(maxMargin, margin));
			}
			
			BigDecimal minMarginRate = report.getMinMarginRate();
			if (minMarginRate == null) {
				report.setMinMarginRate(DecimalUtil.getPercent2(equity, margin));
			} else {
				report.setMinMarginRate(DecimalUtil.min(minMarginRate, DecimalUtil.getPercent2(equity, margin)));
			}
			
			reportMapper.updateByPrimaryKeySelective(report);
		}
		
	}
	
	public void setHistoryReport(String reportType, Integer accountId, BigDecimal balance) {
		Map<String, String> map = tradeOrderExtMapper.selectFirstAndLast(accountId);
		if (map == null) {
			return;
		}
		String firstCloseTime = map.get("firstDate");
		
		setHistoryReport(reportType, accountId, balance, firstCloseTime);
	}
	
	/**
	 * 
	 * @param reportType
	 * @param account
	 * @param balance
	 */
	public void setHistoryReport(String reportType, Integer accountId, BigDecimal balance,
			String firstDate) {
		
		List<Report> reports  = ReportUtils.getHistoryReport(reportType, firstDate);
		
		reports.stream().parallel().forEach(t -> {
			t.setAccountId(accountId);
			String date = DateUtil.formatDatetime(new Date());
			t.setUpdateTime(date);
			t.setCreateTime(date);
			setOrderSum(accountId, t);
			setFund(accountId, t);
		});
		// 过滤掉定订单数为0的记录
		reports = reports.stream().parallel().filter(t -> t.getOrderNum() > 0)
				.collect(Collectors.toList());
		//计算结余
		setBalance(balance, reports);
		
		replaceAll(reports);
	}
	
	private void replaceAll(List<Report> reports) {
		int updateCount = 0;
		int insertCount = 0;
		for(Report report : reports) {
			Report rpt =
					reportExtMapper.selectUnique(
							report.getAccountId(), report.getType(), report.getStartDate());
			
			if (rpt == null) {
				reportMapper.insertSelective(report);
				insertCount++;
			} else {
				report.setId(rpt.getId());
				reportMapper.updateByPrimaryKeySelective(report);
				updateCount++;
			}
		}
		log.info("更新报表" + updateCount + "条, 新增" + insertCount + "条");
	}
	
	/*
	 * 计算初始余额， 结余
	 */
	private void setBalance(BigDecimal balance,  List<Report> reports) {
		for (Report report : reports) {
			BigDecimal deposit = report.getDeposit();
			BigDecimal withdraw = report.getWithdraw();
			BigDecimal other = report.getOther();
			BigDecimal preBalance = DecimalUtil.subtract(
					balance, deposit, withdraw, other, report.getRealProfit());
			
			report.setPreBalance(preBalance);
			report.setBalance(balance);
			balance = report.getPreBalance();
		}
	}
	/*
	 * 设置真实利润，交易量，订单数，存款，取款，其他收益
	 * 
	 */
	private void setOrderSum(Integer accountId, Report report) {
		TradeOrderParam orderParam = new TradeOrderParam();
		orderParam.setAccountId(accountId);
		orderParam.setCloseStartDate(report.getStartDate());
		orderParam.setCloseEndDate(report.getEndDate());
		OrderSumResult sum = tradeOrderExtMapper.selectSum(orderParam);
		
		if (sum != null) {
			BigDecimal realProfit = DecimalUtil.get(sum.getRealProfit());
			BigDecimal lots = DecimalUtil.get(sum.getLots());
			Integer orderNum = sum.getOrderNum();
			
			report.setCommission(sum.getCommission());
			report.setSwap(sum.getSwap());
			report.setProfit(sum.getProfit());
			report.setRealProfit(realProfit);
			report.setLots(lots);
			report.setOrderNum(orderNum);
		}
	}
	/*
	 * 设置存款，取款，其他收益
	 * 
	 */
	private void setFund(Integer account, Report report) {
		List<FundSumResult> list = 
				tradeFundExtMapper.selectSumByType(account,
						report.getStartDate(), report.getEndDate());
		Map<String, BigDecimal> map = list.stream().collect(
				Collectors.toMap(FundSumResult::getType, FundSumResult::getProfit));
		
		BigDecimal depostit = map.get(FundType.DEPOSIT);
		BigDecimal withdraw = map.get(FundType.WITHDRAW);
		BigDecimal other = map.get(FundType.OTHER);
		
		report.setDeposit(depostit);
		report.setWithdraw(withdraw);
		report.setOther(other);
	}
	
}
