package com.account.messaging;

import java.time.Instant;
import java.util.UUID;

public class AccountEvent {

    public static final String ACCOUNT_CREATED = "ACCOUNT_CREATED";

    public static final String BALANCE_CREATED = "BALANCE_CREATED";

    public static final String BALANCE_UPDATED = "BALANCE_UPDATED";

    public static final String TRANSACTION_CREATED = "TRANSACTION_CREATED";

    private String eventType;

    private UUID accountId;

    private Instant occurredAt;

    private Object payload;

    public AccountEvent() {
    }

    public AccountEvent(String eventType, UUID accountId, Object payload) {
        this.eventType = eventType;
        this.accountId = accountId;
        this.occurredAt = Instant.now();
        this.payload = payload;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
