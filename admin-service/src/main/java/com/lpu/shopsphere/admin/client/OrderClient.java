package com.lpu.shopsphere.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service")
public interface OrderClient {

  @GetMapping("/orders/{orderId}")
  Object getOrder(
      @PathVariable Long orderId,
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );

  @DeleteMapping("/orders/{orderId}")
  void deleteOrder(
      @PathVariable Long orderId,
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );

  @GetMapping("/orders/admin/orders")
  Object getAllOrders(
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );

  @GetMapping("/orders/admin/dashboard")
  Object getDashboard(
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );

  @GetMapping("/orders/admin/reports")
  Object getReports(
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );
}
