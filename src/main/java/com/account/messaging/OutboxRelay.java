package com.account.messaging;

import com.account.config.RabbitMqConfig;
import com.account.domain.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    private final OutboxService outboxService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final int batchSize;

    public OutboxRelay(OutboxService outboxService,
                       RabbitTemplate rabbitTemplate,
                       ObjectMapper objectMapper,
                       @Value("${app.outbox.batch-size:50}") int batchSize) {
        this.outboxService = outboxService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:250}")
    public void publishPending() {
        List<OutboxEvent> batch = outboxService.claimBatch(batchSize);
        for (OutboxEvent row : batch) {
            try {
                AccountEvent event = objectMapper.readValue(row.getPayload(), AccountEvent.class);
                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.ACCOUNT_EXCHANGE,
                        RabbitMqConfig.ACCOUNT_ROUTING_KEY,
                        event
                );
                outboxService.markPublished(row.getId());
            } catch (Exception ex) {
                log.warn("Failed to publish outbox event {}", row.getId(), ex);
                outboxService.recordFailure(row.getId());
            }
        }
    }
}
