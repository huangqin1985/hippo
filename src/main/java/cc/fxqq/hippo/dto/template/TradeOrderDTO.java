package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TradeOrderDTO {
	
	private String ticket;

	private String symbol;

	private BigDecimal lots;
	
	private BigDecimal commission;

	private BigDecimal swap;

	private BigDecimal profit;

	private BigDecimal realProfit;
	
	private String closeTime;
	
	private String openTime;
	
	private String stopLoss;
	
	private String takeProfit;
	
	private String durationStr;
	
	private String points;
	
	private String duration;
	
	private String closeType;

	private String type;
	
	private String openPrice;

	private String closePrice;
	
	private String comment;
}
