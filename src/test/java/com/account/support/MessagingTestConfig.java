package com.account.support;

import com.account.config.RabbitMqConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MessagingTestConfig {

    public static final String ACCOUNT_EVENTS_QUEUE = "account.events.test";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue accountEventsTestQueue() {
        return new Queue(ACCOUNT_EVENTS_QUEUE, false, false, true);
    }

    @Bean
    public Binding accountEventsTestBinding(Queue accountEventsTestQueue, TopicExchange accountExchange) {
        return BindingBuilder
                .bind(accountEventsTestQueue)
                .to(accountExchange)
                .with(RabbitMqConfig.ACCOUNT_ROUTING_KEY);
    }
}
