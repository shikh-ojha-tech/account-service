package com.account.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("RabbitMqConfig")
class RabbitMqConfigTest {

    private final RabbitMqConfig config = new RabbitMqConfig();

    @Test
    void accountExchange_isDurableTopic() {
        TopicExchange exchange = config.accountExchange();
        assertThat(exchange.getName()).isEqualTo(RabbitMqConfig.ACCOUNT_EXCHANGE);
        assertThat(exchange.isDurable()).isTrue();
    }

    @Test
    void rabbitTemplate_usesJacksonConverter() {
        MessageConverter converter = config.jacksonMessageConverter();
        assertThat(converter).isInstanceOf(Jackson2JsonMessageConverter.class);

        RabbitTemplate template = config.rabbitTemplate(mock(ConnectionFactory.class), converter);
        assertThat(template.getMessageConverter()).isSameAs(converter);
    }
}
