package com.account.messaging;

import com.account.domain.OutboxEvent;
import com.account.persistence.OutboxMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OutboxService {

    private final OutboxMapper outboxMapper;

    public OutboxService(OutboxMapper outboxMapper) {
        this.outboxMapper = outboxMapper;
    }

    @Transactional
    public List<OutboxEvent> claimBatch(int limit) {
        return outboxMapper.findUnpublishedForUpdate(limit);
    }

    @Transactional
    public void markPublished(UUID id) {
        outboxMapper.markPublished(id);
    }

    @Transactional
    public void recordFailure(UUID id) {
        outboxMapper.incrementAttempts(id);
    }
}
