package cc.fxqq.hippo.entity.param;

import lombok.Data;

@Data
public class OrderParam extends PageParam {
	
	private Integer accountId;
	
	private String ticket;
	
	private String symbol;
	
	private String openPrice;
	
	private String openTime;
	
	private String closeStartDate;
	
	private String closeEndDate;
	
	private String openStartDate;
	
	private String openEndDate;
	
	private boolean tp;
	
	private boolean sl;
	
	private boolean so;
	
	private boolean close;

	private boolean passDay;

	private boolean passWeekend;
	
	private String status;
	
	private String commentText;
	
	
	private String orderBy;
	
}
