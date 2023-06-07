package cc.fxqq.hippo.dto.json;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class PositionMQL {
	
	//private String serverTime;
	
	private BigDecimal equity; // 净值

	private BigDecimal profit; // 盈亏
	
	private BigDecimal margin; // 预付款

	//private List<TradeOrderMQL> orders;

}
