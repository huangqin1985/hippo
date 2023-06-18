package cc.fxqq.hippo.entity;

import java.math.BigDecimal;

public class ComplexOrder {
    private Integer accountId;

    private String ticket;

    private String openTime;

    private String closeTime;

    private String symbol;

    private BigDecimal lots;

    private BigDecimal commission;

    private BigDecimal swap;

    private BigDecimal profit;

    private BigDecimal realProfit;

    private String type;

    private String openPrice;

    private String closePrice;

    private String stopLoss;

    private String takeProfit;

    private String comment;

    private String status;

    private String parentTicket;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket == null ? null : ticket.trim();
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime == null ? null : openTime.trim();
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime == null ? null : closeTime.trim();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public BigDecimal getLots() {
        return lots;
    }

    public void setLots(BigDecimal lots) {
        this.lots = lots;
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

    public BigDecimal getRealProfit() {
        return realProfit;
    }

    public void setRealProfit(BigDecimal realProfit) {
        this.realProfit = realProfit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice == null ? null : openPrice.trim();
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice == null ? null : closePrice.trim();
    }

    public String getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(String stopLoss) {
        this.stopLoss = stopLoss == null ? null : stopLoss.trim();
    }

    public String getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(String takeProfit) {
        this.takeProfit = takeProfit == null ? null : takeProfit.trim();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getParentTicket() {
        return parentTicket;
    }

    public void setParentTicket(String parentTicket) {
        this.parentTicket = parentTicket == null ? null : parentTicket.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", accountId=").append(accountId);
        sb.append(", ticket=").append(ticket);
        sb.append(", openTime=").append(openTime);
        sb.append(", closeTime=").append(closeTime);
        sb.append(", symbol=").append(symbol);
        sb.append(", lots=").append(lots);
        sb.append(", commission=").append(commission);
        sb.append(", swap=").append(swap);
        sb.append(", profit=").append(profit);
        sb.append(", realProfit=").append(realProfit);
        sb.append(", type=").append(type);
        sb.append(", openPrice=").append(openPrice);
        sb.append(", closePrice=").append(closePrice);
        sb.append(", stopLoss=").append(stopLoss);
        sb.append(", takeProfit=").append(takeProfit);
        sb.append(", comment=").append(comment);
        sb.append(", status=").append(status);
        sb.append(", parentTicket=").append(parentTicket);
        sb.append("]");
        return sb.toString();
    }
}