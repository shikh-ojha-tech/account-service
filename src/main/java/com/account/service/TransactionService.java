package com.account.service;

import com.account.domain.Balance;
import com.account.domain.Currency;
import com.account.domain.Direction;
import com.account.domain.Transaction;
import com.account.exception.DomainException;
import com.account.exception.ErrorCodes;
import com.account.messaging.AccountEvent;
import com.account.messaging.AccountEventPublisher;
import com.account.persistence.AccountMapper;
import com.account.persistence.BalanceMapper;
import com.account.persistence.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final TransactionMapper transactionMapper;
    private final AccountEventPublisher eventPublisher;

    public TransactionService(AccountMapper accountMapper,
                              BalanceMapper balanceMapper,
                              TransactionMapper transactionMapper,
                              AccountEventPublisher eventPublisher) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionMapper = transactionMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Transaction createTransaction(UUID accountId,
                                         BigDecimal amount,
                                         String currencyValue,
                                         String directionValue,
                                         String description) {
        if (accountId == null) {
            throw new DomainException(ErrorCodes.ACCOUNT_NOT_FOUND, "Account missing");
        }
        if (!StringUtils.hasText(description)) {
            throw new DomainException(ErrorCodes.DESCRIPTION_MISSING, "Description missing");
        }

        BigDecimal normalizedAmount = normalizeAmount(amount);

        Currency currency = Currency.from(currencyValue)
                .orElseThrow(() -> new DomainException(ErrorCodes.INVALID_CURRENCY, "Invalid currency"));
        Direction direction = Direction.from(directionValue)
                .orElseThrow(() -> new DomainException(ErrorCodes.INVALID_DIRECTION, "Invalid direction"));

        accountMapper.findById(accountId)
                .orElseThrow(() -> new DomainException(ErrorCodes.ACCOUNT_NOT_FOUND, "Account missing"));

        // lock balance row until commit
        Balance balance = balanceMapper.findByAccountIdAndCurrencyForUpdate(accountId, currency)
                .orElseThrow(() -> new DomainException(ErrorCodes.INVALID_CURRENCY, "Invalid currency"));

        BigDecimal currentAmount = balance.getAvailableAmount();
        BigDecimal balanceAfter = direction == Direction.IN
                ? currentAmount.add(normalizedAmount)
                : currentAmount.subtract(normalizedAmount);

        if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(ErrorCodes.INSUFFICIENT_FUNDS, "Insufficient funds");
        }

        balance.setAvailableAmount(balanceAfter);
        balanceMapper.updateAmount(balance);
        eventPublisher.publish(new AccountEvent(AccountEvent.BALANCE_UPDATED, accountId, balance));

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setAccountId(accountId);
        transaction.setAmount(normalizedAmount);
        transaction.setCurrency(currency);
        transaction.setDirection(direction);
        transaction.setDescription(description.trim());
        transaction.setBalanceAfterTransaction(balanceAfter);
        transactionMapper.insert(transaction);
        eventPublisher.publish(new AccountEvent(AccountEvent.TRANSACTION_CREATED, accountId, transaction));

        return transaction;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(UUID accountId) {
        accountMapper.findById(accountId)
                .orElseThrow(() -> new DomainException(ErrorCodes.ACCOUNT_NOT_FOUND, "Invalid account"));
        return transactionMapper.findByAccountId(accountId);
    }

    private static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null) {
            throw new DomainException(ErrorCodes.INVALID_AMOUNT, "Invalid amount");
        }
        final BigDecimal normalized;
        try {
            normalized = amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new DomainException(
                    ErrorCodes.INVALID_AMOUNT,
                    "Amount must have at most 2 decimal places"
            );
        }
        if (normalized.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCodes.INVALID_AMOUNT, "Invalid amount");
        }
        return normalized;
    }
}
