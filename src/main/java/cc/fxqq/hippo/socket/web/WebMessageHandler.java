package cc.fxqq.hippo.socket.web;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;

@Component
public class WebMessageHandler implements WebSocketHandler {

	private Cache<Integer, List<WebSocketSession>> cache;
	
	public Cache<Integer, List<WebSocketSession>> getCache() {
		if (cache == null) {
			cache = Caffeine.newBuilder()
					.maximumSize(10_000).
				    build();
		}
		return cache;
	}
	
	public void addSession(Integer accountId, WebSocketSession session) {
		if (cache == null) {
			cache = getCache();
		}
		List<WebSocketSession> list = cache.getIfPresent(accountId);
		if (list == null) {
			list = Collections.synchronizedList(Lists.newArrayList());
		}
		list.add(session);
		cache.put(accountId, list);
	}
	
	public void removeSession(Integer accountId, WebSocketSession session) {
		if (cache == null) {
			cache = getCache();
		}
		List<WebSocketSession> list = cache.getIfPresent(accountId);
		if (list != null) {
			list.remove(session);
		}
	}
	
	public void sendMessage(Integer accountId, String msg) {
		if (cache == null) {
			cache = getCache();
		}
		List<WebSocketSession> list = cache.getIfPresent(accountId);
		if (list != null) {
			Iterator<WebSocketSession> ite = list.listIterator();
			while(ite.hasNext()) {
				WebSocketMessage<?> message = new TextMessage(msg);
				try {
					ite.next().sendMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Integer accountId = getAccountId(session);
		if (accountId != null) {
			addSession(accountId, session);
		}
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		Integer accountId = getAccountId(session);
		if (accountId != null) {
			removeSession(accountId, session);
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	private Integer getAccountId(WebSocketSession session) {
		try {
			Integer accountId = (Integer) session.getAttributes().get("accountId");
			return accountId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
