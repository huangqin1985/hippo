package cc.fxqq.hippo.entity.result;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderSumResult {
	
	private String symbol;
	
	private Integer orderNum;
	
	private BigDecimal lots;

	private BigDecimal commission;
	
	private BigDecimal swap;
	
	private BigDecimal profit;
	
	private BigDecimal realProfit;
	
	private String curDate;
}
