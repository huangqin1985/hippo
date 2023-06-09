package cc.fxqq.hippo.entity.param;

import lombok.Data;

@Data
public class TradeFundParam extends PageParam {
	
	private Integer accountId;
	
	private String type;
	
	private String startDate;
	
	private String endDate;
	
	private String orderBy;
}
