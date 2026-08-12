package com.account.service;

import com.account.domain.Account;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService")
class TransactionServiceTest {

    @Mock
    private AccountMapper accountMapper;
    @Mock
    private BalanceMapper balanceMapper;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private AccountEventPublisher eventPublisher;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_inIncreasesBalanceAndPublishesEvents() {
        UUID accountId = UUID.randomUUID();
        Balance balance = lockedBalance(accountId, "40.00");
        stubExistingAccount(accountId, balance);

        Transaction result = transactionService.createTransaction(
                accountId, new BigDecimal("10.5"), "EUR", "IN", " Deposit "
        );

        assertThat(result.getDirection()).isEqualTo(Direction.IN);
        assertThat(result.getAmount()).isEqualByComparingTo("10.50");
        assertThat(result.getDescription()).isEqualTo("Deposit");
        assertThat(result.getBalanceAfterTransaction()).isEqualByComparingTo("50.50");
        assertThat(balance.getAvailableAmount()).isEqualByComparingTo("50.50");

        verify(balanceMapper).updateAmount(balance);
        verify(transactionMapper).insert(any(Transaction.class));

        ArgumentCaptor<AccountEvent> events = ArgumentCaptor.forClass(AccountEvent.class);
        verify(eventPublisher, times(2)).publish(events.capture());
        assertThat(events.getAllValues())
                .extracting(AccountEvent::getEventType)
                .containsExactly(AccountEvent.BALANCE_UPDATED, AccountEvent.TRANSACTION_CREATED);
    }

    @Test
    void createTransaction_outDecreasesBalance() {
        UUID accountId = UUID.randomUUID();
        Balance balance = lockedBalance(accountId, "40.00");
        stubExistingAccount(accountId, balance);

        Transaction result = transactionService.createTransaction(
                accountId, new BigDecimal("15.00"), "EUR", "OUT", "Purchase"
        );

        assertThat(result.getDirection()).isEqualTo(Direction.OUT);
        assertThat(result.getBalanceAfterTransaction()).isEqualByComparingTo("25.00");
    }

    @Test
    void createTransaction_rejectsNullAccountId() {
        assertThatThrownBy(() -> transactionService.createTransaction(
                null, new BigDecimal("1.00"), "EUR", "IN", "x"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.ACCOUNT_NOT_FOUND);
    }

    @Test
    void createTransaction_rejectsMissingDescription() {
        UUID accountId = UUID.randomUUID();
        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("1.00"), "EUR", "IN", " "
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.DESCRIPTION_MISSING);
    }

    @Test
    void createTransaction_rejectsInvalidAmount() {
        UUID accountId = UUID.randomUUID();
        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("-1.00"), "EUR", "IN", "x"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_AMOUNT);
    }

    @Test
    void createTransaction_rejectsAmountWithUnsupportedScale() {
        UUID accountId = UUID.randomUUID();
        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("0.001"), "EUR", "IN", "x"
        ))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    DomainException domainException = (DomainException) ex;
                    assertThat(domainException.getErrorCode()).isEqualTo(ErrorCodes.INVALID_AMOUNT);
                    assertThat(domainException.getMessage()).contains("2 decimal places");
                });
        verify(balanceMapper, never()).findByAccountIdAndCurrencyForUpdate(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void createTransaction_rejectsAmountThatWouldHaveBeenSilentlyRounded() {
        UUID accountId = UUID.randomUUID();
        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("10.005"), "EUR", "IN", "x"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_AMOUNT);
    }

    @Test
    void createTransaction_rejectsAmountThatWouldRoundToZero() {
        UUID accountId = UUID.randomUUID();
        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("0.004"), "EUR", "IN", "x"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_AMOUNT);
    }

    @Test
    void createTransaction_rejectsInvalidCurrency() {
        UUID accountId = UUID.randomUUID();
        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("1.00"), "JPY", "IN", "x"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_CURRENCY);
    }

    @Test
    void createTransaction_rejectsInvalidDirection() {
        UUID accountId = UUID.randomUUID();
        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("1.00"), "EUR", "SIDE", "x"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_DIRECTION);
    }

    @Test
    void createTransaction_rejectsMissingAccount() {
        UUID accountId = UUID.randomUUID();
        when(accountMapper.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("1.00"), "EUR", "IN", "x"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.ACCOUNT_NOT_FOUND);
        verify(balanceMapper, never()).findByAccountIdAndCurrencyForUpdate(any(), any());
    }

    @Test
    void createTransaction_rejectsCurrencyNotOnAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        when(accountMapper.findById(accountId)).thenReturn(Optional.of(account));
        when(balanceMapper.findByAccountIdAndCurrencyForUpdate(accountId, Currency.USD))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("1.00"), "USD", "IN", "x"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_CURRENCY);
    }

    @Test
    void createTransaction_rejectsInsufficientFunds() {
        UUID accountId = UUID.randomUUID();
        Balance balance = lockedBalance(accountId, "5.00");
        stubExistingAccount(accountId, balance);

        assertThatThrownBy(() -> transactionService.createTransaction(
                accountId, new BigDecimal("10.00"), "EUR", "OUT", "Too much"
        )).extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INSUFFICIENT_FUNDS);
        verify(transactionMapper, never()).insert(any());
    }

    @Test
    void getTransactions_returnsHistory() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID());

        when(accountMapper.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionMapper.findByAccountId(accountId)).thenReturn(List.of(transaction));

        assertThat(transactionService.getTransactions(accountId)).containsExactly(transaction);
    }

    @Test
    void getTransactions_rejectsUnknownAccount() {
        UUID accountId = UUID.randomUUID();
        when(accountMapper.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactions(accountId))
                .extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.ACCOUNT_NOT_FOUND);
    }

    private void stubExistingAccount(UUID accountId, Balance balance) {
        Account account = new Account();
        account.setAccountId(accountId);
        when(accountMapper.findById(accountId)).thenReturn(Optional.of(account));
        when(balanceMapper.findByAccountIdAndCurrencyForUpdate(eq(accountId), eq(Currency.EUR)))
                .thenReturn(Optional.of(balance));
    }

    private static Balance lockedBalance(UUID accountId, String amount) {
        Balance balance = new Balance();
        balance.setBalanceId(UUID.randomUUID());
        balance.setAccountId(accountId);
        balance.setCurrency(Currency.EUR);
        balance.setAvailableAmount(new BigDecimal(amount));
        return balance;
    }
}
