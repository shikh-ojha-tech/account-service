package com.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Transaction after it was booked")
public class CreateTransactionResponse {

    @Schema(description = "Account that was updated")
    private UUID accountId;

    @Schema(description = "New transaction id")
    private UUID transactionId;

    @Schema(description = "Money moved", example = "10.00")
    private BigDecimal amount;

    @Schema(description = "Currency of the move", example = "EUR")
    private String currency;

    @Schema(description = "IN adds money, OUT takes money out", example = "IN")
    private String direction;

    @Schema(description = "Short note about the move", example = "Deposit")
    private String description;

    @Schema(description = "Balance in that currency after this move", example = "10.00")
    private BigDecimal balanceAfterTransaction;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(BigDecimal balanceAfterTransaction) {
        this.balanceAfterTransaction = balanceAfterTransaction;
    }
}
