package cc.fxqq.hippo.dto.template;

import java.util.List;

import lombok.Data;

@Data
public class Pager<T> {

	private List<T> data;
	
	private int totalRecord;
	
	private int pageCount;
	
	private int pageNum;
	
	private int rows;
	
	private int nextPage;
	
	private int prePage;
	
	private boolean hasNextPage;
	
	private boolean hasPrePage;
}