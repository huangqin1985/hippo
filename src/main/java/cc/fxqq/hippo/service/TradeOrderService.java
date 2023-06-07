package cc.fxqq.hippo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.consts.CloseTypeEnum;
import cc.fxqq.hippo.consts.FundType;
import cc.fxqq.hippo.consts.OrderTypeEnum;
import cc.fxqq.hippo.dao.AccountMapper;
import cc.fxqq.hippo.dao.ext.TradeFundExtMapper;
import cc.fxqq.hippo.dao.ext.TradeOrderExtMapper;
import cc.fxqq.hippo.dto.json.TradeOrderMQL;
import cc.fxqq.hippo.dto.template.OrderSumDTO;
import cc.fxqq.hippo.dto.template.Pager;
import cc.fxqq.hippo.dto.template.ReportDTO;
import cc.fxqq.hippo.dto.template.TradeOrderDTO;
import cc.fxqq.hippo.entity.Account;
import cc.fxqq.hippo.entity.TradeFund;
import cc.fxqq.hippo.entity.TradeOrder;
import cc.fxqq.hippo.entity.param.TradeOrderParam;
import cc.fxqq.hippo.entity.result.OrderSumResult;
import cc.fxqq.hippo.notify.MailService;
import cc.fxqq.hippo.socket.terminal.SocketHandler;
import cc.fxqq.hippo.util.BatchUtil;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;
import cc.fxqq.hippo.util.PageUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TradeOrderService {
	
	@Autowired
	private TradeOrderExtMapper tradeOrderExtMapper;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private TradeFundExtMapper tradeFundExtMapper;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private AccountMapper accountMapper;
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public Pager<TradeOrderDTO> getTradeOrderList(TradeOrderParam query) {
		Pager<TradeOrderDTO> pager = PageUtil.selectPage(tradeOrderExtMapper, query,
			t -> {
				TradeOrderDTO dto = new TradeOrderDTO();
				dto.setTicket(t.getTicket());
				dto.setSymbol(t.getSymbol());
				dto.setOpenPrice(t.getOpenPrice());
				dto.setClosePrice(t.getClosePrice());
				dto.setType(t.getType());
				dto.setLots(t.getLots());
				dto.setCommission(t.getCommission());
				dto.setSwap(t.getSwap());
				dto.setStopLoss(t.getStopLoss());
				dto.setTakeProfit(t.getTakeProfit());
				dto.setProfit(t.getProfit());
				dto.setRealProfit(t.getRealProfit());
				dto.setComment(t.getComment());
				
				String type = t.getType();
				String openPrice = t.getOpenPrice();
				String closePrice = t.getClosePrice();
				if ("sell".equals(type)) {
					dto.setPoints(DecimalUtil.removePecimalPoint(
							new BigDecimal(openPrice).subtract(new BigDecimal(closePrice))));
				}
				if ("buy".equals(type)) {
					dto.setPoints(DecimalUtil.removePecimalPoint(
							new BigDecimal(closePrice).subtract(new BigDecimal(openPrice))));
				}
				dto.setOpenTime(t.getOpenTime());
				dto.setCloseTime(t.getCloseTime());
				dto.setCloseType(CloseTypeEnum.parse(t.getComment()));
				dto.setDuration(DateUtil.getSecondDuration(t.getOpenTime(), t.getCloseTime()));
				
				return dto;
			});
		
		return pager;
	}
	
	/**
	 * 
	 * @param account
	 * @return
	 */
	public ReportDTO queryOrderTotal(Integer account) {
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(account);
		OrderSumResult orderSum = tradeOrderExtMapper.selectSum(param);
		
		ReportDTO report = new ReportDTO();
		if (orderSum != null) {
			report.setOrderNum(orderSum.getOrderNum());
			report.setLots(DecimalUtil.get(orderSum.getLots()));
			report.setRealProfit(DecimalUtil.get(orderSum.getRealProfit()));
		
			Map<String, String> map = tradeOrderExtMapper.selectFirstAndLast(account);
			if (map != null) {
				report.setStartDate(DateUtil.getDate(map.get("firstDate")));
				report.setEndDate(DateUtil.getDate(map.get("lastDate")));
			}
		}
		return report;
	}
	
	public String queryFirstOrderDate(Integer account) {
		Map<String, String> map = tradeOrderExtMapper.selectFirstAndLast(account);
		if (map != null) {
			return DateUtil.getDate(map.get("firstDate"));
		}
		return null;
	}
	
	/**
	 * 
	 * @param order
	 */
	public void updateOrder(Account acc, List<TradeOrderMQL> orders) {
		int orderCount = 0;
		int fundCount = 0;
		for (TradeOrderMQL order : orders) {
			
			String type = order.getType();
			// 订单类型为余额
			if (OrderTypeEnum.BALANCE.getValue().equals(type)) {
				String comment = order.getComment();
				String balanceType = FundType.parse(comment);
				
				if (balanceType == null) {
					continue;
				}
				
				TradeFund bal = new TradeFund();
				bal.setAccountId(acc.getId());
				bal.setComment(comment);
				bal.setOpenTime(DateUtil.getUTF8Time(order.getOpenTime(), acc.getTimeZone()));
				bal.setProfit(order.getProfit());
				bal.setTicket(order.getTicket());
				bal.setType(balanceType);
				
				tradeFundExtMapper.replaceBatch(Lists.newArrayList(bal));
				fundCount++;
			} else if (OrderTypeEnum.BUY.getValue().equals(type) ||
					OrderTypeEnum.SELL.getValue().equals(type)) {
				TradeOrder to = new TradeOrder();
				to.setAccountId(acc.getId());
				to.setTicket(order.getTicket());
				to.setOpenTime(DateUtil.getUTF8Time(order.getOpenTime(), acc.getTimeZone()));
				to.setCloseTime(DateUtil.getUTF8Time(order.getCloseTime(), acc.getTimeZone()));
				to.setSymbol(order.getSymbol());
				to.setLots(order.getLots());
				
				BigDecimal commission = order.getCommission();
				BigDecimal swap = order.getSwap();
				BigDecimal profit = order.getProfit();
				BigDecimal realProfit = DecimalUtil.add(commission, swap, profit);
				to.setCommission(commission);
				to.setSwap(swap);
				to.setProfit(profit);
				to.setRealProfit(realProfit);
				to.setType(order.getType());
			    to.setOpenPrice(order.getOpenPrice());
			    to.setClosePrice(order.getClosePrice());
			    to.setStopLoss(order.getStopLoss());
			    to.setTakeProfit(order.getTakeProfit());
			    to.setComment(order.getComment());
			    
			    // 通知
//			    String comment = order.getComment();
//			    String prefix = null;
//			    if (comment.contains("tp")) {
//			    	prefix = "tp";
//			    } else if (comment.contains("sl")) {
//			    	prefix = "sl";
//			    } else if (comment.contains("so")) {
//			    	prefix = "so";
//			    }
//			    if (prefix != null) {
//			    	mailService.send(prefix, acc.getAccount(), order);
//			    }
				
				tradeOrderExtMapper.replaceBatch(Lists.newArrayList(to));
				orderCount++;
			}
		}
		if (fundCount + orderCount > 0) {
			log.info("新增订单" + orderCount + "条,资金" + fundCount + "条");
		}

		reportService.updateCurrentHistoryReport(acc.getId(), acc.getBalance());
	}
	
	/**
	 * 
	 * @param data
	 */
	public void updateHistoryOrders(Account acc, List<TradeOrderMQL> orders) {
		
		Integer accountId = acc.getId();
		
		List<TradeOrder> tradeOrders = Lists.newArrayList();
		List<TradeFund> funds = Lists.newArrayList();
		
		// 转换为do
		for (TradeOrderMQL order : orders) {
			String type = order.getType();
			// 订单类型为余额
			if (OrderTypeEnum.BALANCE.getValue().equals(type)) {
				String comment = order.getComment();
				String balanceType = FundType.parse(comment);
				
				if (balanceType == null) {
					continue;
				}
				
				TradeFund bal = new TradeFund();
				bal.setAccountId(accountId);
				bal.setComment(comment);
				bal.setOpenTime(DateUtil.getUTF8Time(order.getOpenTime(), acc.getTimeZone()));
				bal.setProfit(order.getProfit());
				bal.setTicket(order.getTicket());
				bal.setType(balanceType);
				funds.add(bal);
			} else if (OrderTypeEnum.BUY.getValue().equals(type) ||
					OrderTypeEnum.SELL.getValue().equals(type)) {
				TradeOrder to = new TradeOrder();
				to.setAccountId(accountId);
				to.setTicket(order.getTicket());
				to.setOpenTime(DateUtil.getUTF8Time(order.getOpenTime(), acc.getTimeZone()));
				to.setCloseTime(DateUtil.getUTF8Time(order.getCloseTime(), acc.getTimeZone()));
				to.setSymbol(order.getSymbol());
				to.setLots(order.getLots());
				
				BigDecimal commission = order.getCommission();
				BigDecimal swap = order.getSwap();
				BigDecimal profit = order.getProfit();
				BigDecimal realProfit = DecimalUtil.add(commission, swap, profit);
				to.setCommission(commission);
				to.setSwap(swap);
				to.setProfit(profit);
				to.setRealProfit(realProfit);
				to.setType(order.getType());
			    to.setOpenPrice(order.getOpenPrice());
			    to.setClosePrice(order.getClosePrice());
			    to.setStopLoss(order.getStopLoss());
			    to.setTakeProfit(order.getTakeProfit());
			    
			    to.setComment(order.getComment());
				 
				tradeOrders.add(to);
			}
		}
		
		BatchUtil.replaceBatch(tradeOrderExtMapper, tradeOrders);
		BatchUtil.replaceBatch(tradeFundExtMapper, funds);

		log.info("更新历史订单" + tradeOrders.size() + "条,历史资金" + funds.size() + "条");
		
		reportService.updateHistoryReport(acc.getId(), acc.getBalance());
		
	}
	
	/**
	 * 
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<OrderSumDTO> querySumGroupSymbol(Integer account, String startDate, String endDate) {
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setOrderBy("real_profit desc");
		
		List<OrderSumResult> results = tradeOrderExtMapper.selectSumBySymbol(param);
		List<OrderSumDTO> dtos = Lists.newArrayList();
		results.stream().forEach(t -> {
			OrderSumDTO f = new OrderSumDTO();
			BeanUtils.copyProperties(t, f);
			dtos.add(f);
		});
		return dtos;
	}
	
	public List<OrderSumDTO> querySumGroupDate(Integer account,
			String startDate, String endDate) {
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		
		List<OrderSumResult> results = tradeOrderExtMapper.selectSumByDate(param);
		List<OrderSumDTO> dtos = Lists.newArrayList();
		results.stream().forEach(t -> {
			OrderSumDTO f = new OrderSumDTO();
			BeanUtils.copyProperties(t, f);
			
			dtos.add(f);
		});
		return dtos;
	}
	
	/**
	 * 
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public OrderSumDTO querySum(Integer account, String startDate, String endDate) {
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setOrderBy("real_profit desc");
		
		OrderSumResult result = tradeOrderExtMapper.selectSum(param);
		OrderSumDTO dto = new OrderSumDTO();
		BeanUtils.copyProperties(result, dto);
		
		return dto;
	}
}