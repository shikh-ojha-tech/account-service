package com.account.messaging;

import com.account.config.RabbitMqConfig;
import com.account.domain.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxRelay")
class OutboxRelayTest {

    @Mock
    private OutboxService outboxService;
    @Mock
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void publishPending_sendsClaimedEventsAndMarksPublished() throws Exception {
        UUID id = UUID.randomUUID();
        AccountEvent event = new AccountEvent(AccountEvent.BALANCE_UPDATED, UUID.randomUUID(), "x");
        OutboxEvent row = new OutboxEvent();
        row.setId(id);
        row.setPayload(objectMapper.writeValueAsString(event));

        when(outboxService.claimBatch(50)).thenReturn(List.of(row));

        OutboxRelay relay = new OutboxRelay(outboxService, rabbitTemplate, objectMapper, 50);
        relay.publishPending();

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMqConfig.ACCOUNT_EXCHANGE),
                eq(RabbitMqConfig.ACCOUNT_ROUTING_KEY),
                any(AccountEvent.class)
        );
        verify(outboxService).markPublished(id);
        verify(outboxService, never()).recordFailure(any());
    }

    @Test
    void publishPending_recordsFailureWhenRabbitSendFails() throws Exception {
        UUID id = UUID.randomUUID();
        AccountEvent event = new AccountEvent(AccountEvent.TRANSACTION_CREATED, UUID.randomUUID(), "x");
        OutboxEvent row = new OutboxEvent();
        row.setId(id);
        row.setPayload(objectMapper.writeValueAsString(event));

        when(outboxService.claimBatch(50)).thenReturn(List.of(row));
        doThrow(new RuntimeException("broker down"))
                .when(rabbitTemplate)
                .convertAndSend(any(String.class), any(String.class), any(AccountEvent.class));

        OutboxRelay relay = new OutboxRelay(outboxService, rabbitTemplate, objectMapper, 50);
        relay.publishPending();

        verify(outboxService).recordFailure(id);
        verify(outboxService, never()).markPublished(any());
    }
}
