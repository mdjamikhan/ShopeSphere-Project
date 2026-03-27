package com.lpu.shopsphere.admin.dto;

import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequest {
  public enum OrderStatus {
    DRAFT,
    CHECKOUT,
    PAID,
    PACKED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED
  }

  @NotNull
  private OrderStatus status;

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }
}

