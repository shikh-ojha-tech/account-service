package com.account.messaging;

import com.account.domain.OutboxEvent;
import com.account.persistence.OutboxMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxService")
class OutboxServiceTest {

    @Mock
    private OutboxMapper outboxMapper;

    @InjectMocks
    private OutboxService outboxService;

    @Test
    void claimBatch_delegatesToMapper() {
        OutboxEvent row = new OutboxEvent();
        when(outboxMapper.findUnpublishedForUpdate(10)).thenReturn(List.of(row));

        assertThat(outboxService.claimBatch(10)).containsExactly(row);
    }

    @Test
    void markPublished_andRecordFailure_delegateToMapper() {
        UUID id = UUID.randomUUID();

        outboxService.markPublished(id);
        outboxService.recordFailure(id);

        verify(outboxMapper).markPublished(id);
        verify(outboxMapper).incrementAttempts(id);
    }
}
