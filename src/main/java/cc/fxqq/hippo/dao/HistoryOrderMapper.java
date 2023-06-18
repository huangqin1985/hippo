package cc.fxqq.hippo.dao;

import cc.fxqq.hippo.entity.HistoryOrder;

public interface HistoryOrderMapper {
    int insert(HistoryOrder row);

    int insertSelective(HistoryOrder row);
}