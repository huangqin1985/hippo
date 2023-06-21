package cc.fxqq.hippo.notify;

/**
 *
 */
public enum MailTypeEnum {
	SL("止损"),
	
	TP("止盈"),
	
	SO("爆仓"),

	PENDING("挂单成交");
	
	private String value;
	
	private MailTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
