package cc.fxqq.hippo.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import cc.fxqq.hippo.entity.Account;

public class AccountCache {
	
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
		getCache().put(StringCache.CONNECT_ID + connectId, acc);
		getCache().put(StringCache.ACCOUNT_NAME + acc.getName(), acc);
	}
	
	public static void removeByConnectId(String connectId) {
		Account acc = getCache().getIfPresent(StringCache.CONNECT_ID + connectId);
		getCache().invalidate(StringCache.CONNECT_ID + connectId);
		getCache().invalidate(StringCache.ACCOUNT_NAME + acc.getName());
	}
	
	public static Account getByAccountName(String accountName) {
		return getCache().getIfPresent(StringCache.ACCOUNT_NAME + accountName);
	}
	
	public static Account getByConnectId(String connectId) {
		return getCache().getIfPresent(StringCache.CONNECT_ID + connectId);
	}
	
}
