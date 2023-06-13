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
	
}
