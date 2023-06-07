package cc.fxqq.hippo.consts;

/**
 *
 */
public enum ReportTypeEnum {
	MONTH("month"),
	WEEK("week"),
	DAY("day");
	
	private String value;
	
	private ReportTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

}
