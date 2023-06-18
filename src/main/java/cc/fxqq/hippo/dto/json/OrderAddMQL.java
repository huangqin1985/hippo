package cc.fxqq.hippo.dto.json;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class OrderAddMQL {

	private List<OrderMQL> tradeOrders;

	private BigDecimal balance;
}
