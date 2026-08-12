package com.account.service;

import com.account.domain.Account;
import com.account.domain.Balance;
import com.account.domain.Currency;
import com.account.exception.DomainException;
import com.account.exception.ErrorCodes;
import com.account.messaging.AccountEvent;
import com.account.messaging.AccountEventPublisher;
import com.account.persistence.AccountMapper;
import com.account.persistence.BalanceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final AccountEventPublisher eventPublisher;

    public AccountService(AccountMapper accountMapper,
                          BalanceMapper balanceMapper,
                          AccountEventPublisher eventPublisher) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AccountDetails createAccount(String customerId, String country, List<String> currencies) {
        if (!StringUtils.hasText(customerId)) {
            throw new DomainException(ErrorCodes.INVALID_REQUEST, "Customer ID is required");
        }
        if (!StringUtils.hasText(country)) {
            throw new DomainException(ErrorCodes.INVALID_REQUEST, "Country is required");
        }

        Set<Currency> parsedCurrencies = parseCurrencies(currencies);

        Account account = new Account();
        account.setAccountId(UUID.randomUUID());
        account.setCustomerId(customerId.trim());
        account.setCountry(country.trim().toUpperCase());
        accountMapper.insert(account);
        eventPublisher.publish(new AccountEvent(AccountEvent.ACCOUNT_CREATED, account.getAccountId(), account));

        List<Balance> balances = new ArrayList<>();
        for (Currency currency : parsedCurrencies) {
            Balance balance = new Balance();
            balance.setBalanceId(UUID.randomUUID());
            balance.setAccountId(account.getAccountId());
            balance.setCurrency(currency);
            balance.setAvailableAmount(BigDecimal.ZERO.setScale(2));
            balanceMapper.insert(balance);
            eventPublisher.publish(new AccountEvent(AccountEvent.BALANCE_CREATED, account.getAccountId(), balance));
            balances.add(balance);
        }

        return new AccountDetails(account, balances);
    }

    @Transactional(readOnly = true)
    public AccountDetails getAccount(UUID accountId) {
        Account account = accountMapper.findById(accountId)
                .orElseThrow(() -> new DomainException(ErrorCodes.ACCOUNT_NOT_FOUND, "Account not found"));
        List<Balance> balances = balanceMapper.findByAccountId(accountId);
        return new AccountDetails(account, balances);
    }

    private Set<Currency> parseCurrencies(List<String> currencies) {
        if (currencies == null || currencies.isEmpty()) {
            throw new DomainException(ErrorCodes.INVALID_CURRENCY, "Invalid currency");
        }

        Set<Currency> parsed = new LinkedHashSet<>();
        for (String value : currencies) {
            Currency currency = Currency.from(value)
                    .orElseThrow(() -> new DomainException(ErrorCodes.INVALID_CURRENCY, "Invalid currency"));
            parsed.add(currency);
        }
        return parsed;
    }
}
