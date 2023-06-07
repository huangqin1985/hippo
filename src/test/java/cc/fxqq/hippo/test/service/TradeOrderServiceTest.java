package cc.fxqq.hippo.test.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.consts.OrderTypeEnum;
import cc.fxqq.hippo.dto.json.OrderMQL;
import cc.fxqq.hippo.dto.json.TradeOrderMQL;
import cc.fxqq.hippo.service.TradeOrderService;
import cc.fxqq.hippo.util.DateUtil;

@SpringBootTest
/* @TestPropertySource("classpath:application.properties") */
public class TradeOrderServiceTest {
	protected final static Logger logger = LoggerFactory.getLogger(TradeOrderServiceTest.class);

	@Autowired
	protected TradeOrderService tradeOrderService;
	
	private TradeOrderMQL create(String ticket, String type, String comment) {
		TradeOrderMQL order = new TradeOrderMQL();
		
		order.setTicket(ticket);
		order.setOpenTime(DateUtil.parseDate("2022-09-22 13:34:08"));
		order.setCloseTime(DateUtil.parseDate("2022-09-22 22:11:22"));
		order.setSymbol("XAUUSD.p");
		order.setLots(new BigDecimal("0.5"));
		order.setCommission(new BigDecimal("-3.5"));
		order.setSwap(new BigDecimal("4.2"));
		order.setProfit(new BigDecimal("200"));
		order.setType(type);
		order.setOpenPrice("1670.50");
		order.setClosePrice("1666.50");
		order.setStopLoss("0");
		order.setTakeProfit("0");
		order.setComment(comment);
		return order;
	}
	
//	@Test
//	@Transactional
//	void testParseOrder() {
//		TradeMQL trade = new TradeMQL();
//		trade.setAccount(232111);
//		trade.setTimeZone(3);
//		trade.setBalance(new BigDecimal("8000.10"));
//		List<TradeOrderMQL> list = Lists.newArrayList();
//		list.add(create("100000", OrderTypeEnum.BUY.getValue(), ""));
//		list.add(create("100001", OrderTypeEnum.SELL.getValue(), ""));
//		list.add(create("200000", OrderTypeEnum.BALANCE.getValue(), "Withdraw 2333"));
//		trade.setTradeOrders(list);
//
//		tradeOrderService.updateHistoryOrders(trade);
//	}
	
//	@Test
//	@Transactional
//	void testUpdateOrder() {
//		TradeMQL trade = new TradeMQL();
//		trade.setAccount(10004);
//		trade.setTimeZone(3);
//		trade.setBalance(new BigDecimal("12701.40"));
//		List<TradeOrderMQL> list = Lists.newArrayList();
//		list.add(create("232226", OrderTypeEnum.SELL.getValue(), ""));
//		trade.setTradeOrders(list);
//
//		tradeOrderService.updateOrder(trade);
//	}
}
