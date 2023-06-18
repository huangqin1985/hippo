package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PendingOrderDTO {
	
	private String ticket;

	private String symbol;

	private BigDecimal lots;
	
	private String closeTime;
	
	private String openTime;
	
	private String stopLoss;
	
	private String takeProfit;
	
	private String type;
	
	private String openPrice;

	private String closePrice;
	
	private String expiration;
	
	private String status;
	
	private HistoryOrderDTO order;
}
