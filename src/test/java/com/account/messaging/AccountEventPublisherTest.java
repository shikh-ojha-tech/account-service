package com.account.messaging;

import com.account.domain.OutboxEvent;
import com.account.persistence.OutboxMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountEventPublisher")
class AccountEventPublisherTest {

    @Mock
    private OutboxMapper outboxMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private AccountEventPublisher publisher;

    @Test
    void publish_writesEventToOutbox() throws Exception {
        AccountEvent event = new AccountEvent(
                AccountEvent.ACCOUNT_CREATED,
                UUID.randomUUID(),
                "payload"
        );

        publisher.publish(event);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxMapper).insert(captor.capture());

        OutboxEvent stored = captor.getValue();
        assertThat(stored.getId()).isNotNull();
        AccountEvent storedEvent = objectMapper.readValue(stored.getPayload(), AccountEvent.class);
        assertThat(storedEvent.getEventType()).isEqualTo(AccountEvent.ACCOUNT_CREATED);
        assertThat(storedEvent.getAccountId()).isEqualTo(event.getAccountId());
        assertThat(storedEvent.getOccurredAt()).isNotNull();
    }
}
