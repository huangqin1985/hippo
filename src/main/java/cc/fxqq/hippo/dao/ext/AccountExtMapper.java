package cc.fxqq.hippo.dao.ext;

import java.util.List;

import cc.fxqq.hippo.entity.Account;

public interface AccountExtMapper {

    List<Account> selectAccounts();

    Account selectUnique(String name);
}