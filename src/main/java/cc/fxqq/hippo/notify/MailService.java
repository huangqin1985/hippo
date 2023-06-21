package cc.fxqq.hippo.notify;

import java.math.BigDecimal;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import cc.fxqq.hippo.dto.json.OrderMQL;
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
    
    private void sedMsg(String subject, String text) {
    	MimeMessage mimeMessage = mailSender.createMimeMessage();
        
    	try {
    		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom(address);
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
 
    public void sendTradeInfo(String mailType, OrderMQL order) {
        
        StringBuilder subject = new StringBuilder();
        subject.append("【").append(mailType).append("】").append(order.getSymbol()).append(" ");
        subject.append(order.getClosePrice());
        
        StringBuilder text = new StringBuilder();
        text.append("<div>").append(order.getSymbol()).append(" ").append(order.getType());
        text.append(" ").append(DecimalUtil.get(order.getLots())).append("</div>");
        text.append("<div>");
        if (MailTypeEnum.SL.getValue().equals(mailType) ||
        		MailTypeEnum.TP.getValue().equals(mailType)) {
        	text.append(order.getOpenPrice() + " → " + order.getClosePrice());
        } else {
        	text.append(order.getClosePrice());
        }
        text.append("</div>");
        if (MailTypeEnum.SL.getValue().equals(mailType) ||
        		MailTypeEnum.TP.getValue().equals(mailType)) {
        	
        	BigDecimal realProfit = DecimalUtil.add(order.getSwap(), order.getCommission(), order.getProfit());
        	text.append("<div>利润：").append(realProfit);
            text.append("</div>");
        }
        
        sedMsg(subject.toString(), text.toString());
    }
}
