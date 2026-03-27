package com.lpu.shopsphere.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ─── Exchange names ───────────────────────────────────────────────────────
    public static final String ORDER_EXCHANGE = "order.exchange";

    // ─── Routing keys ─────────────────────────────────────────────────────────
    public static final String ORDER_PLACED_ROUTING_KEY         = "order.placed";
    public static final String ORDER_STATUS_CHANGED_ROUTING_KEY = "order.status.changed";

    // ─── Queue names ──────────────────────────────────────────────────────────
    public static final String ORDER_PLACED_QUEUE         = "order.placed.queue";
    public static final String ORDER_STATUS_CHANGED_QUEUE = "order.status.changed.queue";

    // ─── Exchange ─────────────────────────────────────────────────────────────
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    // ─── Queues ───────────────────────────────────────────────────────────────
    @Bean
    public Queue orderPlacedQueue() {
        return new Queue(ORDER_PLACED_QUEUE, true);
    }

    @Bean
    public Queue orderStatusChangedQueue() {
        return new Queue(ORDER_STATUS_CHANGED_QUEUE, true);
    }

    // ─── Bindings ─────────────────────────────────────────────────────────────
    @Bean
    public Binding orderPlacedBinding(Queue orderPlacedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderPlacedQueue)
                .to(orderExchange)
                .with(ORDER_PLACED_ROUTING_KEY);
    }

    @Bean
    public Binding orderStatusChangedBinding(Queue orderStatusChangedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderStatusChangedQueue)
                .to(orderExchange)
                .with(ORDER_STATUS_CHANGED_ROUTING_KEY);
    }

    // ─── JSON message converter ───────────────────────────────────────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
