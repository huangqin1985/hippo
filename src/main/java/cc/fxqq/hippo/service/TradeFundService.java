package cc.fxqq.hippo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.fxqq.hippo.dao.ext.TradeFundExtMapper;
import cc.fxqq.hippo.dto.template.Pager;
import cc.fxqq.hippo.dto.template.TradeFundDTO;
import cc.fxqq.hippo.entity.param.TradeFundParam;
import cc.fxqq.hippo.util.DecimalUtil;
import cc.fxqq.hippo.util.PageUtil;

@Service
public class TradeFundService {
	
	@Autowired
	private TradeFundExtMapper tradeFundExtMapper;

	public Pager<TradeFundDTO> getFundList(TradeFundParam query) {
		Pager<TradeFundDTO> pager = PageUtil.selectPage(tradeFundExtMapper, query,
			t -> {
				TradeFundDTO dto = new TradeFundDTO();
				dto.setProfit(DecimalUtil.get(t.getProfit()));
				dto.setOpenTime(t.getOpenTime());
				dto.setComment(t.getComment());
				return dto;
			}); 
		
		return pager;
	}
}