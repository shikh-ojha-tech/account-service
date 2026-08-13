package com.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "One row from transaction history")
public class TransactionResponse {

    @Schema(description = "Account id")
    private UUID accountId;

    @Schema(description = "Transaction id")
    private UUID transactionId;

    @Schema(description = "Money moved", example = "10.00")
    private BigDecimal amount;

    @Schema(description = "Currency of the move", example = "EUR")
    private String currency;

    @Schema(description = "IN or OUT", example = "OUT")
    private String direction;

    @Schema(description = "Short note about the move", example = "Spend")
    private String description;

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
}
