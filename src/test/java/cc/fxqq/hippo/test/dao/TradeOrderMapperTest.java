package cc.fxqq.hippo.test.dao;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.dao.TradeOrderMapper;
import cc.fxqq.hippo.dao.ext.TradeOrderExtMapper;
import cc.fxqq.hippo.entity.TradeOrder;
import cc.fxqq.hippo.entity.param.PageParam;
import cc.fxqq.hippo.entity.param.TradeOrderParam;
import cc.fxqq.hippo.entity.result.OrderSumResult;

@SpringBootTest
/* @TestPropertySource("classpath:application.properties") */
public class TradeOrderMapperTest {
	
	public static final int account = 1;

	@Autowired
	protected TradeOrderExtMapper tradeOrderExtMapper;

	@Autowired
	protected TradeOrderMapper TradeOrderMapper;

	private TradeOrder create(String ticket) {
		TradeOrder order = new TradeOrder();
		order.setAccountId(account);
		order.setTicket(ticket);
		order.setOpenTime("2022-09-06 22:00:08");
		order.setCloseTime("2022-09-08 22:00:08");
		order.setSymbol("XAUUSD");
		order.setLots(new BigDecimal("1.5"));
		order.setCommission(new BigDecimal("0.7"));
		order.setSwap(BigDecimal.ZERO);
		order.setProfit(new BigDecimal("56.11"));
		order.setRealProfit(new BigDecimal("100"));
		order.setType("sell");
		order.setOpenPrice("1730.12");
		order.setClosePrice("1721.00");
		order.setComment("-----");
		return order;
	}

	@Test
	@Transactional
	public void testReplaceBatch() {
		List<TradeOrder> list = Lists.newArrayList();
		list.add(create("100"));
		list.add(create("101"));
		list.add(create("100"));
		
		tradeOrderExtMapper.replaceBatch(list);
	}
	
	@Test
	public void testSelectSum() {
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(1);
		param.setCloseStartDate("2022-09-01");
		param.setCloseEndDate("2022-09-10");
		
		OrderSumResult sum = tradeOrderExtMapper.selectSum(param);
		BigDecimal realProfit =  sum.getRealProfit();
		BigDecimal lots = sum.getLots();
		Integer orderNum = sum.getOrderNum();
		System.out.println("realProfit=" + realProfit + ",lots=" + lots + ",orderNum=" + orderNum);
	}
	
	@Test
	public void testSelectSumBySymbol() {
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(1);
		param.setCloseStartDate("2022-09-01");
		param.setCloseEndDate("2022-09-10");
		param.setOrderBy("real_profit desc");
		
		List<OrderSumResult> result = tradeOrderExtMapper.selectSumBySymbol(param);
		
		System.out.println(result);
	}
	
	@Test
	public void testSelectByCondition() {
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(1);
		param.setSymbol("XAUUSD.p");
		param.setCloseStartDate("2022-09-08");
		param.setCloseEndDate("2022-09-20");
		param.setOrderBy("close_time desc");

		param.setPage(1);
		
		int count = tradeOrderExtMapper.selectTotal(param);
		List<TradeOrder> list = tradeOrderExtMapper.selectPage(param);
		System.out.println("count = " + count + ", size = " + list.size());
		
	}
	
}
