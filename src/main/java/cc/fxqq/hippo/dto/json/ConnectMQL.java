package cc.fxqq.hippo.dto.json;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class ConnectMQL {
	
	private Integer number; // 账号
	
	private String company; // 交易商
	
	private String server; // 服务器

	private String currency; // 货币
	
	private Integer leverage; // 杠杠
	
	private BigDecimal balance; // 余额

	private Integer timeZone; // 时区
	
	private String stopOutLevel;
	
	private String clientName;
	
	private Map<String, List<SymbolMarginMQL>> symbolMargins;
	
	private List<TradeOrderMQL> histories; //  历史订单
}
