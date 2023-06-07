package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AccountDTO {
	
	private Integer id;
	
	private String symbol; // 账号名称
	
	private Integer number; // 号码

	private String company; // 交易商
	
	private String server; // 服务器
	
	private String name; // 名称
	
	private String currency; // 货币
	
	private Integer leverage; // 杠杠
	
	private BigDecimal balance; // 余额
	
	private String clientName;
	
	private String stopOutLevel;
	
	private String depositDate;
	
	private String tradeDate;
	
	private Integer status;
	
	private BigDecimal income;
	
	private String connectTime;

	private Integer timeZone; // 时区
}
