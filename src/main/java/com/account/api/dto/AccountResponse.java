package com.account.api.dto;

import java.util.List;
import java.util.UUID;

public class AccountResponse {

    private UUID accountId;

    private String customerId;

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
