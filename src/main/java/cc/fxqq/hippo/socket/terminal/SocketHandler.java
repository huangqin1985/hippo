package cc.fxqq.hippo.socket.terminal;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cc.fxqq.hippo.cache.AccountCache;
import cc.fxqq.hippo.cache.StringCache;
import cc.fxqq.hippo.dto.json.ConnectMQL;
import cc.fxqq.hippo.dto.json.PositionMQL;
import cc.fxqq.hippo.dto.json.OrderMQL;
import cc.fxqq.hippo.dto.json.TradeOrderMQL;
import cc.fxqq.hippo.dto.template.PositionDTO;
import cc.fxqq.hippo.entity.Account;
import cc.fxqq.hippo.service.AccountService;
import cc.fxqq.hippo.service.ReportService;
import cc.fxqq.hippo.service.TradeOrderService;
import cc.fxqq.hippo.socket.web.WebMessageHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Socket拦截器，用于处理客户端的行为
 *
 **/
@Slf4j
@Component
@Sharable
public class SocketHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private AccountService accountService;
	
	@Autowired
    private ReportService reportService;
	
	@Autowired
    private TradeOrderService tradeOrderService;
	
	@Autowired
	private WebMessageHandler webMessageHandler;
	
	/*
	 * 获取账户标识
	 */
	private String parseName(String server, Integer number) {
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(server);

        if (matcher.find()) {
            return matcher.group() + "-" + number;
        } else {
        	throw new IllegalArgumentException("servier is illegal: " + server);
        }
	}
    /**
     * 读取到客户端发来的消息
     *
     * @param ctx ChannelHandlerContext
     * @param msg msg
     * @throws Exception e
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        
    	String str = (String) msg;
    	String id = ctx.channel().id().asShortText();
    	
    	// 处理连接数据
    	if (str.startsWith("connect:")) {
    		String text = str.substring(str.indexOf(':') + 1);
			ConnectMQL connectMQL = JSON.parseObject(text, ConnectMQL.class);
			String name = parseName(connectMQL.getServer(), connectMQL.getNumber());
			
			Account acc = accountService.queryAccount(name);
			// 账号不存在
			if (acc == null) {
				acc = accountService.addAccount(name, connectMQL);
			} else {
				accountService.updateAccount(acc, connectMQL);
			}
			log.info("账号" + name + "连接成功 connectId=" + id);

			AccountCache.add(id, name, acc);
			
			// 更新历史订单
			List<TradeOrderMQL> list = connectMQL.getHistories();
			
    		if (list != null && list.size() > 0) {
    			tradeOrderService.updateHistoryOrders(acc, list);
    		}
			
    	} else if (str.startsWith("orders:")) {
    		String text = str.substring(str.indexOf(':') + 1);
    		
    		OrderMQL orderMQL = JSON.parseObject(text, OrderMQL.class);
    		
    		Account account = AccountCache.getByConnectId(id);
    		
    		if (account == null) {
    			log.info("连接账号不存在");
    			return;
    		}
    		
    		List<TradeOrderMQL> list = orderMQL.getTradeOrders();
			
    		if (list != null && list.size() > 0) {
    			account.setBalance(orderMQL.getBalance());
    			accountService.setAccountBalance(account);
        		
    			tradeOrderService.updateOrder(account, list);
    		}
    		
    	} else if (str.startsWith("position:")) {
    		String text = str.substring(str.indexOf(':') + 1);
    		PositionMQL position = JSON.parseObject(text, PositionMQL.class);
    		
    		Account account = AccountCache.getByConnectId(id);
    		
    		if (account == null) {
    			return;
    		}
    		
    		StringCache.put("position_" + account.getId(), text);

    		reportService.updateReportStatus(account.getId(),
    				position.getEquity(), position.getProfit(), position.getMargin(),
    				position.getServerTime());
    		
    		//String json = JSON.toJSONString(new PositionDTO(position));
    		//webMessageHandler.sendMessage(account.getId(), json);
    		
    	} else if (str.startsWith("setTimeZone:")) {
    		String text = str.substring(str.indexOf(':') + 1);
    		ConnectMQL connectMQL = JSON.parseObject(text, ConnectMQL.class);
    		Account account = AccountCache.getByConnectId(id);
    		
    		if (account != null) {
    			accountService.setTimeZone(account, connectMQL.getTimeZone());
    		}
    		
    	} else if (str.startsWith("margin:")) {
    		String text = str.substring(str.indexOf(':') + 1);
    		ConnectMQL connectMQL = JSON.parseObject(text, ConnectMQL.class);
    		Account account = AccountCache.getByConnectId(id);
    		
    		if (account != null) {
    			accountService.setSymbolMargin(account.getName(), connectMQL.getSymbolMargins());
    		}
    		
    	}
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    	String id = ctx.channel().id().asShortText();
    	Account acc = AccountCache.getByConnectId(id);
    	log.info("账号" + acc.getName() + "连接已断开");
    	
    	StringCache.remove("position_" + acc.getId());
    	AccountCache.remove(id);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        String id = ctx.channel().id().asShortText();
    	Account acc = AccountCache.getByConnectId(id);
    	log.info("账号" + acc.getName() + "连接异常");
    	
    	StringCache.remove("position_" + acc.getId());
    	AccountCache.remove(id);
        ctx.channel().close();
    }
}