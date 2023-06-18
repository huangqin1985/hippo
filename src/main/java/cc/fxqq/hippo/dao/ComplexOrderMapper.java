package cc.fxqq.hippo.dao;

import cc.fxqq.hippo.entity.ComplexOrder;

public interface ComplexOrderMapper {
    int insert(ComplexOrder row);

    int insertSelective(ComplexOrder row);
}