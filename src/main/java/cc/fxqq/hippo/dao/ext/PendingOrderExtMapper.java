package cc.fxqq.hippo.dao.ext;

import cc.fxqq.hippo.entity.PendingOrder;

public interface PendingOrderExtMapper extends BatchOperator<PendingOrder>, PageQuery<PendingOrder> {

	void updateUnique(Integer accountId, String ticket, String status);
}