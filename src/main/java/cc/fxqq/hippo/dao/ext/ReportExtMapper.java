package cc.fxqq.hippo.dao.ext;

import java.math.BigDecimal;

import cc.fxqq.hippo.entity.Report;

public interface ReportExtMapper extends PageQuery<Report> {

    Report selectUnique(Integer accountId, String type, String startDate);
    
    BigDecimal selectIncome(Integer accountId);
}