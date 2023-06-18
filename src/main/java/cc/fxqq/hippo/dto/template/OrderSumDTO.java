package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderSumDTO {
	
	private String symbol;
	
	private Integer orderNum;
	
	private BigDecimal lots;

	private BigDecimal commission;
	
	private BigDecimal swap;
	
	private BigDecimal profit;
	
	private BigDecimal realProfit;
	
	private String curDate;
	
	private Integer slCount;
	
	private Integer tpCount;

	private Integer soCount;
	
	private Integer complexCount;

	private Integer pendingCount;

}
