package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MarketDTO {

	private String symbol;
	
	private BigDecimal requiredMargin;

	private String requiredMarginStr;
	
	private BigDecimal buySwapProfit;

	private String buySwapProfitStr;

	private BigDecimal sellSwapProfit;

	private String sellSwapProfitStr;

	private BigDecimal pointProfit;

	private String pointProfitStr;

	private BigDecimal sellPrice;

	private BigDecimal buyPrice;

	private BigDecimal lowPrice;

	private BigDecimal highPrice;
	
	private String spreadProfitStr;

	private Integer digits;

	private Integer spread;
	
	private Boolean allowTrade;
}
