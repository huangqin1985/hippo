package cc.fxqq.hippo.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import cc.fxqq.hippo.dao.ext.PageQuery;
import cc.fxqq.hippo.dto.template.Pager;
import cc.fxqq.hippo.entity.param.PageParam;

public class PageUtil {
	
	public static <T, R> Pager<R> selectPage(PageQuery<T> query, 
			PageParam param, Function<? super T, ? extends R> mapper) {
		int totalRecord = query.selectTotal(param);
		
		List<T> list = query.selectPage(param);
		Pager<R> pager = new Pager<R>();
		pager.setTotalRecord(totalRecord);
		
		int rows = param.getRows();
		int pageCount = totalRecord % rows == 0 ? totalRecord / rows : totalRecord / rows + 1;
		pager.setPageNum(param.getPage());
		pager.setPageCount(pageCount);
		pager.setRows(rows);
		
		int page = param.getPage();
		pager.setPageNum(page);
		pager.setPrePage(page - 1);
		pager.setNextPage(page + 1);
		pager.setHasPrePage(pageCount > 1 && page > 1);
		pager.setHasNextPage(pageCount > 1 && page < pageCount);
		
		List<R> data = list.stream().parallel().map(mapper).collect(Collectors.toList());
		pager.setData(data);
		return pager;
	}

	public static <T> Pager<T> selectPage(PageQuery<T> query, PageParam param) {
		int totalRecord = query.selectTotal(param);
		
		List<T> list = query.selectPage(param);
		
		Pager<T> pager = new Pager<T>();
		pager.setData(list);
		pager.setTotalRecord(totalRecord);
		
		int rows = param.getRows();
		int pageCount = totalRecord % rows == 0 ? totalRecord / rows : totalRecord / rows + 1;
		pager.setPageNum(pageCount);
		pager.setRows(rows);
		
		int page = param.getPage();
		pager.setPageNum(page);
		pager.setPrePage(page - 1);
		pager.setNextPage(page + 1);
		pager.setHasPrePage(pageCount > 1 && page > 1);
		pager.setHasNextPage(pageCount > 1 && page < pageCount);
		
		return pager;
	}
	
	public static <T, R> Pager<R> selectPage(List<T> records, 
			PageParam param, Function<? super T, ? extends R> mapper) {
		int totalRecord = records.size();
		
		int endRecord = Math.min(param.getStartRecord() + param.getRows(),
				totalRecord);
		List<T> list = records.subList(param.getStartRecord(), endRecord);
		Pager<R> pager = new Pager<R>();
		pager.setTotalRecord(totalRecord);
		
		int rows = param.getRows();
		int pageCount = totalRecord % rows == 0 ? totalRecord / rows : totalRecord / rows + 1;
		pager.setPageNum(param.getPage());
		pager.setPageCount(pageCount);
		pager.setRows(rows);
		
		int page = param.getPage();
		pager.setPageNum(page);
		pager.setPrePage(page - 1);
		pager.setNextPage(page + 1);
		pager.setHasPrePage(pageCount > 1 && page > 1);
		pager.setHasNextPage(pageCount > 1 && page < pageCount);
		
		List<R> data = list.stream().parallel().map(mapper).collect(Collectors.toList());
		pager.setData(data);
		return pager;
	}
}
