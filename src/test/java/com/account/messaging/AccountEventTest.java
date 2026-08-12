package com.account.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccountEvent")
class AccountEventTest {

    @Test
    void constructor_setsRequiredFields() {
        UUID accountId = UUID.randomUUID();
        AccountEvent event = new AccountEvent(AccountEvent.BALANCE_UPDATED, accountId, "body");

        assertThat(event.getEventType()).isEqualTo(AccountEvent.BALANCE_UPDATED);
        assertThat(event.getAccountId()).isEqualTo(accountId);
        assertThat(event.getPayload()).isEqualTo("body");
        assertThat(event.getOccurredAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void setters_updateFields() {
        AccountEvent event = new AccountEvent();
        UUID accountId = UUID.randomUUID();
        Instant now = Instant.parse("2024-01-01T00:00:00Z");

        event.setEventType(AccountEvent.TRANSACTION_CREATED);
        event.setAccountId(accountId);
        event.setOccurredAt(now);
        event.setPayload(42);

        assertThat(event.getEventType()).isEqualTo(AccountEvent.TRANSACTION_CREATED);
        assertThat(event.getAccountId()).isEqualTo(accountId);
        assertThat(event.getOccurredAt()).isEqualTo(now);
        assertThat(event.getPayload()).isEqualTo(42);
    }
}
