package cc.fxqq.hippo.test;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;

import cc.fxqq.hippo.dto.json.TradeOrderMQL;

class ListTests {
	
	@Test
	void test3() {	
		BigDecimal b1 = new BigDecimal("0.433232");
		b1 = b1.setScale(2, RoundingMode.HALF_DOWN);
		System.out.println(b1);
	}

	@Test
	void test() {	
		int maxSend = 4;

		List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		int limit = (list.size() + maxSend - 1) / maxSend;
		
		Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
	          List<Integer> subList = list.stream().skip(i * maxSend).limit(maxSend).collect(Collectors.toList());
	          System.out.println(subList);
		});
	}
	
	@Test
	void test1() {	
		String json = "{\"cfd\":9.91,\"misc\":500.00}";
		JSONObject obj = JSON.parseObject(json);
		
		Map<String, BigDecimal> map =
				JSON.parseObject(json, new TypeReference<Map<String, BigDecimal>>() {});
		System.out.println(map);
		
	}
	
	@Test
	void test7() {
		List<TradeOrderMQL> trades = Lists.newArrayList();
		TradeOrderMQL trade = new TradeOrderMQL();
		trade.setSymbol("XAUUSD.p");
		trade.setType("sell");
		trade.setLots(new BigDecimal(1.0));
		trade.setProfit(new BigDecimal(50));
		trades.add(trade);
		
		trade = new TradeOrderMQL();
		trade.setSymbol("XAUUSD.p");
		trade.setType("buy");
		trade.setLots(new BigDecimal(0.5));
		trade.setProfit(new BigDecimal(-21));
		trades.add(trade);
		
		Map<String, List<TradeOrderMQL>> map = 
				trades.stream().filter(t -> {
					return "buy".equals(t.getType());
				}).collect(Collectors.groupingBy(TradeOrderMQL::getSymbol));
		
		List<TradeOrderMQL> result = Lists.newArrayList();
		for (String key : map.keySet()) {
			TradeOrderMQL mql = new TradeOrderMQL();
			mql.setSymbol(key);
			List<TradeOrderMQL> ll = map.get(key);
			
			Double lots = ll.stream().collect(Collectors.summingDouble(t -> {
				if (t.getType().equals("buy")) {
					return t.getLots().doubleValue();
				} else {
					return -t.getLots().doubleValue();
				}
			})).doubleValue();
			mql.setLots(new BigDecimal(lots));
			Double profit = ll.stream().collect(Collectors.summingDouble(t -> {
				return t.getProfit().doubleValue();
			})).doubleValue();
			
			mql.setProfit(new BigDecimal(profit));
			result.add(mql);
		}
		System.out.println(result);
	}
}
