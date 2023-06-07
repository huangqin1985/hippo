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
	
	public static void add(String connectId, String accountName, Account acc) {
		getCache().put("connectId_" + connectId, acc);
		getCache().put("accountName_" + acc.getName(), acc);
	}
	
	public static void remove(String connectId) {
		Account acc = getCache().getIfPresent("connectId_" + connectId);
		getCache().invalidate("connectId_" + connectId);
		getCache().invalidate("accountName_" + acc.getName());
	}
	
	public static Account getByAccountName(String accountName) {
		return getCache().getIfPresent("accountName_" + accountName);
	}
	
	public static Account getByConnectId(String connectId) {
		return getCache().getIfPresent("connectId_" + connectId);
	}
	
}
