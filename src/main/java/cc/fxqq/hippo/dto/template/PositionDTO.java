package cc.fxqq.hippo.dto.template;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.dto.json.PositionMQL;
import cc.fxqq.hippo.dto.json.OrderMQL;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;
import lombok.Data;

@Data
public class PositionDTO {
	
	public PositionDTO() {
	}
	
	public PositionDTO(PositionMQL arg) {
		trading = Lists.newArrayList();
		pending = Lists.newArrayList();
		
		if (arg == null) {
			return;
		}
		
		//serverTime = arg.getServerTime();
		profitColor = getColor(arg.getProfit());
		profit = DecimalUtil.format(arg.getProfit());
		equity = DecimalUtil.format(arg.getEquity());
		freeMargin = DecimalUtil.format(arg.getFreeMargin());
		marginLevel = DecimalUtil.format(arg.getMarginLevel()) + "%";
		margin = DecimalUtil.format(arg.getMargin());
		
		BigDecimal sw = new BigDecimal(
				arg.getOrders().stream().mapToDouble(t -> t.getTodaySwap().doubleValue()).sum());
		swap =  DecimalUtil.format(sw);
		
		
		Map<String, List<OrderMQL>> map = 
				arg.getOrders().stream().filter(order -> {
					if ("0".equals(order.getType()) || "1".equals(order.getType())) {
						return true;
					} else {
						return false;
					}
				}).collect(Collectors.groupingBy(OrderMQL::getSymbol));
		List<OrderMQL> pendingList = arg.getOrders().stream().filter(order -> {
			if ("0".equals(order.getType()) || "1".equals(order.getType())) {
				return false;
			} else {
				return true;
			}
		}).collect(Collectors.toList());
		pending = pendingList.stream().map(t -> {
			POrder pd = new POrder();
			pd.setSymbol(t.getSymbol());
			
			if (t.getType().equals("2") || t.getType().equals("4")) {
				pd.setTypeStr("buy");
				pd.setTypeColor("g");
			} else if (t.getType().equals("3") || t.getType().equals("5")) {
				pd.setTypeStr("sell");
				pd.setTypeColor("r");
			}

			pd.setLots(DecimalUtil.formatFull(t.getLots()));
			
			pd.setOpenPrice(t.getOpenPrice());
			pd.setClosePrice(t.getClosePrice());
			
			pd.setTakeProfit(t.getTakeProfit());
			pd.setStopLoss(t.getStopLoss());
			
			pd.setMaxLoss(DecimalUtil.formatFull(t.getMaxLoss()));
			pd.setMaxProfit(DecimalUtil.formatFull(t.getMaxProfit()));
			pd.setMaxLossPoint(t.getMaxLossPoint());
			pd.setMaxProfitPoint(t.getMaxProfitPoint());
			
			pd.setMargin(DecimalUtil.formatFull(t.getMargin()));
			BigDecimal margin = t.getMargin();
			if (margin.compareTo(arg.getFreeMargin()) < 0) {
				pd.setMarginColor("g");
			} else {
				pd.setMarginColor("r");
			}
			
			return pd;
		}).collect(Collectors.toList());
		
		BigDecimal maxLoss = BigDecimal.ZERO;
		BigDecimal maxProfit = BigDecimal.ZERO;
		
		for (String key : map.keySet()) {
			POrder po = new POrder();
			po.setSymbol(key);
			
			List<OrderMQL> ll = map.get(key);
			
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
			po.setLots(DecimalUtil.formatFull(new BigDecimal(lots).abs()));
			
			Double profit = ll.stream().collect(Collectors.summingDouble(t -> {
				return t.getProfit().doubleValue();
			})).doubleValue();
			
			po.setProfit(DecimalUtil.formatFull(new BigDecimal(profit)));
			po.setProfitColor(getColor(new BigDecimal(profit)));
			
			for(OrderMQL tom : ll) {
				if (maxLoss != null) {
					maxLoss = DecimalUtil.add(tom.getMaxLoss(), maxLoss);
				}
				if (maxProfit != null) {
					maxProfit = DecimalUtil.add(tom.getMaxProfit(), maxProfit);
				}
				if (new BigDecimal(tom.getStopLoss()).compareTo(BigDecimal.ZERO) == 0) {
					maxLoss = null;
				}
				if (new BigDecimal(tom.getTakeProfit()).compareTo(BigDecimal.ZERO) == 0) {
					maxProfit = null;
				}
			}
			
			po.setOrderNum(ll.size());
			
			List<POrder> orders = ll.stream().map(t ->{
				POrder pp = new POrder();
				pp.setSymbol(t.getSymbol());
				
				if (t.getType().equals("1")) {
					pp.setTypeStr("sell");
					pp.setTypeColor("r");
				} else if (t.getType().equals("0")) {
					pp.setTypeStr("buy");
					pp.setTypeColor("g");
				}

				pp.setLots(DecimalUtil.formatFull(t.getLots()));
				pp.setOpenTime(DateUtil.format(t.getOpenTime(), DateUtil.DATETIME_FORMAT));
				
				pp.setOpenPrice(t.getOpenPrice());
				pp.setClosePrice(t.getClosePrice());
				
				pp.setProfit(DecimalUtil.format(t.getProfit()));
				pp.setProfitColor(getColor(t.getProfit()));
				pp.setTakeProfit(t.getTakeProfit());
				pp.setStopLoss(t.getStopLoss());
				
				if (new BigDecimal(pp.getStopLoss()).compareTo(BigDecimal.ZERO) == 0) {
					pp.setSltpClass("nosltp");
				} else if (new BigDecimal(pp.getTakeProfit()).compareTo(BigDecimal.ZERO) == 0) {
					pp.setSltpClass("nosltp");
				} else {
					pp.setSltpClass("sltp");
				}
				if ("1".equals(t.getType())) {
					pp.setPoints(DecimalUtil.removePecimalPoint(
							new BigDecimal(t.getOpenPrice()).subtract(new BigDecimal(t.getClosePrice()))));
				}
				if ("0".equals(t.getType())) {
					pp.setPoints(DecimalUtil.removePecimalPoint(
							new BigDecimal(t.getClosePrice()).subtract(new BigDecimal(t.getOpenPrice()))));
				}
				pp.setMaxLoss(DecimalUtil.formatFull(t.getMaxLoss()));
				pp.setMaxProfit(DecimalUtil.formatFull(t.getMaxProfit()));
				pp.setMaxLossPoint(t.getMaxLossPoint());
				pp.setMaxProfitPoint(t.getMaxProfitPoint());

				pp.setSwap(DecimalUtil.format(t.getSwap()));
				pp.setCommission(DecimalUtil.format(t.getCommission()));
				pp.setTodaySwap(DecimalUtil.format(t.getTodaySwap()));
				pp.setMargin(DecimalUtil.format(t.getMargin()));
				
				return pp;
			}).collect(Collectors.toList());
			po.setOrders(orders);
			
			trading.add(po);
		}
		
		if (maxLoss == null) {
			this.maxLoss = "∞" ;
		} else {
			this.maxLoss = DecimalUtil.formatFull(maxLoss);
		}
		if (maxProfit == null) {
			this.maxProfit = "∞";
		} else {
			this.maxProfit = DecimalUtil.formatFull(maxProfit);
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

	private String margin; // 净值
	
	private String freeMargin;

	private String marginLevel;
	
	private String swap;

	private String serverTime;
	
	private String profitColor;
	
	private String maxLoss;

	private String maxProfit;

	private List<POrder> trading;
	private List<POrder> pending;
	
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

		private String commission;

		private String swap;

		private String todaySwap;

		private String maxLoss;

		private String maxProfit;

		private Integer maxLossPoint;

		private Integer maxProfitPoint;
		
		private String margin;

		private String marginColor;
		
		private Integer orderNum;
		
		private String sltpClass;
		
		private List<POrder> orders;
	}
}
