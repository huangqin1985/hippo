package cc.fxqq.hippo.consts;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import cc.fxqq.hippo.dto.json.TradeOrderMQL;

/**
 *
 */
public class FundType {
	
	public static boolean filter(TradeOrderMQL order) {
		String comment = order.getComment();
		if (StringUtils.isNotEmpty(comment) && StringUtils.toRootLowerCase(comment).contains("summary")) {
			return false;
		}

		return true;
	}
}
