package com.lpu.shopsphere.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lpu.shopsphere.order.domain.Cart;
import com.lpu.shopsphere.order.dto.CartItemUpdateRequest;
import com.lpu.shopsphere.order.service.CartService;

@RestController
@RequestMapping("/orders")
public class CartController {
  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @PostMapping("/cart/add/{productId}")
  public ResponseEntity<Cart> addToCart(@PathVariable Long productId, @RequestParam(defaultValue = "1") int quantity) {
    return ResponseEntity.ok(cartService.addToCart(productId, quantity));
  }

  @GetMapping("/cart")
  public ResponseEntity<Cart> getCart() {
    return ResponseEntity.ok(cartService.getMyCart());
  }

  @PutMapping("/cart/items/{itemId}")
  public ResponseEntity<Cart> updateItem(@PathVariable Long itemId, @RequestBody CartItemUpdateRequest request) {
    return ResponseEntity.ok(cartService.updateItem(itemId, request));
  }

  @DeleteMapping("/cart/items/{itemId}")
  public ResponseEntity<String> remove(@PathVariable Long itemId) {
    return ResponseEntity.ok(cartService.removeItem(itemId));
  }
}

