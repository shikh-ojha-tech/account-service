package com.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Account with its currency balances")
public class AccountResponse {

    @Schema(description = "Account id")
    private UUID accountId;

    @Schema(description = "Customer who owns the account", example = "cust-1")
    private String customerId;

    @Schema(description = "Money available per currency")
    private List<BalanceDto> balances;

    public AccountResponse() {
    }

    public AccountResponse(UUID accountId, String customerId, List<BalanceDto> balances) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balances = balances;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<BalanceDto> getBalances() {
        return balances;
    }

    public void setBalances(List<BalanceDto> balances) {
        this.balances = balances;
    }
}
