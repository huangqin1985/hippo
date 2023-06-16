package cc.fxqq.hippo.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

import cc.fxqq.hippo.cache.AccountCache;
import cc.fxqq.hippo.cache.StringCache;
import cc.fxqq.hippo.dto.json.PositionMQL;
import cc.fxqq.hippo.dto.json.MarketMQL;
import cc.fxqq.hippo.dto.template.AccountDTO;
import cc.fxqq.hippo.dto.template.MarketDTO;
import cc.fxqq.hippo.dto.template.OrderSumDTO;
import cc.fxqq.hippo.dto.template.Pager;
import cc.fxqq.hippo.dto.template.PositionDTO;
import cc.fxqq.hippo.dto.template.ReportDTO;
import cc.fxqq.hippo.dto.template.TradeFundDTO;
import cc.fxqq.hippo.dto.template.TradeOrderDTO;
import cc.fxqq.hippo.entity.Account;
import cc.fxqq.hippo.entity.param.ReportParam;
import cc.fxqq.hippo.entity.param.TradeFundParam;
import cc.fxqq.hippo.entity.param.TradeOrderParam;
import cc.fxqq.hippo.service.AccountService;
import cc.fxqq.hippo.service.ReportService;
import cc.fxqq.hippo.service.TradeFundService;
import cc.fxqq.hippo.service.TradeOrderService;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;

