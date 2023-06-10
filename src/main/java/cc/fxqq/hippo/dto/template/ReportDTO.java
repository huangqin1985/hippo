package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;
import java.util.List;

import cc.fxqq.hippo.entity.result.OrderDayResult;
import lombok.Data;

@Data
public class ReportDTO {

	private String startDate;
	
	private String endDate;
	
	private String description;

	private BigDecimal preBalance;
	
	private BigDecimal balance;

	private BigDecimal realProfit;
	
	private BigDecimal lots;
	
	private Integer orderNum;
	
	private BigDecimal deposit;
	
	private BigDecimal withdraw;

	/*
	 * 最大净值
	 */
	private BigDecimal maxEquity;
	
	/*
	 * 最小净值
	 */
	private BigDecimal minEquity;
	
	private BigDecimal maxRealProfit;
	
	private BigDecimal minRealProfit;

	private BigDecimal maxProfit;
	
	private BigDecimal minProfit;

	private BigDecimal maxMargin;

	private String minMarginRateStr;
	
	private boolean thisWeek;
	
	private List<OrderDayDTO> dayResults;
}
