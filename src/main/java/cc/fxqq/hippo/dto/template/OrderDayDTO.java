package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderDayDTO {
	
	private String description;
	
	private String startDate;
	
	private String endDate;
	
	private BigDecimal balance;
	
	private String orderNum;
	
	private BigDecimal realProfit;
	
	private BigDecimal lots;

}
