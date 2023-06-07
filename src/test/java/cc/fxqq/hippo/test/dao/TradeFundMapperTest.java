package cc.fxqq.hippo.test.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.dao.TradeFundMapper;
import cc.fxqq.hippo.dao.ext.TradeFundExtMapper;
import cc.fxqq.hippo.entity.TradeFund;
import cc.fxqq.hippo.entity.result.FundSumResult;

@SpringBootTest
/* @TestPropertySource("classpath:application.properties") */
public class TradeFundMapperTest {
	
	public static final int account = 1;

	private TradeFund create(String ticket) {
		TradeFund fund = new TradeFund();
		fund.setAccountId(account);
		fund.setOpenTime("2022-09-08 22:00:08");
		fund.setProfit(new BigDecimal("1000.25"));
		fund.setTicket(ticket);
		fund.setType("deposit");
		fund.setComment("-----");
		return fund;
	}
	
	@Autowired
	protected TradeFundExtMapper tradeFundExtMapper;

	@Autowired
	protected TradeFundMapper tradeFundMapper;

	@Test
	@Transactional
	public void testReplaceBatch() {
		List<TradeFund> list = Lists.newArrayList();
		list.add(create("100"));
		list.add(create("101"));
		list.add(create("100"));
		
		tradeFundExtMapper.replaceBatch(list);
	}
	
	@Test
	public void testSelectSumByType() {
		List<FundSumResult> list = 
				tradeFundExtMapper.selectSumByType(account, "2021-09-01", "2022-09-30");
		
		Map<String, BigDecimal> map = list.stream().collect(Collectors.toMap(FundSumResult::getType, FundSumResult::getProfit));
		
		System.out.println(map);
		
//		map = tradeFundExtMapper.selectSumByType(account, null, "2022-09-09");
//		
//		System.out.println(map);
	}

}