@Controller
@RequestMapping("/")
public class TradeOrderController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(TradeOrderController.class);

	@Autowired
	private TradeOrderService tradeOrderService;

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ReportService reportService;

	@Autowired
	private TradeFundService tradeFundService;
	
	@GetMapping("/order")
	public String order(Model model,
			@RequestParam(name="account", required = false) Integer accountId,
			@RequestParam(name="type", required = false) String type,
			@RequestParam(name="startDate", required = false) Date startDate,
			@RequestParam(name="endDate", required = false) Date endDate) {
		
		// 账户列表
		List<AccountDTO> accounts = accountService.getAccounts();
		
		if (accountId == null) {
			if (accounts.size() > 0) {
				accountId = accounts.get(0).getId();
			} else {
				return "error";
			}
		}
		Account acc = accountService.getAccountById(accountId);
		
		String startDateStr = null;
		String endDateStr = null;
		if (StringUtils.isNotEmpty(type)) {
			Date serverTime = AccountCache.getServerTime(acc.getName());
			if (serverTime == null) {
				serverTime = new Date();
			}
			
			if ("today".equals(type)) {
				String date = DateUtil.formatDate(serverTime);

				startDateStr = date;
				endDateStr = date;
			} else if ("thisWeek".equals(type)) {
				startDateStr = DateUtil.getStartDateStrOfWeek(serverTime);
				endDateStr = DateUtil.getEndDateStrOfWeek(serverTime);
			} else if ("thisMonth".equals(type)) {
				startDateStr = DateUtil.getStartDateStrOfMonth(serverTime);
				endDateStr = DateUtil.getEndDateStrOfMonth(serverTime);
			}  else if ("all".equals(type)) {
				startDateStr = tradeOrderService.queryFirstOrderDate(accountId);
				String date = DateUtil.formatDate(new Date());
				endDateStr = date;
			}
		}
		if (startDateStr == null || endDateStr == null) {
			if (startDate == null || endDate == null) {
				startDateStr = DateUtil.getStartDateStrOfWeek();
				endDateStr = DateUtil.getEndDateStrOfWeek();
			} else {
				startDateStr = DateUtil.formatDate(startDate);
				endDateStr = DateUtil.formatDate(endDate);
			}
		}
		
		model.addAttribute("accounts", accounts);
		
		List<OrderSumDTO> dtos = tradeOrderService.querySumGroupSymbol(accountId, 
				startDateStr, endDateStr);
		model.addAttribute("sum", dtos);
		
		OrderSumDTO totalSum = tradeOrderService.querySum(accountId, 
				startDateStr, endDateStr);
		
		model.addAttribute("totalSum", totalSum);
		
		model.addAttribute("account", accountId);
		model.addAttribute("startDate", startDateStr);
		model.addAttribute("endDate", endDateStr);
		model.addAttribute("type", type);
		model.addAttribute("startDateStr", startDateStr);
		model.addAttribute("endDateStr", endDateStr);
		
		return "order";
	}
	
	@GetMapping("/fund")
	public String fund(Model model,
			@RequestParam(name="account", required = false) Integer accountId,
			@RequestParam(name="type", required = false, defaultValue="all") String type,
			@RequestParam(name="startDate", required = false) Date startDate,
			@RequestParam(name="endDate", required = false) Date endDate,
			@RequestParam(name="pageNum", required = false) Integer pageNum) {
		
		// 账户列表
		List<AccountDTO> accounts = accountService.getAccounts();
		
		if (accountId == null) {
			if (accounts.size() > 0) {
				accountId = accounts.get(0).getId();
			} else {
				return "error";
			}
		}
		
		model.addAttribute("accounts", accounts);
		
		ReportDTO item = reportService.querySummary(accountId);
		
		TradeFundParam param = new TradeFundParam();
		param.setAccountId(accountId);
		if ("all".equals(type)) {
			param.setType(null);
		} else {
			param.setType(type);
		}
		if (startDate != null) {
			param.setStartDate(DateUtil.formatDate(startDate));
		}
		if (endDate != null) {
			param.setEndDate(DateUtil.formatDate(endDate));
		}
		
		param.setOrderBy("open_time desc");
		
		if (pageNum == null) {
			pageNum = 1;
		}
		if (pageNum <= 0) {
			param.setPage(1);
		} else {
			param.setPage(pageNum);
		}
		
		Pager<TradeFundDTO> pager = tradeFundService.getFundList(param);
		model.addAttribute("pager", pager);
		model.addAttribute("report", item);
		model.addAttribute("account", accountId);
		model.addAttribute("type", type);
		
		return "fund";
	}
	
	@GetMapping("/report")
	public String report(Model model,
			@RequestParam(name="account", required = false) Integer accountId,
			@RequestParam(name="type", required = false, defaultValue="week") String type,
			@RequestParam(name="sort", required = false, defaultValue="startDate") String sort,
			@RequestParam(name="sortType", required = false, defaultValue="desc") String sortType,
			@RequestParam(name="col", required = false, defaultValue="balance") String col,
			@RequestParam(name="pageNum", required = false) Integer pageNum) {
		
		// 账户列表
		List<AccountDTO> accounts = accountService.getAccounts();
		
		if (accountId == null) {
			if (accounts.size() > 0) {
				accountId = accounts.get(0).getId();
			} else {
				return "error";
			}
		}
		
		model.addAttribute("accounts", accounts);
		
		ReportParam param = new ReportParam();
		param.setAccountId(accountId);
		param.setType(type);

		String sortName = null;
		if ("startDate".equals(sort)) {
			sortName = "start_date";
		} else if ("balance".equals(sort)) {
			sortName = "balance";
		} else if ("lots".equals(sort)) {
			sortName = "lots";
		} else if ("orderNum".equals(sort)) {
			sortName = "order_num";
		} else if ("realProfit".equals(sort)) {
			sortName = "real_profit";
		}
		if (sortName != null) {
			param.setOrderBy(sortName + " " + sortType);
		}
		
		if (pageNum == null) {
			pageNum = 1;
		}
		if (pageNum <= 0) {
			param.setPage(1);
		} else {
			param.setPage(pageNum);
		}
		if ("day".equals(type)) {
			param.setRows(30);
		} else if ("week".equals(type)) {
			param.setRows(24);
		} else if ("month".equals(type)) {
			param.setRows(24);
		}
		Pager<ReportDTO> pager = reportService.queryReportList(param);
		model.addAttribute("pager", pager);
		
		ReportDTO report = tradeOrderService.queryOrderTotal(accountId);
		model.addAttribute("report", report);
		model.addAttribute("account", accountId);
		model.addAttribute("type", type);
		model.addAttribute("sort", sort);
		model.addAttribute("sortType", sortType);
		model.addAttribute("col", col);
		
		return "report";
	}
	
	@GetMapping("/orderList")
	public String orderList(Model model,
			@RequestParam(name="account", required = false) Integer accountId,
			@RequestParam(name="symbol", required = false, defaultValue="") String symbol,
			@RequestParam(name="sort", required = false, defaultValue="closeTime") String sort,
			@RequestParam(name="sortOrder", required = false, defaultValue="desc") String sortOrder,
			@RequestParam(name="startDate", required = false) Date startDate,
			@RequestParam(name="endDate", required = false) Date endDate,
			@RequestParam(name="sl", required = false, defaultValue="false") boolean sl,
			@RequestParam(name="tp", required = false, defaultValue="false") boolean tp,
			@RequestParam(name="noSl", required = false, defaultValue="false") boolean noSl,
			@RequestParam(name="pageNum", required = false) Integer pageNum) {
		
		// 账户列表
		List<AccountDTO> accounts = accountService.getAccounts();
		
		if (accountId == null) {
			if (accounts.size() > 0) {
				accountId = accounts.get(0).getId();
			} else {
				return "error";
			}
		}
		
		if (startDate == null || endDate == null) {
			if (startDate == null && endDate == null) {
				startDate = DateUtil.getStartDateOfWeek();
				endDate = DateUtil.getEndDateOfWeek();
			} else {
				return "error";
			}
		}
		
		model.addAttribute("accounts", accounts);
		
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(accountId);
		if (StringUtils.isNotEmpty(symbol)) {
			param.setSymbol(symbol);
		}
		
		if (startDate != null && endDate != null) {
			param.setCloseStartDate(DateUtil.formatDate(startDate));
			param.setCloseEndDate(DateUtil.formatDate(endDate));
		}
		
		String sortName = "";
		if ("closeTime".equals(sort) || "openTime".equals(sort) ||
				"lots".equals(sort)
				|| "realProfit".equals(sort) || "holdTime".equals(sort)) {
			if ("closeTime".equals(sort)) {
				sortName = "close_time";
			} else if ("openTime".equals(sort)) {
				sortName = "open_time";
			} else if ("realProfit".equals(sort)) {
				sortName = "real_profit";
			} else if ("holdTime".equals(sort)) {
				sortName = "strftime('%s', close_time) - strftime('%s', open_time)";
			} else {
				sortName = sort;
			}
			if ("asc".equals(sortOrder) || "desc".equals(sortOrder)) {
				param.setOrderBy(sortName + " " + sortOrder);
			}
		}
		
		if (pageNum == null) {
			pageNum = 1;
		}
		if (pageNum <= 0) {
			param.setPage(1);
		} else {
			param.setPage(pageNum);
		}
		param.setTp(tp);
		param.setSl(sl);
		param.setNoSl(noSl);
		
		Pager<TradeOrderDTO> orderList = tradeOrderService.getTradeOrderList(param);
		model.addAttribute("pager", orderList);
		
		model.addAttribute("account", accountId);
		model.addAttribute("startDate", DateUtil.formatDate(startDate));
		model.addAttribute("endDate", DateUtil.formatDate(endDate));
		model.addAttribute("symbol", symbol);
		model.addAttribute("sort", sort);
		model.addAttribute("sortType", sortOrder);
		model.addAttribute("startDateStr", DateUtil.formatDate(startDate));
		model.addAttribute("endDateStr", DateUtil.formatDate(endDate));
		model.addAttribute("tp", tp);
		model.addAttribute("sl", sl);
		
		return "orderList";
	}

	@GetMapping("/position")
	public String position(Model model,
			@RequestParam(name="account", required = false) Integer accountId) {
		
		// 账户列表
		List<AccountDTO> accounts = accountService.getAccounts();
		
		if (accountId == null) {
			if (accounts.size() > 0) {
				accountId = accounts.get(0).getId();
			} else {
				return "error";
			}
		}
		Account acc = accountService.getAccountById(accountId);
		Account accCache = AccountCache.getByAccountName(acc.getName());
		if (accCache == null) {
			model.addAttribute("isConnect", false);
		} else {
			model.addAttribute("isConnect", true);
		}
		
		if (acc != null) {
			PositionMQL position = AccountCache.getPosition(acc.getName());
			PositionDTO dto = new PositionDTO(position);
			model.addAttribute("position", dto);
		}
		
		model.addAttribute("accounts", accounts);
		model.addAttribute("account", accountId);
		
		return "position";
	}
	
	@GetMapping("/accountInfo")
	public String accountInfo(Model model,
			@RequestParam(name="account", required = false) Integer accountId) {
		
		// 账户列表
		List<AccountDTO> accounts = accountService.getAccounts();
		
		if (accountId == null) {
			if (accounts.size() > 0) {
				accountId = accounts.get(0).getId();
			} else {
				return "error";
			}
		}
		
		model.addAttribute("accounts", accounts);
		model.addAttribute("account", accountId);
		
		AccountDTO acc = accountService.queryAccountInfo(accountId);
		model.addAttribute("acc", acc);
		
		return "accountInfo";
	}
	
	@GetMapping("/market")
	public String market(Model model,
			@RequestParam(name="account", required = false) Integer accountId,
			@RequestParam(name="path", required = false) String path,
			@RequestParam(name="subfix", required = false, defaultValue="") String subfix) {
		
		// 账户列表
		List<AccountDTO> accounts = accountService.getAccounts();
		
		if (accountId == null) {
			if (accounts.size() > 0) {
				accountId = accounts.get(0).getId();
			} else {
				return "error";
			}
		}
		
		model.addAttribute("accounts", accounts);
		
		Account acc = accountService.getAccountById(accountId);

		Account accCache = AccountCache.getByAccountName(acc.getName());
		if (accCache == null) {
			model.addAttribute("isConnect", false);
		} else {
			model.addAttribute("isConnect", true);
		}
		
		if (acc != null) {
			List<MarketMQL> markets = AccountCache.getMarket(acc.getName());
			
			if (markets != null) {
				List<String> subfixList = markets.stream().map(
						t -> t.getSymbol().substring(0, 1)).distinct().sorted().collect(Collectors.toList());
				
				List<String> pathList = markets.stream().map(
						t -> t.getPath()).distinct().sorted().collect(Collectors.toList());

				model.addAttribute("subfixList", subfixList);
				model.addAttribute("pathList", pathList);
				
				if (markets.size() > 0) {
					if (StringUtils.isNotEmpty(subfix)) {
						markets = markets.stream().filter(t -> t.getSymbol().startsWith(subfix)).collect(Collectors.toList());
					} 
					else if (StringUtils.isNotEmpty(path)) {
						markets = markets.stream().filter(t -> StringUtils.equals(path, t.getPath())).collect(Collectors.toList());
					} else {
						markets = markets.subList(0, Math.min(markets.size(), 20));
					}
				}
				
				List<MarketDTO> dtos = Lists.newArrayList();
				for(MarketMQL t : markets) {
					MarketDTO dto = new MarketDTO();
					dto.setSymbol(t.getSymbol());
					dto.setAllowTrade(t.getAllowTrade());
					
					BigDecimal requiredMargin = DecimalUtil.get(t.getRequiredMargin());
					dto.setRequiredMarginStr(DecimalUtil.format(requiredMargin));
					
					BigDecimal pointProfit = DecimalUtil.get(t.getPointProfit(), 3);
					dto.setPointProfitStr(DecimalUtil.format3Digit(pointProfit));
					
					BigDecimal buySwapProfit = DecimalUtil.get(t.getBuySwapProfit(), 3);
					dto.setBuySwapProfit(buySwapProfit);
					dto.setBuySwapProfitStr(DecimalUtil.format3Digit(buySwapProfit));

					BigDecimal sellSwapProfit = DecimalUtil.get(t.getSellSwapProfit(), 3);
					dto.setSellSwapProfit(sellSwapProfit);
					dto.setSellSwapProfitStr(DecimalUtil.format3Digit(sellSwapProfit));
					
					dto.setBuyPrice(DecimalUtil.get(t.getBuyPrice(), t.getDigits()));
					dto.setSellPrice(DecimalUtil.get(t.getSellPrice(), t.getDigits()));
					
					BigDecimal spreadProfit = DecimalUtil.get(
							t.getPointProfit().multiply(new BigDecimal(t.getSpread())), 3);
					dto.setSpreadProfitStr(DecimalUtil.format3Digit(spreadProfit));
					dto.setSpread(t.getSpread());
					
					dtos.add(dto);
				}
				Date serverTime = AccountCache.getServerTime(acc.getName());
				
				model.addAttribute("serverTime", DateUtil.formatDatetime(serverTime));
				model.addAttribute("markets", dtos);
				model.addAttribute("path", path);
				model.addAttribute("subfix", subfix);
			}
		}
		
		model.addAttribute("account", accountId);
		model.addAttribute("subfix", subfix);
		
		return "market";
	}
}
