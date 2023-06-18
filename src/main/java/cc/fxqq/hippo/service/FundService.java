package cc.fxqq.hippo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cc.fxqq.hippo.dao.ext.FundExtMapper;
import cc.fxqq.hippo.dto.template.Pager;
import cc.fxqq.hippo.dto.template.FundDTO;
import cc.fxqq.hippo.entity.param.FundParam;
import cc.fxqq.hippo.util.DecimalUtil;
import cc.fxqq.hippo.util.PageUtil;

@Service
public class FundService {
	
	@Autowired
	private FundExtMapper tradeFundExtMapper;

	public Pager<FundDTO> getFundList(FundParam query) {
		Pager<FundDTO> pager = PageUtil.selectPage(tradeFundExtMapper, query,
			t -> {
				FundDTO dto = new FundDTO();
				dto.setProfit(DecimalUtil.get(t.getProfit()));
				dto.setOpenTime(t.getOpenTime());
				dto.setComment(t.getComment());
				return dto;
			}); 
		
		return pager;
	}
}