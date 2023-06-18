package cc.fxqq.hippo.consts;

/**
 *
 */
public enum ComplexOrderStatusEnum {
	CLOSED("closed"),
	TRADING("trading");
	
	private String value;
	
	private ComplexOrderStatusEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
