package com.account.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.account.messaging.AccountEvent;
import com.account.support.AbstractIntegrationTest;
import com.account.support.MessagingTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class RabbitMqPublishIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void purgeQueue() {
        rabbitAdmin.purgeQueue(MessagingTestConfig.ACCOUNT_EVENTS_QUEUE, false);
    }

    @Test
    void createAccount_publishesInsertEvents() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "mq-customer-1",
                                  "country": "EE",
                                  "currencies": ["EUR", "GBP"]
                                }
                                """))
                .andExpect(status().isCreated());

        List<String> eventTypes = drainEventTypes(3);
        assertThat(eventTypes).containsExactlyInAnyOrder(
                AccountEvent.ACCOUNT_CREATED,
                AccountEvent.BALANCE_CREATED,
                AccountEvent.BALANCE_CREATED
        );
    }

    @Test
    void createTransaction_publishesUpdateAndInsertEvents() throws Exception {
        MvcResult created = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "mq-customer-2",
                                  "country": "EE",
                                  "currencies": ["EUR"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        drainEventTypes(2);

        String accountId = JsonPath.read(created.getResponse().getContentAsString(), "$.accountId");

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "%s",
                                  "amount": 25.00,
                                  "currency": "EUR",
                                  "direction": "IN",
                                  "description": "Deposit"
                                }
                                """.formatted(accountId)))
                .andExpect(status().isCreated());

        List<String> eventTypes = drainEventTypes(2);
        assertThat(eventTypes).containsExactlyInAnyOrder(
                AccountEvent.BALANCE_UPDATED,
                AccountEvent.TRANSACTION_CREATED
        );
    }

    private List<String> drainEventTypes(int expectedCount) throws Exception {
        List<String> eventTypes = new ArrayList<>();
        for (int i = 0; i < expectedCount; i++) {
            Message message = rabbitTemplate.receive(MessagingTestConfig.ACCOUNT_EVENTS_QUEUE, 5000);
            assertThat(message).as("expected event #%s", i + 1).isNotNull();
            JsonNode node = objectMapper.readTree(new String(message.getBody(), StandardCharsets.UTF_8));
            eventTypes.add(node.get("eventType").asText());
        }
        return eventTypes;
    }
}
