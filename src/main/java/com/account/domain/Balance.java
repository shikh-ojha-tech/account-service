package com.account.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Balance {

    private UUID balanceId;

    private UUID accountId;

    private BigDecimal availableAmount;

    private Currency currency;

    public UUID getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(UUID balanceId) {
        this.balanceId = balanceId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
