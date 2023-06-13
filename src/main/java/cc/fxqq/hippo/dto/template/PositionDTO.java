package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.dto.json.PositionMQL;
import cc.fxqq.hippo.dto.json.TradeOrderMQL;
import cc.fxqq.hippo.util.DecimalUtil;
import lombok.Data;

@Data
public class PositionDTO {
	
	public PositionDTO() {
	}
	
	public PositionDTO(PositionMQL arg) {
		trading = Lists.newArrayList();
		
		if (arg == null) {
			return;
		}
		
		//serverTime = arg.getServerTime();
		profitColor = getColor(arg.getProfit());
		profit = DecimalUtil.format(arg.getProfit());
		equity = DecimalUtil.format(arg.getEquity());
		BigDecimal sw = new BigDecimal(
				arg.getOrders().stream().mapToDouble(t -> t.getSwap().doubleValue()).sum());
		swap =  DecimalUtil.format(sw);
		
		Map<String, List<TradeOrderMQL>> map = 
				arg.getOrders().stream().filter(order -> {
					if ("0".equals(order.getType()) || "1".equals(order.getType())) {
						return true;
					} else {
						return false;
					}
				}).collect(Collectors.groupingBy(TradeOrderMQL::getSymbol));
		
		for (String key : map.keySet()) {
			POrder po = new POrder();
			po.setSymbol(key);
			
			List<TradeOrderMQL> ll = map.get(key);
			
			List<String> buyPrice = ll.stream().filter(t -> t.getType().equals("0")).map(t -> t.getClosePrice())
					.collect(Collectors.toList());
			List<String> sellPrice = ll.stream().filter(t -> t.getType().equals("1")).map(t -> t.getClosePrice())
					.collect(Collectors.toList());
			
			Double lots = ll.stream().collect(Collectors.summingDouble(t -> {
				if (t.getType().equals("0")) {
					return t.getLots().doubleValue();
				} else {
					return -t.getLots().doubleValue();
				}
			})).doubleValue();
			if (lots > 0) {
				po.setTypeStr("buy");
				po.setTypeColor("g");
				
			} else if (lots < 0) {
				po.setTypeStr("sell");
				po.setTypeColor("r");
			} else {
				po.setTypeColor("d");
			}
			if (buyPrice.size() > 0) {
				po.setClosePrice(buyPrice.get(0));
			}
			if (sellPrice.size() > 0) {
				po.setClosePrice(sellPrice.get(0));
			}
			po.setLots(DecimalUtil.format(new BigDecimal(lots).abs()));
			
			Double profit = ll.stream().collect(Collectors.summingDouble(t -> {
				return t.getProfit().doubleValue();
			})).doubleValue();
			
			po.setProfit(DecimalUtil.format(new BigDecimal(profit)));
			po.setProfitColor(getColor(new BigDecimal(profit)));
			trading.add(po);
		}
		
	}
	private String getColor(BigDecimal bd) {
		if (bd.compareTo(BigDecimal.ZERO) >= 0) {
			return "g";
		} else {
			return "r";
		}
	}

	private String profit; // 盈亏
	
	private String equity; // 净值
	
	private String swap;
	
	private String serverTime;
	
	private String profitColor;

	private List<POrder> trading;
	
	@Data
	static class POrder {
		private String symbol;

		private String lots;
		
		private String profit;
		
		private String profitColor;

		private String openTime;
		
		private String stopLoss;
		
		private String takeProfit;
		
		private String points;
		
		private String typeStr;
		
		private String typeColor;
		
		private String openPrice;

		private String closePrice;
	}
}
