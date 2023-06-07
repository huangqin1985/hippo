package cc.fxqq.hippo.dao.ext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cc.fxqq.hippo.entity.TradeOrder;
import cc.fxqq.hippo.entity.param.TradeOrderParam;
import cc.fxqq.hippo.entity.result.OrderSumResult;

public interface TradeOrderExtMapper extends BatchOperator<TradeOrder>, PageQuery<TradeOrder> {

    List<OrderSumResult> selectSumBySymbol(TradeOrderParam param);
    
    List<OrderSumResult> selectSumByDate(TradeOrderParam param);

    OrderSumResult selectSum(TradeOrderParam param);
    
    BigDecimal selectRealProfit(TradeOrderParam param);
    
    Map<String, String> selectFirstAndLast(Integer accountId);

    Integer selectTradeDays(Integer accountId);
    
    String selectMinOpenTime(Integer accountId);
}