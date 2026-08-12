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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService")
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;
    @Mock
    private BalanceMapper balanceMapper;
    @Mock
    private AccountEventPublisher eventPublisher;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_persistsAccountAndBalancesAndPublishesEvents() {
        AccountDetails details = accountService.createAccount("cust-1", "ee", List.of("EUR", "USD", "EUR"));

        assertThat(details.getAccount().getCustomerId()).isEqualTo("cust-1");
        assertThat(details.getAccount().getCountry()).isEqualTo("EE");
        assertThat(details.getBalances()).hasSize(2);
        assertThat(details.getBalances())
                .extracting(Balance::getCurrency)
                .containsExactly(Currency.EUR, Currency.USD);
        assertThat(details.getBalances())
                .allMatch(balance -> balance.getAvailableAmount().compareTo(BigDecimal.ZERO) == 0);

        verify(accountMapper).insert(any(Account.class));
        verify(balanceMapper, times(2)).insert(any(Balance.class));

        ArgumentCaptor<AccountEvent> eventCaptor = ArgumentCaptor.forClass(AccountEvent.class);
        verify(eventPublisher, times(3)).publish(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues())
                .extracting(AccountEvent::getEventType)
                .containsExactly(
                        AccountEvent.ACCOUNT_CREATED,
                        AccountEvent.BALANCE_CREATED,
                        AccountEvent.BALANCE_CREATED
                );
    }

    @Test
    void createAccount_rejectsBlankCustomerId() {
        assertThatThrownBy(() -> accountService.createAccount(" ", "EE", List.of("EUR")))
                .isInstanceOf(DomainException.class)
                .extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_REQUEST);
        verify(accountMapper, never()).insert(any());
    }

    @Test
    void createAccount_rejectsBlankCountry() {
        assertThatThrownBy(() -> accountService.createAccount("cust", "", List.of("EUR")))
                .isInstanceOf(DomainException.class)
                .extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_REQUEST);
    }

    @Test
    void createAccount_rejectsInvalidCurrency() {
        assertThatThrownBy(() -> accountService.createAccount("cust", "EE", List.of("JPY")))
                .isInstanceOf(DomainException.class)
                .extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_CURRENCY);
    }

    @Test
    void createAccount_rejectsEmptyCurrencyList() {
        assertThatThrownBy(() -> accountService.createAccount("cust", "EE", List.of()))
                .isInstanceOf(DomainException.class)
                .extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.INVALID_CURRENCY);
    }

    @Test
    void getAccount_returnsAccountWithBalances() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        account.setAccountId(accountId);
        account.setCustomerId("cust");
        account.setCountry("EE");

        Balance balance = new Balance();
        balance.setAccountId(accountId);
        balance.setCurrency(Currency.EUR);
        balance.setAvailableAmount(new BigDecimal("10.00"));

        when(accountMapper.findById(accountId)).thenReturn(Optional.of(account));
        when(balanceMapper.findByAccountId(accountId)).thenReturn(List.of(balance));

        AccountDetails details = accountService.getAccount(accountId);

        assertThat(details.getAccount()).isSameAs(account);
        assertThat(details.getBalances()).containsExactly(balance);
    }

    @Test
    void getAccount_throwsWhenMissing() {
        UUID accountId = UUID.randomUUID();
        when(accountMapper.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount(accountId))
                .isInstanceOf(DomainException.class)
                .extracting(ex -> ((DomainException) ex).getErrorCode())
                .isEqualTo(ErrorCodes.ACCOUNT_NOT_FOUND);
    }
}
