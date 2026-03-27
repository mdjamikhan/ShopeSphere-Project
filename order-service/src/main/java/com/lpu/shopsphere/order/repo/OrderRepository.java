package com.lpu.shopsphere.order.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lpu.shopsphere.order.domain.Order;
import com.lpu.shopsphere.order.domain.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUserEmail(String userEmail);
  List<Order> findByStatus(OrderStatus status);
}

