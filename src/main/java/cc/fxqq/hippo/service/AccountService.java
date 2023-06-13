package cc.fxqq.hippo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cc.fxqq.hippo.cache.AccountCache;
import cc.fxqq.hippo.dao.AccountMapper;
import cc.fxqq.hippo.dao.ext.AccountExtMapper;
import cc.fxqq.hippo.dao.ext.TradeOrderExtMapper;
import cc.fxqq.hippo.dto.json.ConnectMQL;
import cc.fxqq.hippo.dto.json.MarketMQL;
import cc.fxqq.hippo.dto.template.AccountDTO;
import cc.fxqq.hippo.dto.template.ReportDTO;
import cc.fxqq.hippo.entity.Account;
import cc.fxqq.hippo.entity.param.TradeOrderParam;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;

@Service
public class AccountService {
	
	@Autowired
	private AccountExtMapper accountExtMapper;

	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private TradeOrderExtMapper tradeOrderExtMapper;
	
	@Autowired
	private ReportService reportService;
	
	public AccountDTO queryAccountInfo(Integer accountId) {
		Account acc = getAccountById(accountId);
		
		AccountDTO info = new AccountDTO();
		info.setBalance(acc.getBalance());
		info.setCompany(acc.getCompany());
		info.setLeverage(acc.getLeverage());
		info.setName(acc.getName());
		info.setNumber(acc.getNumber());
		info.setServer(acc.getServer());
		info.setCurrency(acc.getCurrency());
		info.setClientName(acc.getClientName());
		info.setTimeZone(acc.getTimeZone());
		info.setStopOutLevel(acc.getStopOutLevel());
		
		Account accCache = AccountCache.getByAccountName(acc.getName());
		if (accCache == null) {
			info.setStatus(0);
		} else {
			info.setStatus(1);
		}
		info.setConnectTime(acc.getConnectTime());
		
		String tradeTime = tradeOrderExtMapper.selectMinOpenTime(accountId);
		if (tradeTime != null) {
			// 交易日期
			String tradeDate = DateUtil.formatDate(DateUtil.parseDatetime(tradeTime));
			info.setTradeDate(tradeDate);
		}
		
		BigDecimal balance = acc.getBalance();
		ReportDTO report = reportService.querySummary(accountId);
		TradeOrderParam param = new TradeOrderParam();
		param.setAccountId(accountId);
		BigDecimal income = balance.subtract(
				DecimalUtil.add(report.getDeposit(), report.getWithdraw()));
		info.setIncome(income);
		
		return info;
	}
	
	public Integer getAccountIdByName(String name) {
		Account acc = accountExtMapper.selectUnique(name);
		
		return acc.getId();
	}

	public Account getAccountById(Integer accountId) {
		Account acc = accountMapper.selectByPrimaryKey(accountId);
		
		return acc;
	}
	/**
	 * 
	 * @return
	 */
	public List<AccountDTO> getAccounts() {
		List<Account> list = accountExtMapper.selectAccounts();
		
		List<AccountDTO> result = list.stream().map(t -> {
			AccountDTO acc = new AccountDTO();
			BeanUtils.copyProperties(t, acc);
			return acc;
		}).collect(Collectors.toList());
		
		return result;
	}
	
	public void setConnectTime(Integer id) {
		Account acc = accountMapper.selectByPrimaryKey(id);
		if (acc != null) {
			String date = DateUtil.formatDatetime(new Date());
			acc.setConnectTime(date);
			accountMapper.updateByPrimaryKeySelective(acc);
		}
	} 
	
	public Account queryAccount(String symbol) {
		return accountExtMapper.selectUnique(symbol);
	}
	
	public Account addAccount(String name, ConnectMQL accountInfo) {
		Account acc = new Account();
		acc.setName(name);
		acc.setNumber(accountInfo.getNumber());
		acc.setCurrency(accountInfo.getCurrency());
		acc.setLeverage(accountInfo.getLeverage());
		acc.setBalance(accountInfo.getBalance());
		acc.setCompany(accountInfo.getCompany());
		acc.setServer(accountInfo.getServer());
		acc.setTimeZone(accountInfo.getTimeZone());
		acc.setClientName(accountInfo.getClientName());
		acc.setStopOutLevel(accountInfo.getStopOutLevel());
		
		String date = DateUtil.formatDatetime(new Date());
		acc.setCreateTime(date);
		acc.setConnectTime(date);
		acc.setUpdateTime(date);
		accountMapper.insertSelective(acc);
		
		return acc;
	}
	
	public Account updateAccount(Account acc, ConnectMQL accountInfo) {
		acc.setNumber(accountInfo.getNumber());
		acc.setCurrency(accountInfo.getCurrency());
		acc.setLeverage(accountInfo.getLeverage());
		acc.setBalance(accountInfo.getBalance());
		acc.setCompany(accountInfo.getCompany());
		acc.setServer(accountInfo.getServer());
		acc.setTimeZone(accountInfo.getTimeZone());
		acc.setClientName(accountInfo.getClientName());
		acc.setStopOutLevel(accountInfo.getStopOutLevel());
		
		String date = DateUtil.formatDatetime(new Date());
		acc.setConnectTime(date);
		acc.setUpdateTime(date);
		accountMapper.updateByPrimaryKeySelective(acc);
		
		return acc;
	}
	
	public Account setTimeZone(Account acc, Integer timeZone) {
		acc.setTimeZone(timeZone);
		String date = DateUtil.formatDatetime(new Date());
		acc.setUpdateTime(date);
		accountMapper.updateByPrimaryKeySelective(acc);
		
		return acc;
	}
	
	public void setAccountBalance(Account account) {
		Account acc = accountMapper.selectByPrimaryKey(account.getId());
		if (acc != null) {
			acc.setBalance(account.getBalance());
			String date = DateUtil.formatDatetime(new Date());
			acc.setUpdateTime(date);
			accountMapper.updateByPrimaryKeySelective(acc);
		}
	}
}