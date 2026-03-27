package com.lpu.shopsphere.order.event;

import java.io.Serializable;

/**
 * Event published to RabbitMQ when an order's status is changed by an admin.
 * Can be consumed by a future notification service to alert the customer.
 */
public class OrderStatusChangedEvent implements Serializable {

    private Long orderId;
    private String userEmail;
    private String oldStatus;
    private String newStatus;

    public OrderStatusChangedEvent() {}

    public OrderStatusChangedEvent(Long orderId, String userEmail, String oldStatus, String newStatus) {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}
