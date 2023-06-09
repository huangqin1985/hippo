package cc.fxqq.hippo.entity;

import java.math.BigDecimal;

public class Report {
    private Integer id;

    private Integer accountId;

    private String type;

    private String startDate;

    private String endDate;

    private BigDecimal preBalance;

    private BigDecimal preEquity;

    private BigDecimal balance;

    private BigDecimal equity;

    private BigDecimal realProfit;

    private BigDecimal commission;

    private BigDecimal swap;

    private BigDecimal profit;

    private BigDecimal lots;

    private Integer orderNum;

    private BigDecimal deposit;

    private BigDecimal withdraw;

    private BigDecimal other;

    private BigDecimal maxEquity;

    private BigDecimal minEquity;

    private BigDecimal maxRealProfit;

    private BigDecimal minRealProfit;

    private BigDecimal maxProfit;

    private BigDecimal minProfit;

    private BigDecimal maxMargin;

    private BigDecimal minMarginRate;

    private Integer extra;

    private String updateTime;

    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate == null ? null : startDate.trim();
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate == null ? null : endDate.trim();
    }

    public BigDecimal getPreBalance() {
        return preBalance;
    }

    public void setPreBalance(BigDecimal preBalance) {
        this.preBalance = preBalance;
    }

    public BigDecimal getPreEquity() {
        return preEquity;
    }

    public void setPreEquity(BigDecimal preEquity) {
        this.preEquity = preEquity;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getEquity() {
        return equity;
    }

    public void setEquity(BigDecimal equity) {
        this.equity = equity;
    }

    public BigDecimal getRealProfit() {
        return realProfit;
    }

    public void setRealProfit(BigDecimal realProfit) {
        this.realProfit = realProfit;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getSwap() {
        return swap;
    }

    public void setSwap(BigDecimal swap) {
        this.swap = swap;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getLots() {
        return lots;
    }

    public void setLots(BigDecimal lots) {
        this.lots = lots;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(BigDecimal withdraw) {
        this.withdraw = withdraw;
    }

    public BigDecimal getOther() {
        return other;
    }

    public void setOther(BigDecimal other) {
        this.other = other;
    }

    public BigDecimal getMaxEquity() {
        return maxEquity;
    }

    public void setMaxEquity(BigDecimal maxEquity) {
        this.maxEquity = maxEquity;
    }

    public BigDecimal getMinEquity() {
        return minEquity;
    }

    public void setMinEquity(BigDecimal minEquity) {
        this.minEquity = minEquity;
    }

    public BigDecimal getMaxRealProfit() {
        return maxRealProfit;
    }

    public void setMaxRealProfit(BigDecimal maxRealProfit) {
        this.maxRealProfit = maxRealProfit;
    }

    public BigDecimal getMinRealProfit() {
        return minRealProfit;
    }

    public void setMinRealProfit(BigDecimal minRealProfit) {
        this.minRealProfit = minRealProfit;
    }

    public BigDecimal getMaxProfit() {
        return maxProfit;
    }

    public void setMaxProfit(BigDecimal maxProfit) {
        this.maxProfit = maxProfit;
    }

    public BigDecimal getMinProfit() {
        return minProfit;
    }

    public void setMinProfit(BigDecimal minProfit) {
        this.minProfit = minProfit;
    }

    public BigDecimal getMaxMargin() {
        return maxMargin;
    }

    public void setMaxMargin(BigDecimal maxMargin) {
        this.maxMargin = maxMargin;
    }

    public BigDecimal getMinMarginRate() {
        return minMarginRate;
    }

    public void setMinMarginRate(BigDecimal minMarginRate) {
        this.minMarginRate = minMarginRate;
    }

    public Integer getExtra() {
        return extra;
    }

    public void setExtra(Integer extra) {
        this.extra = extra;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime == null ? null : updateTime.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", accountId=").append(accountId);
        sb.append(", type=").append(type);
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", preBalance=").append(preBalance);
        sb.append(", preEquity=").append(preEquity);
        sb.append(", balance=").append(balance);
        sb.append(", equity=").append(equity);
        sb.append(", realProfit=").append(realProfit);
        sb.append(", commission=").append(commission);
        sb.append(", swap=").append(swap);
        sb.append(", profit=").append(profit);
        sb.append(", lots=").append(lots);
        sb.append(", orderNum=").append(orderNum);
        sb.append(", deposit=").append(deposit);
        sb.append(", withdraw=").append(withdraw);
        sb.append(", other=").append(other);
        sb.append(", maxEquity=").append(maxEquity);
        sb.append(", minEquity=").append(minEquity);
        sb.append(", maxRealProfit=").append(maxRealProfit);
        sb.append(", minRealProfit=").append(minRealProfit);
        sb.append(", maxProfit=").append(maxProfit);
        sb.append(", minProfit=").append(minProfit);
        sb.append(", maxMargin=").append(maxMargin);
        sb.append(", minMarginRate=").append(minMarginRate);
        sb.append(", extra=").append(extra);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}