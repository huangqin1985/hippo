package cc.fxqq.hippo.dao.ext;

import java.util.List;

/**
 * 批量操作
 * @author huangqin
 *
 * @param <T>
 */
public interface BatchOperator<T> {

	/**
	 * 批量替换
	 * @param T
	 */
	public int replaceBatch(List<T> T);
}
