package cc.fxqq.hippo.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cc.fxqq.hippo.dao.ext.BatchOperator;
import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BatchUtil {

	public static <T> void replaceBatch(BatchOperator<T> oper, List<T> list) {
		// 批量插入
		int maxSend = 1000; // 数量
		int size = list.size();
		
		Stream.iterate(0, n -> n + 1).limit((size + maxSend - 1) / maxSend).parallel().forEach(i -> {
	          List<T> subList = list.stream().skip(i * maxSend).limit(maxSend).collect(Collectors.toList());
	          oper.replaceBatch(subList);
		});
	}
}
