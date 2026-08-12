package com.account.api.dto;

import java.math.BigDecimal;

public class BalanceDto {

    private BigDecimal availableAmount;

    private String currency;

    public BalanceDto() {
    }

    public BalanceDto(BigDecimal availableAmount, String currency) {
        this.availableAmount = availableAmount;
        this.currency = currency;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
