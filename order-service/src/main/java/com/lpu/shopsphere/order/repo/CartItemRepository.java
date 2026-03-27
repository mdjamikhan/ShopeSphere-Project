package com.lpu.shopsphere.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lpu.shopsphere.order.domain.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {}

