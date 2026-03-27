package com.lpu.shopsphere.order.messaging;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.lpu.shopsphere.order.config.RabbitMQConfig;
import com.lpu.shopsphere.order.domain.Order;
import com.lpu.shopsphere.order.event.OrderPlacedEvent;
import com.lpu.shopsphere.order.event.OrderStatusChangedEvent;

/**
 * Publishes order-related domain events to RabbitMQ.
 */
@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes an {@link OrderPlacedEvent} after a successful checkout.
     * Consumed by catalog-service to decrement stock.
     */
    public void publishOrderPlaced(Order order) {
        List<OrderPlacedEvent.OrderItemDto> itemDtos = order.getItems().stream()
                .map(oi -> new OrderPlacedEvent.OrderItemDto(oi.getProductId(), oi.getQuantity()))
                .collect(Collectors.toList());

        OrderPlacedEvent event = new OrderPlacedEvent(order.getId(), order.getUserEmail(), itemDtos);

        log.info("[RabbitMQ] Publishing OrderPlacedEvent for orderId={}, items={}", order.getId(), itemDtos.size());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_PLACED_ROUTING_KEY,
                event);
    }

    /**
     * Publishes an {@link OrderStatusChangedEvent} when an admin updates an order's status.
     */
    public void publishOrderStatusChanged(Order order, String oldStatus) {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order.getId(),
                order.getUserEmail(),
                oldStatus,
                order.getStatus().name());

        log.info("[RabbitMQ] Publishing OrderStatusChangedEvent for orderId={}, {} -> {}",
                order.getId(), oldStatus, order.getStatus().name());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_CHANGED_ROUTING_KEY,
                event);
    }
}
