package com.lpu.shopsphere.catalog.messaging;

import java.io.Serializable;
import java.util.List;

/**
 * Mirror DTO of order-service's OrderPlacedEvent.
 * Only the fields catalog-service needs are included (productId, quantity).
 */
public class OrderPlacedEvent implements Serializable {

    private Long orderId;
    private String userEmail;
    private List<OrderItemDto> items;

    public OrderPlacedEvent() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }

    public static class OrderItemDto implements Serializable {
        private Long productId;
        private int quantity;

        public OrderItemDto() {}

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
