package cc.fxqq.hippo.dto.json;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class OrderMQL {

	private BigDecimal balance; // 余额

	private List<TradeOrderMQL> tradeOrders;

}
