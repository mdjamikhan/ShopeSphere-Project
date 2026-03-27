package com.lpu.shopsphere.order.service;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lpu.shopsphere.order.domain.Order;
import com.lpu.shopsphere.order.domain.OrderItem;
import com.lpu.shopsphere.order.domain.OrderStatus;
import com.lpu.shopsphere.order.repo.OrderRepository;
import com.lpu.shopsphere.order.messaging.OrderEventPublisher;

@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final AuthenticatedUserService authenticatedUserService;
  private final OrderEventPublisher orderEventPublisher;

  public OrderService(OrderRepository orderRepository,
                      AuthenticatedUserService authenticatedUserService,
                      OrderEventPublisher orderEventPublisher) {
    this.orderRepository = orderRepository;
    this.authenticatedUserService = authenticatedUserService;
    this.orderEventPublisher = orderEventPublisher;
  }

  @Transactional(readOnly = true)
  public Order getConfirmation(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    String email = authenticatedUserService.getCurrentEmail();
    if (order.getUserEmail() == null || !order.getUserEmail().equals(email)) {
      throw new IllegalArgumentException("Order not owned by current user");
    }
    return order;
  }

  @Transactional(readOnly = true)
  public List<Order> history() {
    String email = authenticatedUserService.getCurrentEmail();
    return orderRepository.findByUserEmail(email);
  }

  // Admin endpoints
  @Transactional(readOnly = true)
  public List<Order> listOrders() {
    return orderRepository.findAll();
  }

  @Transactional
  public Order updateStatus(Long orderId, OrderStatus status) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    String oldStatus = order.getStatus() != null ? order.getStatus().name() : "UNKNOWN";
    order.setStatus(status);
    Order saved = orderRepository.save(order);
    // Notify downstream services of the status change asynchronously.
    orderEventPublisher.publishOrderStatusChanged(saved, oldStatus);
    return saved;
  }

  @Transactional(readOnly = true)
  public Map<String, Object> reports() {
    EnumMap<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
    for (OrderStatus st : OrderStatus.values()) {
      counts.put(st, orderRepository.findByStatus(st).stream().count());
    }

    double revenue = orderRepository.findByStatus(OrderStatus.DELIVERED).stream()
        .flatMap(o -> o.getItems().stream())
        .mapToDouble((OrderItem oi) -> oi.getUnitPrice() * oi.getQuantity())
        .sum();

    return Map.of("orderStatusCounts", counts, "deliveredRevenue", revenue);
  }
}

