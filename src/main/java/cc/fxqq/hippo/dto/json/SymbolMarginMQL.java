package cc.fxqq.hippo.dto.json;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class SymbolMarginMQL {

	private String symbol;
	
	private BigDecimal margin;
	
	private BigDecimal lots;

	private String type;
}
