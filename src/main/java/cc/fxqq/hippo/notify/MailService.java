package cc.fxqq.hippo.notify;

import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import cc.fxqq.hippo.dto.json.TradeOrderMQL;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MailService {

	@Value("${spring.mail.username}") 
	private String address;
	
	@Value("${url.home}")
	private String homeUrl;
	
	@Value("${mail.to}")
	private String mailTo;
	
    @Autowired
    private JavaMailSender mailSender;
    
    private void sedMsg(Integer account, String subject, String text) {
    	MimeMessage mimeMessage = mailSender.createMimeMessage();
        
    	try {
    		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom(address, String.valueOf(account));
            helper.setTo(mailTo);
            helper.setPriority(1);
            helper.setSubject(subject);
            helper.setText(text, true); //支持html
            mailSender.send(mimeMessage);
            
            log.info("send mail: " + subject);
    	} catch(Exception e) {
    		e.printStackTrace();
    		log.warn(e.getMessage());
    	}
    }
 
    public void send(String type, Integer account, TradeOrderMQL order) {
    	String prefix = "";
    	if (type.equals("tp")) {
	    	prefix = "【获利】";
	    } else if (type.equals("sl")) {
	    	prefix = "【止损】";
	    } else if (type.equals("so")) {
	    	prefix = "【爆仓】";
	    }
        
        StringBuilder subject = new StringBuilder();
        subject.append(prefix).append(order.getSymbol()).append(" ");
        subject.append(order.getClosePrice()).append(" ");
        subject.append(order.getProfit());
        
        Date closeTime = order.getCloseTime();
        Date startDate = DateUtil.getStartDateOfWeek(closeTime);
        Date endDate = DateUtil.getEndDateOfWeek(closeTime);
        
        StringBuilder text = new StringBuilder();
        text.append("<p>").append(account).append("</p>");
        text.append("<p>").append(order.getSymbol()).append(" ").append(order.getType());
        text.append(" ").append(DecimalUtil.get(order.getLots())).append("</p>");
        text.append("<p>").append(order.getOpenPrice()).append(" → ").append(order.getClosePrice());
        text.append("</p><p>").append(DecimalUtil.get(order.getProfit())).append("</p>");
        text.append("<p><a href=\"").append(homeUrl).append("/order?account=");
        text.append(account).append("&startDate=").append(DateUtil.formatDate(startDate));
        text.append("&endDate=").append(DateUtil.formatDate(endDate));
        text.append("&ticket=").append(order.getTicket());
        text.append("\">查看详细</a></p>");
        
        sedMsg(account, subject.toString(), text.toString());
    }
}
