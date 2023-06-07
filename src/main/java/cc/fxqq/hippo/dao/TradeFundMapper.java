package cc.fxqq.hippo.dao;

import cc.fxqq.hippo.entity.TradeFund;

public interface TradeFundMapper {
    int insert(TradeFund row);

    int insertSelective(TradeFund row);
}