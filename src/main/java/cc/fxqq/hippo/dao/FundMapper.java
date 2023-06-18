package cc.fxqq.hippo.dao;

import cc.fxqq.hippo.entity.Fund;

public interface FundMapper {
    int insert(Fund row);

    int insertSelective(Fund row);
}