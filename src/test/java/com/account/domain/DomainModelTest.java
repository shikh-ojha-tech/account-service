package com.account.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Domain models")
class DomainModelTest {

    @Test
    void account_gettersAndSetters() {
        Account account = new Account();
        UUID id = UUID.randomUUID();
        account.setAccountId(id);
        account.setCustomerId("c1");
        account.setCountry("EE");

        assertThat(account.getAccountId()).isEqualTo(id);
        assertThat(account.getCustomerId()).isEqualTo("c1");
        assertThat(account.getCountry()).isEqualTo("EE");
    }

    @Test
    void balance_gettersAndSetters() {
        Balance balance = new Balance();
        UUID balanceId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        balance.setBalanceId(balanceId);
        balance.setAccountId(accountId);
        balance.setCurrency(Currency.GBP);
        balance.setAvailableAmount(new BigDecimal("3.25"));

        assertThat(balance.getBalanceId()).isEqualTo(balanceId);
        assertThat(balance.getAccountId()).isEqualTo(accountId);
        assertThat(balance.getCurrency()).isEqualTo(Currency.GBP);
        assertThat(balance.getAvailableAmount()).isEqualByComparingTo("3.25");
    }

    @Test
    void transaction_gettersAndSetters() {
        Transaction transaction = new Transaction();
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        transaction.setTransactionId(transactionId);
        transaction.setAccountId(accountId);
        transaction.setAmount(new BigDecimal("1.00"));
        transaction.setCurrency(Currency.SEK);
        transaction.setDirection(Direction.OUT);
        transaction.setDescription("x");
        transaction.setBalanceAfterTransaction(new BigDecimal("9.00"));

        assertThat(transaction.getTransactionId()).isEqualTo(transactionId);
        assertThat(transaction.getAccountId()).isEqualTo(accountId);
        assertThat(transaction.getAmount()).isEqualByComparingTo("1.00");
        assertThat(transaction.getCurrency()).isEqualTo(Currency.SEK);
        assertThat(transaction.getDirection()).isEqualTo(Direction.OUT);
        assertThat(transaction.getDescription()).isEqualTo("x");
        assertThat(transaction.getBalanceAfterTransaction()).isEqualByComparingTo("9.00");
    }
}
