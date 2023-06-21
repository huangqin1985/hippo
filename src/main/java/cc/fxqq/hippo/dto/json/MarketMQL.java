package cc.fxqq.hippo.dto.json;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class MarketMQL {

	private String symbol;
	
	private BigDecimal requiredMargin;
	
	private BigDecimal buySwapProfit;

	private BigDecimal sellSwapProfit;

	private BigDecimal pointProfit;

	private BigDecimal buyPrice;

	private BigDecimal sellPrice;

	private BigDecimal lowPrice;

	private BigDecimal highPrice;
	
	private BigDecimal openD1;
	
	private Integer spread;
	
	private String path;

	private Integer digits;
	
	private Boolean allowTrade;
}
