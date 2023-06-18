package cc.fxqq.hippo.cache;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cc.fxqq.hippo.dto.json.MarketMQL;
import cc.fxqq.hippo.dto.json.PositionMQL;
import cc.fxqq.hippo.entity.Account;
import cc.fxqq.hippo.util.DateUtil;
import cc.fxqq.hippo.util.DecimalUtil;

public class AccountCache {

	public static final String MARKET = "market_";

	public static final String CONNECT_ID = "connectId_";

	public static final String ACCOUNT = "account_";

	public static final String POSITION = "position_";

	public static final String BALANCE = "balance_";

	public static final String SERVER_TIME = "serverTime_";
	
	private static Cache<String, Account> cache;
	
	private static Cache<String, Account> getCache() {
		if (cache == null) {
			cache = Caffeine.newBuilder()
					.maximumSize(10_000).
				    build();
		}
		return cache;
	}
	
	public static void addAccount(String connectId, Account acc) {
		getCache().put(CONNECT_ID + connectId, acc);
		getCache().put(ACCOUNT + acc.getId(), acc);
	}
	
	public static Account getByAccountId(Integer accountId) {
		return getCache().getIfPresent(ACCOUNT + accountId);
	}
	
	public static boolean isConnected(Integer accountId) {
		return getCache().getIfPresent(ACCOUNT + accountId) != null;
	}
	
	public static Account getByConnectId(String connectId) {
		return getCache().getIfPresent(CONNECT_ID + connectId);
	}
	
	public static void setMarket(Integer accountId, List<MarketMQL> market) {
		//缓存symbolMargin
		StringCache.put(MARKET + accountId, JSON.toJSONString(market));
	}

	public static List<MarketMQL> getMarket(Integer accountId) {
		String text = StringCache.get(MARKET + accountId);
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		
		return JSON.parseArray(text, MarketMQL.class);
	}
	
	public static void setPosition(Integer accountId, PositionMQL position) {
		StringCache.put(POSITION + accountId, JSON.toJSONString(position));
	}

	public static PositionMQL getPosition(Integer accountId) {
		String text = StringCache.get(POSITION + accountId);
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		
		return JSON.parseObject(text, PositionMQL.class);
	}
	
	public static void removeAll(Integer accountId, String connectId) {
		StringCache.remove(MARKET + accountId);
		StringCache.remove(POSITION + accountId);
		
		Account acc = getCache().getIfPresent(CONNECT_ID + connectId);
		getCache().invalidate(CONNECT_ID + connectId);
		getCache().invalidate(ACCOUNT + accountId);
		getCache().invalidate(SERVER_TIME + accountId);
	}
	
	public static void setServerTime(Integer accountId, String text) {
		StringCache.put(SERVER_TIME + accountId, text);
	}
	
	public static Date getServerTime(Integer accountId) {
		String serverTime = StringCache.get(SERVER_TIME + accountId);
		if (StringUtils.isEmpty(serverTime)) {
			return null;
		}
		return DateUtil.parse(serverTime, DateUtil.SERVER_PATTER);
	}
}
