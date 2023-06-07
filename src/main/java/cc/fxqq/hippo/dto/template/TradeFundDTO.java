package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TradeFundDTO {

	private String openTime;
	
	private BigDecimal profit;
	
	private String comment;

}
