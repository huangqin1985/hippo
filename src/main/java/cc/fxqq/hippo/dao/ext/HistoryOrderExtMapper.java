package cc.fxqq.hippo.dao.ext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cc.fxqq.hippo.entity.HistoryOrder;
import cc.fxqq.hippo.entity.Report;
import cc.fxqq.hippo.entity.param.OrderParam;
import cc.fxqq.hippo.entity.result.OrderDayResult;
import cc.fxqq.hippo.entity.result.OrderSumResult;

public interface HistoryOrderExtMapper extends BatchOperator<HistoryOrder>, PageQuery<HistoryOrder> {

    List<OrderSumResult> selectSumBySymbol(OrderParam param);
    
    List<OrderSumResult> selectSumByDate(OrderParam param);

    OrderSumResult selectSum(OrderParam param);
    
    BigDecimal selectRealProfit(OrderParam param);
    
    Map<String, String> selectFirstAndLast(Integer accountId);

    Integer selectTradeDays(Integer accountId);
    
    String selectMinOpenTime(Integer accountId);
    
    List<OrderDayResult> selectGroupByDay(OrderParam param);
    
    HistoryOrder selectUnique(Integer accountId, String ticket);
    
}