package cc.fxqq.hippo.cache;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cc.fxqq.hippo.dto.json.MarketMQL;
import cc.fxqq.hippo.dto.json.PositionMQL;
import cc.fxqq.hippo.entity.Account;

public class AccountCache {

	public static final String MARKET = "market_";

	public static final String CONNECT_ID = "connectId_";

	public static final String ACCOUNT_NAME = "accountName_";

	public static final String POSITION = "position_";

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
		getCache().put(ACCOUNT_NAME + acc.getName(), acc);
	}
	
	public static void removeByConnectId(String connectId) {
		Account acc = getCache().getIfPresent(CONNECT_ID + connectId);
		getCache().invalidate(CONNECT_ID + connectId);
		getCache().invalidate(ACCOUNT_NAME + acc.getName());
	}
	
	public static Account getByAccountName(String accountName) {
		return getCache().getIfPresent(ACCOUNT_NAME + accountName);
	}
	
	public static Account getByConnectId(String connectId) {
		return getCache().getIfPresent(CONNECT_ID + connectId);
	}
	
	public static void setMarket(String accountName, List<MarketMQL> market) {
		//缓存symbolMargin
		StringCache.put(MARKET + accountName, JSON.toJSONString(market));
	}
	
	public static void removeMarket(String accountName) {
		StringCache.remove(MARKET + accountName);
	}
	
	public static List<MarketMQL> getMarket(String accountName) {
		String text = StringCache.get(MARKET + accountName);
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		
		return JSON.parseArray(text, MarketMQL.class);
	}
	
	public static void setPosition(String accountName, PositionMQL position) {
		StringCache.put(POSITION + accountName, JSON.toJSONString(position));
	}
	
	public static void removePosition(String accountName) {
		StringCache.remove(POSITION + accountName);
	}
	
	public static PositionMQL getPosition(String accountName) {
		String text = StringCache.get(POSITION + accountName);
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		
		return JSON.parseObject(text, PositionMQL.class);
	}
	
	public static Date getServerTime(String accountName) {
		PositionMQL position = getPosition(accountName);
		if (position != null) {
			return position.getServerTime();
		} else {
			return null;
		}
	}
}
