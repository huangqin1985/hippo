package cc.fxqq.hippo.dto.json;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class TradeOrderMQL {

	private String ticket;

	@JSONField (format="yyyy.MM.dd HH:mm:ss")
	private Date openTime;

	@JSONField (format="yyyy.MM.dd HH:mm:ss")
	private Date closeTime;
	
	private String symbol;
	
	private BigDecimal lots;

	private BigDecimal commission;

	private BigDecimal swap;

	private BigDecimal margin;

	private BigDecimal profit;

	private String type;
	
	private String openPrice;

	private String closePrice;

	private String stopLoss;

	private String takeProfit;
	
	private String comment;
	
	private BigDecimal maxLoss;
	
	private BigDecimal maxProfit;

}
