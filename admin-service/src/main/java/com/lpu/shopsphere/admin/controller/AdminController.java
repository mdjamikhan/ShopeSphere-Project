package com.lpu.shopsphere.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lpu.shopsphere.admin.client.OrderClient;
import com.lpu.shopsphere.admin.service.AuthenticatedUserService;

@RestController
@RequestMapping("/admin")
public class AdminController {
  private final OrderClient orderClient;
  private final AuthenticatedUserService authenticatedUserService;

  public AdminController(OrderClient orderClient, AuthenticatedUserService authenticatedUserService) {
    this.orderClient = orderClient;
    this.authenticatedUserService = authenticatedUserService;
  }

  @GetMapping("/orders")
  public ResponseEntity<Object> getAllOrders() {
    String email = authenticatedUserService.getCurrentEmail();
    String role = authenticatedUserService.getCurrentRole().name();
    Object orders = orderClient.getAllOrders(email, role);
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/dashboard")
  public ResponseEntity<Object> getDashboard() {
    String email = authenticatedUserService.getCurrentEmail();
    String role = authenticatedUserService.getCurrentRole().name();
    Object dashboard = orderClient.getDashboard(email, role);
    return ResponseEntity.ok(dashboard);
  }

  @GetMapping("/reports")
  public ResponseEntity<Object> getReports() {
    String email = authenticatedUserService.getCurrentEmail();
    String role = authenticatedUserService.getCurrentRole().name();
    Object reports = orderClient.getReports(email, role);
    return ResponseEntity.ok(reports);
  }
}