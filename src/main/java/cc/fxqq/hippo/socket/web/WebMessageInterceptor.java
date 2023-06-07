package cc.fxqq.hippo.socket.web;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class WebMessageInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
			org.springframework.http.server.ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		String account = ((ServletServerHttpRequest) request).getServletRequest().getParameter("accountId");
	    if (StringUtils.isEmpty(account)) {
	    	return false;
	    } else {
	    	attributes.put("accountId", Integer.parseInt(account));
	    	return true;
	    }
	}

	@Override
	public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
			org.springframework.http.server.ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {

	}

}
