package com.lpu.shopsphere.order.dto;

import jakarta.validation.constraints.Min;

public class CartItemUpdateRequest {
  @Min(1)
  private int quantity;

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}

