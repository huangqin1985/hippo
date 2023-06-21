package cc.fxqq.hippo.consts;

/**
 *
 */
public enum CloseTypeEnum {
	TP("tp"),
	SL("sl"),
	SO("so");
	
	private String value;
	
	private CloseTypeEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public static String parse(String comment) {
		if(comment != null && comment.contains("[sl]")) {
			return SL.value;
		}
		
		if(comment != null && comment.contains("[tp]")) {
			return TP.value;
		}
		
		if(comment != null && comment.contains("so:")) {
			return SO.value;
		}
		return "";
	}
}
