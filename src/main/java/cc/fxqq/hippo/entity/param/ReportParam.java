package cc.fxqq.hippo.entity.param;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReportParam extends PageParam {
	
	private Integer accountId;
	
	private String type;
	
	private String startDate;
	
	private String endDate;
	
	private BigDecimal balance;

	private BigDecimal commission;
	
	private BigDecimal swap;
	
	private BigDecimal profit;
	
	private BigDecimal realProfit;
	
	private BigDecimal lots;

	private Integer orderNum;
	
	private String orderBy;
}
