package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ComplexOrderDTO {
	
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
	
	private String type;
	
	private String openPrice;

	private String closePrice;
	
	private String comment;
	
	private String status;
	
	private Integer childrenCount;
	
	private String points;
	
	private String closeType;
	
	private String duration;
	
	private List<ComplexOrderDTO> children;
}
