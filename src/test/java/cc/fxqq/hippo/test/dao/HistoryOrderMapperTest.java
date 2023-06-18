package cc.fxqq.hippo.test.dao;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.dao.HistoryOrderMapper;
import cc.fxqq.hippo.dao.ext.HistoryOrderExtMapper;
import cc.fxqq.hippo.entity.HistoryOrder;
import cc.fxqq.hippo.entity.param.PageParam;
import cc.fxqq.hippo.entity.param.OrderParam;
import cc.fxqq.hippo.entity.result.OrderSumResult;

@SpringBootTest
/* @TestPropertySource("classpath:application.properties") */
public class HistoryOrderMapperTest {
	
	public static final int account = 1;

	@Autowired
	protected HistoryOrderExtMapper HistoryOrderExtMapper;

	@Autowired
	protected HistoryOrderMapper HistoryOrderMapper;

	private HistoryOrder create(String ticket) {
		HistoryOrder order = new HistoryOrder();
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
		List<HistoryOrder> list = Lists.newArrayList();
		list.add(create("100"));
		list.add(create("101"));
		list.add(create("100"));
		
		HistoryOrderExtMapper.replaceBatch(list);
	}
	
	@Test
	public void testSelectSum() {
		OrderParam param = new OrderParam();
		param.setAccountId(1);
		param.setCloseStartDate("2022-09-01");
		param.setCloseEndDate("2022-09-10");
		
		OrderSumResult sum = HistoryOrderExtMapper.selectSum(param);
		BigDecimal realProfit =  sum.getRealProfit();
		BigDecimal lots = sum.getLots();
		Integer orderNum = sum.getOrderNum();
		System.out.println("realProfit=" + realProfit + ",lots=" + lots + ",orderNum=" + orderNum);
	}
	
	@Test
	public void testSelectSumBySymbol() {
		OrderParam param = new OrderParam();
		param.setAccountId(1);
		param.setCloseStartDate("2022-09-01");
		param.setCloseEndDate("2022-09-10");
		param.setOrderBy("real_profit desc");
		
		List<OrderSumResult> result = HistoryOrderExtMapper.selectSumBySymbol(param);
		
		System.out.println(result);
	}
	
	@Test
	public void testSelectByCondition() {
		OrderParam param = new OrderParam();
		param.setAccountId(1);
		param.setSymbol("XAUUSD.p");
		param.setCloseStartDate("2022-09-08");
		param.setCloseEndDate("2022-09-20");
		param.setOrderBy("close_time desc");

		param.setPage(1);
		
		int count = HistoryOrderExtMapper.selectTotal(param);
		List<HistoryOrder> list = HistoryOrderExtMapper.selectPage(param);
		System.out.println("count = " + count + ", size = " + list.size());
		
	}
	
}
