package com.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Balance for one currency")
public class BalanceDto {

    @Schema(description = "How much is available", example = "10.00")
    private BigDecimal availableAmount;

    @Schema(description = "Currency code", example = "EUR")
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
