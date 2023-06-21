package cc.fxqq.hippo.test;

import java.math.BigDecimal;
import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.MimeMessageHelper;

import cc.fxqq.hippo.dto.json.OrderMQL;
import cc.fxqq.hippo.notify.MailService;
import cc.fxqq.hippo.notify.MailTypeEnum;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;

@SpringBootTest
public class MailTest {
	
	// JavaMailSender 在Mail 自动配置类 MailSenderAutoConfiguration 中已经导入，这里直接注入使用即可
    @Autowired
    private MailService mailService;
    
    @Test
    public void test() throws Exception {
    	OrderMQL order = new OrderMQL();
    	order.setSymbol("USDJPY.p");
    	order.setType("sell");
    	order.setLots(DecimalUtil.get(1.0));
    	order.setOpenPrice("144.350");
    	order.setClosePrice("145.350");
    	order.setCloseTime(new Date());
    	order.setProfit(DecimalUtil.get(2.0));
    	order.setProfit(DecimalUtil.get(123.0));
    	order.setTicket("83672074");
    	mailService.sendTradeInfo(MailTypeEnum.SL.getValue(), order);
    }
}
