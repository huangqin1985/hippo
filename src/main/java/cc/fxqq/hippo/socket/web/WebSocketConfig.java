package cc.fxqq.hippo.socket.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration  
@EnableWebSocket  
public class WebSocketConfig implements WebSocketConfigurer {  
	
	@Autowired
	private WebMessageHandler webMessageHandler;
	
	@Autowired
	private WebMessageInterceptor webMessageInterceptor;
	
    /** 
     * 注册handle 
     * @see org.springframework.web.socket.config.annotation.WebSocketConfigurer#registerWebSocketHandlers(org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry) 
     */  
    @Override  
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {  
    	registry.addHandler(webMessageHandler, "/ws").setAllowedOrigins("*")
        .addInterceptors(webMessageInterceptor);
    }  
      
}
