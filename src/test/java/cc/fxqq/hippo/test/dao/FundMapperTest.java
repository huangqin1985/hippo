package cc.fxqq.hippo.test.dao;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import cc.fxqq.hippo.dao.FundMapper;
import cc.fxqq.hippo.dao.ext.FundExtMapper;
import cc.fxqq.hippo.entity.Fund;

@SpringBootTest
/* @TestPropertySource("classpath:application.properties") */
public class FundMapperTest {
	
	public static final int account = 1;

	private Fund create(String ticket) {
		Fund fund = new Fund();
		fund.setAccountId(account);
		fund.setOpenTime("2022-09-08 22:00:08");
		fund.setProfit(new BigDecimal("1000.25"));
		fund.setTicket(ticket);
		fund.setComment("-----");
		return fund;
	}
	
	@Autowired
	protected FundExtMapper tradeFundExtMapper;

	@Autowired
	protected FundMapper tradeFundMapper;

	@Test
	@Transactional
	public void testReplaceBatch() {
		List<Fund> list = Lists.newArrayList();
		list.add(create("100"));
		list.add(create("101"));
		list.add(create("100"));
		
		tradeFundExtMapper.replaceBatch(list);
	}
	
	@Test
	public void testSelectSumByType() {
		
		
//		map = tradeFundExtMapper.selectSumByType(account, null, "2022-09-09");
//		
//		System.out.println(map);
	}

}
