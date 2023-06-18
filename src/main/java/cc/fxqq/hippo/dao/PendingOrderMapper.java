package cc.fxqq.hippo.dao;

import cc.fxqq.hippo.entity.PendingOrder;

public interface PendingOrderMapper {
    int insert(PendingOrder row);

    int insertSelective(PendingOrder row);
}