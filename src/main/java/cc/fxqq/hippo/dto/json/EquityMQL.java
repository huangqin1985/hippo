package cc.fxqq.hippo.dto.json;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class EquityMQL {
	
	private String account;

	private BigDecimal equity;
	
	private BigDecimal profit;
}
