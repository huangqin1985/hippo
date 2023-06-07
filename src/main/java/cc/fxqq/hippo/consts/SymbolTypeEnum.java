package cc.fxqq.hippo.consts;

/**
 *
 */
public enum SymbolTypeEnum {
	/**
	 * 外汇黄金
	 */
	FOREX("1"),	
	/**
	 * 股指原油等
	 */
	OTHER("2");
	
	private String value;
	
	private SymbolTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
