package cc.fxqq.hippo.dao.ext;

import java.math.BigDecimal;

import cc.fxqq.hippo.entity.TradeFund;

public interface TradeFundExtMapper extends BatchOperator<TradeFund>, PageQuery<TradeFund> {

    BigDecimal selectDeposit(Integer accountId, String startDate, String endDate);

    BigDecimal selectWithdraw(Integer accountId, String startDate, String endDate);
}