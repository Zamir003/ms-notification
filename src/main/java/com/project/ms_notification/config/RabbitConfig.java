package com.project.ms_notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitConfig {

    public static final String PAYMENTS_EXCHANGE = "payments.events.x";
    public static final String NOTIFICATIONS_EXCHANGE = "notifications.events.x";

    public static final String PAYMENTS_QUEUE = "payments.events.q";
    public static final String PAYMENTS_DLX = "payments.events.dlx";
    public static final String PAYMENTS_DLQ = "payments.events.dlq";

    public static final String BIRTHDAY_QUEUE = "birthday.events.q";
    public static final String NOTIFICATIONS_DLX = "notifications.events.dlx";
    public static final String BIRTHDAY_DLQ = "birthday.events.dlq";

    @Bean
    public TopicExchange paymentsExchange() {
        return ExchangeBuilder.topicExchange(PAYMENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange notificationsExchange() {
        return ExchangeBuilder.topicExchange(NOTIFICATIONS_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange paymentsDlx() {
        return ExchangeBuilder.topicExchange(PAYMENTS_DLX).durable(true).build();
    }

    @Bean
    public TopicExchange notificationsDlx() {
        return ExchangeBuilder.topicExchange(NOTIFICATIONS_DLX).durable(true).build();
    }

    @Bean
    public Queue paymentsQueue() {
        return QueueBuilder.durable(PAYMENTS_QUEUE)
                .withArguments(Map.of(
                        "x-dead-letter-exchange", PAYMENTS_DLX,
                        "x-dead-letter-routing-key", "dlq.payments"
                ))
                .build();
    }

    @Bean
    public Queue paymentsDlq() {
        return QueueBuilder.durable(PAYMENTS_DLQ).build();
    }

    @Bean
    public Binding bindPaymentsQueue(TopicExchange paymentsExchange, Queue paymentsQueue) {
        return BindingBuilder.bind(paymentsQueue).to(paymentsExchange).with("payment.*");
    }

    @Bean
    public Binding bindPaymentsDlq(TopicExchange paymentsDlx, Queue paymentsDlq) {
        return BindingBuilder.bind(paymentsDlq).to(paymentsDlx).with("dlq.payments");
    }

    @Bean
    public Queue birthdayQueue() {
        return QueueBuilder.durable(BIRTHDAY_QUEUE)
                .withArguments(Map.of(
                        "x-dead-letter-exchange", NOTIFICATIONS_DLX,
                        "x-dead-letter-routing-key", "dlq.birthday"
                ))
                .build();
    }

    @Bean
    public Queue birthdayDlq() {
        return QueueBuilder.durable(BIRTHDAY_DLQ).build();
    }

    @Bean
    public Binding bindBirthdayQueue(TopicExchange notificationsExchange, Queue birthdayQueue) {
        return BindingBuilder.bind(birthdayQueue).to(notificationsExchange).with("birthday.*");
    }

    @Bean
    public Binding bindBirthdayDlq(TopicExchange notificationsDlx, Queue birthdayDlq) {
        return BindingBuilder.bind(birthdayDlq).to(notificationsDlx).with("dlq.birthday");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    //do not requeue on exception - if message is rejected - go to dlq

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf,
                                                                               Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(converter);
        f.setDefaultRequeueRejected(false);
        return f;
    }
}