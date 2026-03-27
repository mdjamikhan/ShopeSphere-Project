package com.lpu.shopsphere.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lpu.shopsphere.order.domain.Order;
import com.lpu.shopsphere.order.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/confirmation/{orderId}")
  public ResponseEntity<Order> confirmation(@PathVariable Long orderId) {
    return ResponseEntity.ok(orderService.getConfirmation(orderId));
  }

  @GetMapping({ "/order/history", "/history" })
  public ResponseEntity<List<Order>> history() {
    return ResponseEntity.ok(orderService.history());
  }
}

