package cc.fxqq.hippo.test;

import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import cc.fxqq.hippo.dto.json.ConnectMQL;

public class CacheTest implements RemovalListener<String, String> {

	@Test
	public void test() {
		Cache<String, String> cache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS)
					.maximumSize(10_000)
					.evictionListener(this).
				    build();
		cache.put("111", "xxx");
		
		System.out.println(2);cache.getIfPresent("111");
	}

	@Override
	public void onRemoval(@Nullable String key, @Nullable String value, @NonNull RemovalCause cause) {

		System.out.printf("Key %s was removed (%s)%n", key, cause);
	}
}

