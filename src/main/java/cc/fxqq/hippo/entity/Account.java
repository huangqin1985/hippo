package cc.fxqq.hippo.entity;

import java.math.BigDecimal;

public class Account {
    private Integer id;

    private String name;

    private Integer number;

    private String company;

    private String server;

    private String currency;

    private Integer leverage;

    private BigDecimal balance;

    private Integer timeZone;

    private String clientName;

    private String stopOutLevel;

    private String createTime;

    private String updateTime;

    private String connectTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company == null ? null : company.trim();
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server == null ? null : server.trim();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public Integer getLeverage() {
        return leverage;
    }

    public void setLeverage(Integer leverage) {
        this.leverage = leverage;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Integer timeZone) {
        this.timeZone = timeZone;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName == null ? null : clientName.trim();
    }

    public String getStopOutLevel() {
        return stopOutLevel;
    }

    public void setStopOutLevel(String stopOutLevel) {
        this.stopOutLevel = stopOutLevel == null ? null : stopOutLevel.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime == null ? null : updateTime.trim();
    }

    public String getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(String connectTime) {
        this.connectTime = connectTime == null ? null : connectTime.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", number=").append(number);
        sb.append(", company=").append(company);
        sb.append(", server=").append(server);
        sb.append(", currency=").append(currency);
        sb.append(", leverage=").append(leverage);
        sb.append(", balance=").append(balance);
        sb.append(", timeZone=").append(timeZone);
        sb.append(", clientName=").append(clientName);
        sb.append(", stopOutLevel=").append(stopOutLevel);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", connectTime=").append(connectTime);
        sb.append("]");
        return sb.toString();
    }
}