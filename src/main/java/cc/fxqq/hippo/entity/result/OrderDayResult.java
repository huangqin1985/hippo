package cc.fxqq.hippo.entity.result;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderDayResult {
	
	private String date;
	
	private String orderNum;
	
	private BigDecimal realProfit;
	
	private BigDecimal lots;
	
}
