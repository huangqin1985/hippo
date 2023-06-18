package cc.fxqq.hippo.consts;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class FundType {
	
	public final static String DEPOSIT = "deposit";
	public final static String WITHDRAW = "withdraw";
	public final static String OTHER = "other";
	public final static String SUMMARY = "summary";
	
	public static String parse(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		if (StringUtils.toRootLowerCase(str).contains("deposit")) {
			return DEPOSIT;
		}
		if (StringUtils.toRootLowerCase(str).contains("withdraw")) {
			return WITHDRAW;
		}
		if (StringUtils.toRootLowerCase(str).contains("summary")) {
			return SUMMARY;
		}
		return OTHER;
	}
}
