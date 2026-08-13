package com.account.model;

import com.account.domain.Account;
import com.account.domain.Balance;

import java.util.List;

public class AccountDetails {

    private final Account account;
    private final List<Balance> balances;

    public AccountDetails(Account account, List<Balance> balances) {
        this.account = account;
        this.balances = List.copyOf(balances);
    }

    public Account getAccount() {
        return account;
    }

    public List<Balance> getBalances() {
        return balances;
    }
}
