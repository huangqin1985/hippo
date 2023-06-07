package cc.fxqq.hippo.test.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

import cc.fxqq.hippo.dao.ext.TradeOrderExtMapper;
import cc.fxqq.hippo.entity.Report;
import cc.fxqq.hippo.entity.TradeOrder;
import cc.fxqq.hippo.entity.param.TradeOrderParam;
import cc.fxqq.hippo.service.ReportService;
import cc.fxqq.hippo.util.DecimalUtil;
import cc.fxqq.hippo.util.ReportUtils;

@SpringBootTest
@EnableAsync
/* @TestPropertySource("classpath:application.properties") */
public class ReportServiceTest {

	protected final static Logger logger = LoggerFactory.getLogger(ReportServiceTest.class);

	@Autowired
	private ReportService reportService;
	
	@Autowired
	private TradeOrderExtMapper tradeOrderExtMapper;
	
	@Test
	public void testMaxAndMin() {
		TradeOrderParam query = new TradeOrderParam();
		query.setAccountId(1);
		query.setCloseStartDate("2020-12-01");
		query.setCloseEndDate("2020-12-31");
		query.setRows(Integer.MAX_VALUE);//查询所有数据
		query.setOrderBy("close_time");
		
		List<TradeOrder> orders = tradeOrderExtMapper.selectPage(query);
		
		BigDecimal max = BigDecimal.ZERO;
		BigDecimal min = BigDecimal.ZERO;
		
		BigDecimal totalProfit = BigDecimal.ZERO;
		for (TradeOrder order : orders) {
			totalProfit = DecimalUtil.add(totalProfit, order.getRealProfit());
			
			max = DecimalUtil.max(max, totalProfit);
			min = DecimalUtil.min(min, totalProfit);
			
			System.out.println(order.getCloseTime() + ", " + totalProfit);
		}
		System.out.println(min + ", " + max);
		
	}
	
	public static void main(String[] args) {
		String date = "2022-08-31 21:21:38";
		
		List<Report> list = ReportUtils.getWeekReportList(date);
		
		list.stream().forEach(t -> {
			System.out.println(t.getStartDate() + "-" + t.getEndDate());
		});
	}
}
