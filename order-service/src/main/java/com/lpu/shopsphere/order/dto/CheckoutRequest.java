package com.lpu.shopsphere.order.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckoutRequest {
  @NotBlank
  private String addressLine;

  @NotBlank
  private String deliveryOption;

  @NotBlank
  private String paymentMethod;

  public String getAddressLine() {
    return addressLine;
  }

  public void setAddressLine(String addressLine) {
    this.addressLine = addressLine;
  }

  public String getDeliveryOption() {
    return deliveryOption;
  }

  public void setDeliveryOption(String deliveryOption) {
    this.deliveryOption = deliveryOption;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }
}

