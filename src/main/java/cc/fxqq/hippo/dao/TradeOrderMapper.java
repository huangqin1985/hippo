package cc.fxqq.hippo.dao;

import cc.fxqq.hippo.entity.TradeOrder;

public interface TradeOrderMapper {
    int insert(TradeOrder row);

    int insertSelective(TradeOrder row);
}