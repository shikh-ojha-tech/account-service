package com.account.messaging;

import com.account.domain.OutboxEvent;
import com.account.persistence.OutboxMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountEventPublisher {

    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    public AccountEventPublisher(OutboxMapper outboxMapper, ObjectMapper objectMapper) {
        this.outboxMapper = outboxMapper;
        this.objectMapper = objectMapper;
    }

    public void publish(AccountEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(UUID.randomUUID());
        outboxEvent.setPayload(writePayload(event));
        outboxMapper.insert(outboxEvent);
    }

    private String writePayload(AccountEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize account event", ex);
        }
    }
}
