package cc.fxqq.hippo.consts;

/**
 * 订单类型
 *
 */
public enum PendingOrderStatusEnum {
	CANCELLED("cancelled"),
	TRADE("trade");
	
	private String value;
	
	private PendingOrderStatusEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
