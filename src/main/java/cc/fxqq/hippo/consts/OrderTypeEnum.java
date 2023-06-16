package cc.fxqq.hippo.consts;

/**
 * 订单类型
 *
 */
public enum OrderTypeEnum {
	BUY("buy"),
	
	SELL("sell"),

	SELL_LIMIT("sell limit"),

	SELL_STOP("sell stop"),

	BUY_LIMIT("buy limit"),

	BUY_STOP("buy stop"),
	
	BALANCE("balance");
	
	private String value;
	
	private OrderTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
