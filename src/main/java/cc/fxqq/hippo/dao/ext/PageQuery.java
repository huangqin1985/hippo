package cc.fxqq.hippo.dao.ext;

import java.util.List;

import cc.fxqq.hippo.entity.param.PageParam;

/**
 * 分页查询
 * @author huangqin
 *
 * @param <T>
 */
public interface PageQuery<T> {

	public List<T> selectPage(PageParam param);
	
	public int selectTotal(PageParam param);
}
