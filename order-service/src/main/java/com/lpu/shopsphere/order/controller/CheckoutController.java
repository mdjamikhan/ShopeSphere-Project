package com.lpu.shopsphere.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lpu.shopsphere.order.domain.Order;
import com.lpu.shopsphere.order.dto.CheckoutRequest;
import com.lpu.shopsphere.order.dto.PaymentRequest;
import com.lpu.shopsphere.order.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@Validated
public class CheckoutController {
  private final CartService cartService;

  public CheckoutController(CartService cartService) {
    this.cartService = cartService;
  }

  @PostMapping("/checkout/start")
  public ResponseEntity<Order> start(@Valid @RequestBody CheckoutRequest request) {
    return ResponseEntity.ok(cartService.startCheckout(request));
  }

  @PostMapping({ "/checkout/payments", "/payment", "/payments" })
  public ResponseEntity<Order> pay(@Valid @RequestBody PaymentRequest request) {
    return ResponseEntity.ok(cartService.processPayment(request));
  }
}

