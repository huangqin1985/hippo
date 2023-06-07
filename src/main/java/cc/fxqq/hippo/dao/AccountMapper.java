package cc.fxqq.hippo.dao;

import cc.fxqq.hippo.entity.Account;

public interface AccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Account row);

    int insertSelective(Account row);

    Account selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Account row);

    int updateByPrimaryKey(Account row);
}