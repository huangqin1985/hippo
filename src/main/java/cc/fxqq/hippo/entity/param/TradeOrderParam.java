package cc.fxqq.hippo.entity.param;

import lombok.Data;

@Data
public class TradeOrderParam extends PageParam {
	
	private Integer accountId;
	
	private String ticket;
	
	private String symbol;
	
	private String closeStartDate;
	
	private String closeEndDate;
	
	private String openStartDate;
	
	private String openEndDate;
	
	private boolean isTp;
	
	private boolean isSl;
	
	private String orderBy;
	
}
