package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SymbolGroupDTO {
	
	private Integer total;
	
	private String symbol;
	
	private BigDecimal lots;
	
	private BigDecimal sellLots;
	
	private BigDecimal buyLots;

	private String type;

	private String sellPercent;
	
	private String buyPercent;
	
	private BigDecimal allProfit;

}
