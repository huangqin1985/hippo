package cc.fxqq.hippo.entity.param;

import lombok.Data;

@Data
public abstract class PageParam {

	/**
	 * 页数
	 */
	private int page = 1;
	
	/**
	 * 每页数量
	 */
	private int rows = 50;
	
	private int startRecord = -1;

	public int getStartRecord() {
		if (startRecord >= 0) {
			return startRecord;
		} else {
			return (page - 1) * rows;
		}
	}

}