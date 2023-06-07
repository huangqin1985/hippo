package cc.fxqq.hippo.consts;

/**
 * 订单类型
 *
 */
public enum OrderTypeEnum {
	BUY("buy"),
	
	SELL("sell"),
	
	BALANCE("balance");
	
	private String value;
	
	private OrderTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
