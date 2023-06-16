package cc.fxqq.hippo.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class StringCache {
	
	private static Cache<String, String> cache;
	
	private static Cache<String, String> getCache() {
		if (cache == null) {
			cache = Caffeine.newBuilder()
					.maximumSize(10_000).
				    build();
		}
		return cache;
	}
	
	public static void put(String key, String val) {
		getCache().put(key, val);
	}
	
	public static void remove(String key) {
		getCache().invalidate(key);
	}
	
	public static String get(String key) {
		return getCache().getIfPresent(key);
	}

}
