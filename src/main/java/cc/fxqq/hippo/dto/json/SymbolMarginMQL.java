package cc.fxqq.hippo.dto.json;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

import lombok.Data;

@Data
@JSONType
public class SymbolMarginMQL {

	private String freeMargin;
	
	private List<SymbolMarginDataMQL> data;
}
