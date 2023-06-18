package cc.fxqq.hippo.dao.ext;

import java.math.BigDecimal;
import java.util.List;

import cc.fxqq.hippo.entity.Fund;
import cc.fxqq.hippo.entity.result.FundSumResult;

public interface FundExtMapper extends BatchOperator<Fund>, PageQuery<Fund> {

    List<FundSumResult> selectSumByType(Integer accountId, String startDate, String endDate);

    BigDecimal selectProfitByType(Integer accountId, String type, String startDate, String endDate);

    String selectTradeOpenTime(Integer accountId);
}