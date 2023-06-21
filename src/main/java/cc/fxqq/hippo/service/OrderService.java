package cc.fxqq.hippo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.cache.AccountCache;
import cc.fxqq.hippo.consts.CloseTypeEnum;
import cc.fxqq.hippo.consts.ComplexOrderStatusEnum;
import cc.fxqq.hippo.consts.FundType;
import cc.fxqq.hippo.consts.OrderTypeEnum;
import cc.fxqq.hippo.consts.PendingOrderStatusEnum;
import cc.fxqq.hippo.consts.ReportTypeEnum;
import cc.fxqq.hippo.dao.ext.FundExtMapper;
import cc.fxqq.hippo.dao.ext.HistoryOrderExtMapper;
import cc.fxqq.hippo.dao.ext.PendingOrderExtMapper;
import cc.fxqq.hippo.dto.json.OrderMQL;
import cc.fxqq.hippo.dto.template.ComplexOrderDTO;
import cc.fxqq.hippo.dto.template.HistoryOrderDTO;
import cc.fxqq.hippo.dto.template.OrderSumDTO;
import cc.fxqq.hippo.dto.template.Pager;
import cc.fxqq.hippo.dto.template.PendingOrderDTO;
import cc.fxqq.hippo.dto.template.ReportDTO;
import cc.fxqq.hippo.entity.ComplexOrder;
import cc.fxqq.hippo.entity.Fund;
import cc.fxqq.hippo.entity.HistoryOrder;
import cc.fxqq.hippo.entity.PendingOrder;
import cc.fxqq.hippo.entity.Report;
import cc.fxqq.hippo.entity.param.OrderParam;
import cc.fxqq.hippo.entity.result.OrderSumResult;
import cc.fxqq.hippo.notify.MailService;
import cc.fxqq.hippo.notify.MailTypeEnum;
import cc.fxqq.hippo.util.BatchUtil;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;
import cc.fxqq.hippo.util.PageUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {
	
	@Autowired
	private HistoryOrderExtMapper historyOrderExtMapper;
	
	@Autowired
	private PendingOrderExtMapper pendingOrderExtMapper;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private FundExtMapper fundExtMapper;
	
	@Autowired
	private MailService mailService;
	
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public Pager<PendingOrderDTO> getPendingOrderList(OrderParam query) {
		Pager<PendingOrderDTO> pager = PageUtil.selectPage(pendingOrderExtMapper, query,
			t -> {
				PendingOrderDTO dto = new PendingOrderDTO();
				dto.setTicket(t.getTicket());
				dto.setSymbol(t.getSymbol());
				dto.setOpenPrice(t.getOpenPrice());
				dto.setClosePrice(t.getClosePrice());
				dto.setType(t.getType());
				dto.setLots(t.getLots());
				dto.setStopLoss(t.getStopLoss());
				dto.setTakeProfit(t.getTakeProfit());
				
				Date expiration = DateUtil.parseDate(t.getExpiration());
				if (expiration == null || expiration.getTime() <= 0) {
					dto.setExpiration("");
				} else {
					dto.setExpiration(t.getExpiration());
				}
				
				dto.setStatus(t.getStatus());
				
				dto.setOpenTime(DateUtil.format(DateUtil.parseDatetime(t.getOpenTime()), DateUtil.FORMAT1));
				dto.setCloseTime(DateUtil.format(DateUtil.parseDatetime(t.getCloseTime()), DateUtil.FORMAT1));
				
				String ticket = t.getTicket();
				HistoryOrder order = historyOrderExtMapper.selectUnique(t.getAccountId(), ticket);
				if (order != null) {
					dto.setOrder(convertToHistoryDTO(order));
				}
				
				return dto;
			});
		
		return pager;
	}
	
	public Pager<ComplexOrderDTO> getComplexOrderList(OrderParam query) {
		Pager<ComplexOrderDTO> pager = PageUtil.selectPage(historyOrderExtMapper, query,
			t -> {
				ComplexOrderDTO dto = new ComplexOrderDTO();
				//BeanUtils.copyProperties(t, dto);
				
				dto.setOpenTime(DateUtil.format(DateUtil.parseDatetime(t.getOpenTime()), DateUtil.FORMAT1));
				dto.setCloseTime(DateUtil.format(DateUtil.parseDatetime(t.getCloseTime()), DateUtil.FORMAT1));
				dto.setDuration(DateUtil.getSecondDuration(t.getOpenTime(), t.getCloseTime()));

				
				OrderParam param = new OrderParam();
				param.setAccountId(t.getAccountId());
				param.setSymbol(t.getSymbol());
				param.setOpenPrice(t.getOpenPrice());
				param.setOpenTime(t.getOpenTime());
				List<HistoryOrder> hlList = historyOrderExtMapper.selectPage(param);
				
				dto.setChildrenCount(hlList.size());
				dto.setOpenPrice(t.getOpenPrice());
				dto.setType(t.getType());
				dto.setSymbol(t.getSymbol());
				
				BigDecimal lots = BigDecimal.ZERO;
				BigDecimal realProfit = BigDecimal.ZERO;
				
				List<ComplexOrderDTO> list = Lists.newArrayList();
				for (HistoryOrder order : hlList) {
					lots = DecimalUtil.add(lots, order.getLots());
					realProfit = DecimalUtil.add(realProfit, order.getRealProfit());
					
					ComplexOrderDTO cd = new ComplexOrderDTO();
					BeanUtils.copyProperties(order, cd);
					
					String type = order.getType();
					String openPrice = order.getOpenPrice();
					String closePrice = order.getClosePrice();
					if (OrderTypeEnum.SELL.getValue().equals(type)) {
						cd.setPoints(DecimalUtil.removePecimalPoint(
								new BigDecimal(openPrice).subtract(new BigDecimal(closePrice))));
					}
					if (OrderTypeEnum.BUY.getValue().equals(type)) {
						cd.setPoints(DecimalUtil.removePecimalPoint(
								new BigDecimal(closePrice).subtract(new BigDecimal(openPrice))));
					}
					cd.setOpenTime(
							DateUtil.format(DateUtil.parseDatetime(order.getOpenTime()), DateUtil.FORMAT1));
					cd.setCloseTime(
							DateUtil.format(DateUtil.parseDatetime(order.getCloseTime()), DateUtil.FORMAT1));
					
					cd.setCloseType("sltp");
					
					list.add(cd);
				}
				dto.setLots(lots);
				dto.setRealProfit(realProfit);
				dto.setChildren(list);
				
				return dto;
			});
		
		return pager;
	}
	
	public HistoryOrderDTO convertToHistoryDTO(HistoryOrder t) {
		HistoryOrderDTO dto = new HistoryOrderDTO();
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
		if (OrderTypeEnum.SELL.getValue().equals(type)) {
			dto.setPoints(DecimalUtil.removePecimalPoint(
					new BigDecimal(openPrice).subtract(new BigDecimal(closePrice))));
		}
		if (OrderTypeEnum.BUY.getValue().equals(type)) {
			dto.setPoints(DecimalUtil.removePecimalPoint(
					new BigDecimal(closePrice).subtract(new BigDecimal(openPrice))));
		}
		dto.setOpenTime(DateUtil.format(DateUtil.parseDatetime(t.getOpenTime()), DateUtil.FORMAT1));
		dto.setCloseTime(DateUtil.format(DateUtil.parseDatetime(t.getCloseTime()), DateUtil.FORMAT1));
		dto.setCloseType(CloseTypeEnum.parse(t.getComment()));
		dto.setDuration(DateUtil.getSecondDuration(t.getOpenTime(), t.getCloseTime()));
		
		return dto;
	}
	
	public Pager<HistoryOrderDTO> getHistoryOrderList(OrderParam query) {
		Pager<HistoryOrderDTO> pager = PageUtil.selectPage(historyOrderExtMapper, query,
			t -> {
				return convertToHistoryDTO(t);
			});
		
		return pager;
	}
	
	/**
	 * 
	 * @param account
	 * @return
	 */
	public ReportDTO queryOrderTotal(Integer account) {
		OrderParam param = new OrderParam();
		param.setAccountId(account);
		OrderSumResult orderSum = historyOrderExtMapper.selectSum(param);
		
		ReportDTO report = new ReportDTO();
		if (orderSum != null) {
			report.setOrderNum(orderSum.getOrderNum());
			report.setLots(DecimalUtil.get(orderSum.getLots()));
			report.setRealProfit(DecimalUtil.get(orderSum.getRealProfit()));
		
			Map<String, String> map = historyOrderExtMapper.selectFirstAndLast(account);
			if (map != null) {
				report.setStartDate(DateUtil.getDate(map.get("firstDate")));
				report.setEndDate(DateUtil.getDate(map.get("lastDate")));
			}
		}
		return report;
	}
	
	public String queryFirstOrderDate(Integer account) {
		Map<String, String> map = historyOrderExtMapper.selectFirstAndLast(account);
		if (map != null) {
			return DateUtil.getDate(map.get("firstDate"));
		}
		return null;
	}
	
	/**
	 * 
	 * @param order
	 */
	public void addHistoryOrder(Integer accountId, BigDecimal balance, List<OrderMQL> orders) {
		int orderCount = 0;
		int fundCount = 0;
		List<OrderMQL> notifyList = Lists.newArrayList();
		for (OrderMQL order : orders) {
			
			String type = order.getType();
			// 订单类型为余额
			if (OrderTypeEnum.BALANCE.getValue().equals(type)) {
				String comment = order.getComment();
				String balanceType = FundType.parse(comment);
				
				if (balanceType == null) {
					continue;
				}
				
				Fund bal = new Fund();
				bal.setAccountId(accountId);
				bal.setComment(comment);
				bal.setOpenTime(DateUtil.formatDatetime(order.getOpenTime()));
				bal.setProfit(order.getProfit());
				bal.setTicket(order.getTicket());
				bal.setType(balanceType);
				
				fundExtMapper.replaceBatch(Lists.newArrayList(bal));
				fundCount++;
			} else if (OrderTypeEnum.BUY.getValue().equals(type) ||
					OrderTypeEnum.SELL.getValue().equals(type)) {
				HistoryOrder to = new HistoryOrder();
				to.setAccountId(accountId);
				to.setTicket(order.getTicket());
				to.setOpenTime(DateUtil.formatDatetime(order.getOpenTime()));
				to.setCloseTime(DateUtil.formatDatetime(order.getCloseTime()));
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
			    
			    notifyList.add(order);
				
				historyOrderExtMapper.replaceBatch(Lists.newArrayList(to));
				
				orderCount++;
			} else if (OrderTypeEnum.SELL_LIMIT.getValue().equals(type) ||
					OrderTypeEnum.SELL_STOP.getValue().equals(type) ||
					OrderTypeEnum.BUY_LIMIT.getValue().equals(type) ||
					OrderTypeEnum.BUY_STOP.getValue().equals(type)) {
				PendingOrder to = new PendingOrder();
				to.setAccountId(accountId);
				to.setTicket(order.getTicket());
				to.setOpenTime(DateUtil.formatDatetime(order.getOpenTime()));
				to.setCloseTime(DateUtil.formatDatetime(order.getCloseTime()));
				to.setSymbol(order.getSymbol());
				to.setLots(order.getLots());
				
				to.setType(order.getType());
			    to.setOpenPrice(order.getOpenPrice());
			    to.setClosePrice(order.getClosePrice());
			    to.setStopLoss(order.getStopLoss());
			    to.setTakeProfit(order.getTakeProfit());
				to.setExpiration(DateUtil.formatDatetime(order.getExpiration()));
			    to.setStatus(PendingOrderStatusEnum.CANCELLED.getValue());
				 
			    pendingOrderExtMapper.replaceBatch(Lists.newArrayList(to));
				orderCount++;
			}
		}
		if (fundCount + orderCount > 0) {
			log.info("新增订单" + orderCount + "条,资金" + fundCount + "条");
		}
		
		if (notifyList.size() > 0) {
			OrderMQL order = notifyList.get(0);
			
			// 通知
		    String comment = order.getComment();
		    String mailType = null;
		    if (comment.contains("tp")) {
		    	mailType = MailTypeEnum.TP.getValue();
		    } else if (comment.contains("sl")) {
		    	mailType = MailTypeEnum.SL.getValue();
		    } else if (comment.contains("so")) {
		    	mailType = MailTypeEnum.SO.getValue();
		    }
		    if (mailType != null) {
		    	mailService.sendTradeInfo(mailType, order);
		    }
		}
		
		reportService.updateCurrentHistoryReport(accountId, balance);
	}
	
//	public void updatePendingClosed(HistoryOrder order) {
//		pendingOrderExtMapper.updateUnique(order.getAccountId(), order.getTicket(), PendingOrderStatusEnum.CLOSED.getValue());
//	}
	
	public void updatePendingOrder(Integer accountId, List<OrderMQL> orders) {
		for (OrderMQL order : orders) {

			PendingOrder to = new PendingOrder();
			to.setAccountId(accountId);
			to.setTicket(order.getTicket());
			to.setOpenTime(DateUtil.formatDatetime(order.getOpenTime()));
			Date serverTime = AccountCache.getServerTime(accountId);
			if (serverTime == null) {
				to.setCloseTime(DateUtil.formatDatetime(new Date()));
			} else {
				to.setCloseTime(DateUtil.formatDatetime(serverTime));
			}
			
			to.setSymbol(order.getSymbol());
			to.setLots(order.getLots());
			
			to.setType(order.getType());
		    to.setOpenPrice(order.getOpenPrice());
		    to.setClosePrice(order.getClosePrice());
		    to.setStopLoss(order.getStopLoss());
		    to.setTakeProfit(order.getTakeProfit());
			to.setExpiration(DateUtil.formatDatetime(order.getExpiration()));
		    to.setStatus(PendingOrderStatusEnum.TRADE.getValue());
		    
		    pendingOrderExtMapper.replaceBatch(Lists.newArrayList(to));
		    
		}
		if (orders.size() > 0) {
			mailService.sendTradeInfo(MailTypeEnum.PENDING.getValue(), orders.get(0));
		}
	}
	
	/**
	 * 
	 * @param data
	 */
	public void updateHistoryOrders(Integer accountId, BigDecimal balance, List<OrderMQL> orders) {
		
		List<HistoryOrder> historyOrders = Lists.newArrayList();
		List<PendingOrder> pendingOrders = Lists.newArrayList();
		List<Fund> funds = Lists.newArrayList();
		List<Report> summarys = Lists.newArrayList();
		
		// 转换为do
		for (OrderMQL order : orders) {
			String type = order.getType();
			// 订单类型为余额
			if (OrderTypeEnum.BALANCE.getValue().equals(type)) {
				String comment = order.getComment();
				String balanceType = FundType.parse(comment);
				
				if (balanceType == null) {
					continue;
				} else if (FundType.SUMMARY.equals(balanceType)) {
					Report summary = new Report();
					summary.setAccountId(accountId);
					summary.setType(ReportTypeEnum.MONTH.getValue());
					String startDate = 
							DateUtil.getStartDateStrOfMonth(order.getOpenTime());
					String endDate = 
							DateUtil.getEndDateStrOfMonth(order.getOpenTime());
					summary.setStartDate(startDate);
					summary.setEndDate(endDate);
					summary.setRealProfit(order.getProfit());
					Date date = new Date();
					summary.setCreateTime(DateUtil.formatDatetime(date));
					summary.setUpdateTime(DateUtil.formatDatetime(date));
					summarys.add(summary);
				} else {
					Fund bal = new Fund();
					bal.setAccountId(accountId);
					bal.setComment(comment);
					bal.setOpenTime(DateUtil.formatDatetime(order.getOpenTime()));
					bal.setProfit(order.getProfit());
					bal.setTicket(order.getTicket());
					bal.setType(balanceType);
					funds.add(bal);
				}
			} else if (OrderTypeEnum.BUY.getValue().equals(type) ||
					OrderTypeEnum.SELL.getValue().equals(type)) {
				HistoryOrder to = new HistoryOrder();
				to.setAccountId(accountId);
				to.setTicket(order.getTicket());
				to.setOpenTime(DateUtil.formatDatetime(order.getOpenTime()));
				to.setCloseTime(DateUtil.formatDatetime(order.getCloseTime()));
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
				 
				historyOrders.add(to);
			} else if (OrderTypeEnum.SELL_LIMIT.getValue().equals(type) ||
					OrderTypeEnum.SELL_STOP.getValue().equals(type) ||
					OrderTypeEnum.BUY_LIMIT.getValue().equals(type) ||
					OrderTypeEnum.BUY_STOP.getValue().equals(type)) {
				PendingOrder to = new PendingOrder();
				to.setAccountId(accountId);
				to.setTicket(order.getTicket());
				to.setOpenTime(DateUtil.formatDatetime(order.getOpenTime()));
				to.setCloseTime(DateUtil.formatDatetime(order.getCloseTime()));
				to.setSymbol(order.getSymbol());
				to.setLots(order.getLots());
				
				to.setType(order.getType());
			    to.setOpenPrice(order.getOpenPrice());
			    to.setClosePrice(order.getClosePrice());
			    to.setStopLoss(order.getStopLoss());
			    to.setTakeProfit(order.getTakeProfit());
				to.setExpiration(DateUtil.formatDatetime(order.getExpiration()));
			    to.setStatus(PendingOrderStatusEnum.CANCELLED.getValue());
				 
				pendingOrders.add(to);
			}
		}
		
		BatchUtil.replaceBatch(historyOrderExtMapper, historyOrders);
		log.info("更新历史订单" + historyOrders.size() + "条");
		BatchUtil.replaceBatch(pendingOrderExtMapper, pendingOrders);
		log.info("更新历史挂单" + pendingOrders.size() + "条");
		BatchUtil.replaceBatch(fundExtMapper, funds);
		log.info("更新历史资金" + funds.size() + "条");
		
		reportService.updateHistoryReport(accountId, balance);
		// 需要在更新历史报表之后操作
		reportService.insertSummary(summarys);
		
	}
	
	private void updateCompexOrders(Integer accountId, List<HistoryOrder> historyOrders) {

		List<HistoryOrder> list = historyOrders.stream().filter(t -> {
			return t.getComment().contains("from #");
		}).collect(Collectors.toList());
		
		List<List<ComplexOrder>> complexList = Lists.newArrayList();
		list.stream().forEach(t -> {
			List<ComplexOrder> childList = Lists.newArrayList();
			childList.add(convertToComplex(t));
			
			String symbol = t.getSymbol();
			String openPrice = t.getOpenPrice();
			String openTime = t.getOpenTime();
			String flag = "to #";
			OrderParam query = new OrderParam();
			query.setAccountId(accountId);
			query.setSymbol(symbol);
			query.setOpenPrice(openPrice);
			query.setOpenTime(openTime);
			query.setCommentText(flag);
			List<HistoryOrder> hlList = historyOrderExtMapper.selectPage(query);
			for(HistoryOrder hl : hlList) {
				childList.add(convertToComplex(hl));
			}
			
			complexList.add(childList);
		});
		
		List<ComplexOrder> result = Lists.newArrayList();
		for (List<ComplexOrder> children : complexList) {
			ComplexOrder first = children.get(0);
			String parentTicket = "_" + first.getTicket();
			
			BigDecimal lots = BigDecimal.ZERO;
			BigDecimal commission = BigDecimal.ZERO;
			BigDecimal swap = BigDecimal.ZERO;
			BigDecimal profit = BigDecimal.ZERO;
			BigDecimal realProfit = BigDecimal.ZERO;
			for(ComplexOrder child : children) {
				child.setParentTicket(parentTicket);
				
				lots = DecimalUtil.add(lots, child.getLots());
				commission = DecimalUtil.add(commission, child.getCommission());
				swap = DecimalUtil.add(swap, child.getSwap());
				profit = DecimalUtil.add(profit, child.getProfit());
				realProfit = DecimalUtil.add(realProfit, child.getRealProfit());
				result.add(child);
			}
			
			ComplexOrder parent = new ComplexOrder();
			parent.setAccountId(accountId);
			parent.setTicket(parentTicket);
			parent.setSymbol(first.getSymbol());
			parent.setType(first.getType());
			parent.setOpenTime(first.getOpenTime());
			parent.setCloseTime(first.getCloseTime());
			parent.setOpenPrice(first.getOpenPrice());
			parent.setClosePrice(first.getClosePrice());
			parent.setLots(lots);
			parent.setCommission(commission);
			parent.setSwap(swap);
			parent.setProfit(profit);
			parent.setRealProfit(realProfit);
			parent.setStatus(ComplexOrderStatusEnum.CLOSED.getValue());
			parent.setParentTicket("0");
			parent.setComment("");
			parent.setStopLoss("0");
			parent.setTakeProfit("0");
			result.add(parent);
		}
		//BatchUtil.replaceBatch(complexOrderExtMapper, result);

		log.info("更新组合订单" + result.size() + "条");
	}
	
	public ComplexOrder convertToComplex(HistoryOrder order) {
		ComplexOrder complex = new ComplexOrder();
		BeanUtils.copyProperties(order, complex);
		complex.setStatus(ComplexOrderStatusEnum.CLOSED.getValue());
		
		return complex;
	}
	
	private String parseTicketFromComment(String comment) {
		
		Pattern pattern = Pattern.compile("#(\\d+)");
		
		Matcher matcher = pattern.matcher(comment);
		
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new IllegalArgumentException();
		}
		
	}
	
	/**
	 * 
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<OrderSumDTO> querySumGroupSymbol(Integer account, String startDate, String endDate) {
		OrderParam param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setOrderBy("real_profit desc");
		
		List<OrderSumResult> results = historyOrderExtMapper.selectSumBySymbol(param);
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
		OrderParam param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		
		List<OrderSumResult> results = historyOrderExtMapper.selectSumByDate(param);
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
		OrderParam param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		
		OrderSumResult result = historyOrderExtMapper.selectSum(param);
		OrderSumDTO dto = new OrderSumDTO();
		BeanUtils.copyProperties(result, dto);

		param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setSl(true);
		int slCount = historyOrderExtMapper.selectTotal(param);
		dto.setSlCount(slCount);
		
		param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setTp(true);
		int tpCount = historyOrderExtMapper.selectTotal(param);
		dto.setTpCount(tpCount);

		param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setSo(true);
		int soCount = historyOrderExtMapper.selectTotal(param);
		dto.setSoCount(soCount);
		
		param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setClose(true);
		int closeCount = historyOrderExtMapper.selectTotal(param);
		dto.setCloseCount(closeCount);
		
		param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setCommentText("from");
		int complexCount = historyOrderExtMapper.selectTotal(param);
		dto.setComplexCount(complexCount);
		
		param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setStatus(PendingOrderStatusEnum.TRADE.getValue());
		int pendingCount = pendingOrderExtMapper.selectTotal(param);
		dto.setPendingCount(pendingCount);
		
		param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setPassDay(true);
		int passDayCount = historyOrderExtMapper.selectTotal(param);
		dto.setPassDayCount(passDayCount);
		
		param = new OrderParam();
		param.setAccountId(account);
		param.setCloseStartDate(startDate);
		param.setCloseEndDate(endDate);
		param.setPassWeekend(true);
		int passWeekendCount = historyOrderExtMapper.selectTotal(param);
		dto.setPassWeekendCount(passWeekendCount);
		
		return dto;
	}
}