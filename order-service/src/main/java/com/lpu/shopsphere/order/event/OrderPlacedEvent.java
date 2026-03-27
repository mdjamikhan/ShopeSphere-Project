package com.lpu.shopsphere.order.event;

import java.io.Serializable;
import java.util.List;

/**
 * Event published to RabbitMQ when a new order is successfully created (checkout started).
 * Consumed by catalog-service to decrement product stock.
 */
public class OrderPlacedEvent implements Serializable {

    private Long orderId;
    private String userEmail;
    private List<OrderItemDto> items;

    public OrderPlacedEvent() {}

    public OrderPlacedEvent(Long orderId, String userEmail, List<OrderItemDto> items) {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.items = items;
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }

    // ─── Nested DTO ───────────────────────────────────────────────────────────

    public static class OrderItemDto implements Serializable {
        private Long productId;
        private int quantity;

        public OrderItemDto() {}

        public OrderItemDto(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
