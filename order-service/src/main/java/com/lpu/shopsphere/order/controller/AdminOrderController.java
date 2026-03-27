package com.lpu.shopsphere.order.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lpu.shopsphere.order.domain.Order;
import com.lpu.shopsphere.order.dto.OrderStatusUpdateRequest;
import com.lpu.shopsphere.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/orders/admin")
public class AdminOrderController {
  private final OrderService orderService;

  public AdminOrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/dashboard")
  public ResponseEntity<Map<String, Object>> dashboard() {
    return ResponseEntity.ok(orderService.reports());
  }

  @GetMapping("/orders")
  public ResponseEntity<List<Order>> listOrders() {
    return ResponseEntity.ok(orderService.listOrders());
  }

  @PutMapping("/orders/{id}/status")
  public ResponseEntity<Order> updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
    return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
  }

  @GetMapping("/reports")
  public ResponseEntity<Map<String, Object>> reports() {
    return ResponseEntity.ok(orderService.reports());
  }
}

