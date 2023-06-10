package cc.fxqq.hippo.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class CommonUtil {

	public static <K, V> Map<K, V> parseToMap(String json, 
            Class<K> keyType, 
            Class<V> valueType) {
		return JSON.parseObject(json, 
				new TypeReference<Map<K, V>>(keyType, valueType) {
				});
}
}
