package cc.fxqq.hippo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.fxqq.hippo.cache.AccountCache;
import cc.fxqq.hippo.cache.StringCache;
import cc.fxqq.hippo.dao.AccountMapper;
import cc.fxqq.hippo.dao.ext.AccountExtMapper;
import cc.fxqq.hippo.dao.ext.HistoryOrderExtMapper;
import cc.fxqq.hippo.dao.ext.ReportExtMapper;
import cc.fxqq.hippo.dto.json.ConnectMQL;
import cc.fxqq.hippo.dto.template.AccountDTO;
import cc.fxqq.hippo.entity.Account;
import cc.fxqq.hippo.entity.param.OrderParam;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;

@Service
public class AccountService {
	
	@Autowired
	private AccountExtMapper accountExtMapper;

	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private HistoryOrderExtMapper tradeOrderExtMapper;
	
	@Autowired
	private ReportExtMapper reportExtMapper;
	
	public AccountDTO queryAccountInfo(Integer accountId) {
		Account acc = getAccountById(accountId);
		
		AccountDTO info = new AccountDTO();
		info.setCompany(acc.getCompany());
		info.setLeverage(acc.getLeverage());
		info.setBalance(acc.getBalance());
		info.setName(acc.getName());
		info.setNumber(acc.getNumber());
		info.setServer(acc.getServer());
		info.setCurrency(acc.getCurrency());
		info.setClientName(acc.getClientName());
		info.setStopOutLevel(acc.getStopOutLevel());
		
		Account accCache = AccountCache.getByAccountId(acc.getId());
		if (accCache == null) {
			info.setStatus(0);
		} else {
			info.setStatus(1);
		}
		
		String tradeTime = tradeOrderExtMapper.selectMinOpenTime(accountId);
		if (tradeTime != null) {
			// 交易日期
			String tradeDate = DateUtil.formatDate(DateUtil.parseDatetime(tradeTime));
			info.setTradeDate(tradeDate);
		}
		
		BigDecimal income = reportExtMapper.selectIncome(accountId);
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
	
	public Account queryAccount(String symbol) {
		return accountExtMapper.selectUnique(symbol);
	}
	
	public Account addAccount(String name, ConnectMQL accountInfo) {
		Account acc = new Account();
		acc.setName(name);
		acc.setNumber(accountInfo.getNumber());
		acc.setBalance(accountInfo.getBalance());
		acc.setCurrency(accountInfo.getCurrency());
		acc.setLeverage(accountInfo.getLeverage());
		acc.setCompany(accountInfo.getCompany());
		acc.setServer(accountInfo.getServer());
		acc.setClientName(accountInfo.getClientName());
		acc.setStopOutLevel(accountInfo.getStopOutLevel());
		
		String date = DateUtil.formatDatetime(new Date());
		acc.setCreateTime(date);
		acc.setUpdateTime(date);
		accountMapper.insertSelective(acc);
		
		return acc;
	}
	
	public Account updateAccount(Account acc, ConnectMQL accountInfo) {
		acc.setNumber(accountInfo.getNumber());
		acc.setBalance(accountInfo.getBalance());
		acc.setCurrency(accountInfo.getCurrency());
		acc.setLeverage(accountInfo.getLeverage());
		acc.setCompany(accountInfo.getCompany());
		acc.setServer(accountInfo.getServer());
		acc.setClientName(accountInfo.getClientName());
		acc.setStopOutLevel(accountInfo.getStopOutLevel());
		
		String date = DateUtil.formatDatetime(new Date());
		acc.setUpdateTime(date);
		accountMapper.updateByPrimaryKeySelective(acc);
		
		return acc;
	}
	
	public void setBalance(Integer accountId, BigDecimal balance) {
		Account acc = accountMapper.selectByPrimaryKey(accountId);
		
		acc.setBalance(acc.getBalance());
		
		String date = DateUtil.formatDatetime(new Date());
		acc.setUpdateTime(date);
		accountMapper.updateByPrimaryKeySelective(acc);
	}
	
	public BigDecimal getBalance(Integer accountId) {
		Account acc = accountMapper.selectByPrimaryKey(accountId);
		
		if (acc != null) {
			return acc.getBalance();
		} else {
			return null;
		}
	}


}