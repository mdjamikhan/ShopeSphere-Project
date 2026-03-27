package com.lpu.shopsphere.order.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lpu.shopsphere.order.domain.Cart;
import com.lpu.shopsphere.order.domain.CartItem;
import com.lpu.shopsphere.order.domain.Order;
import com.lpu.shopsphere.order.domain.OrderItem;
import com.lpu.shopsphere.order.dto.CatalogProductDto;
import com.lpu.shopsphere.order.dto.CartItemUpdateRequest;
import com.lpu.shopsphere.order.dto.CheckoutRequest;
import com.lpu.shopsphere.order.dto.PaymentRequest;
import com.lpu.shopsphere.order.domain.OrderStatus;
import com.lpu.shopsphere.order.repo.CartItemRepository;
import com.lpu.shopsphere.order.repo.CartRepository;
import com.lpu.shopsphere.order.repo.OrderRepository;
import com.lpu.shopsphere.order.messaging.OrderEventPublisher;

@Service
public class CartService {
  private final AuthenticatedUserService authenticatedUserService;
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final OrderRepository orderRepository;
  private final CatalogClient catalogClient;
  private final OrderEventPublisher orderEventPublisher;

  public CartService(
      AuthenticatedUserService authenticatedUserService,
      CartRepository cartRepository,
      CartItemRepository cartItemRepository,
      OrderRepository orderRepository,
      CatalogClient catalogClient,
      OrderEventPublisher orderEventPublisher) {
    this.authenticatedUserService = authenticatedUserService;
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.orderRepository = orderRepository;
    this.catalogClient = catalogClient;
    this.orderEventPublisher = orderEventPublisher;
  }

  @Transactional
  public Cart getOrCreateCart() {
    String email = authenticatedUserService.getCurrentEmail();
    Optional<Cart> existing = cartRepository.findByUserEmail(email);
    if (existing.isPresent()) {
      return existing.get();
    }
    Cart cart = new Cart();
    cart.setUserEmail(email);
    return cartRepository.save(cart);
  }

  @Transactional
  public Cart addToCart(Long productId, int quantity) {
    if (quantity < 1) {
      throw new IllegalArgumentException("quantity must be >= 1");
    }

    CatalogProductDto product = catalogClient.getProduct(productId);
    if (product == null) {
      throw new IllegalArgumentException("product not found: " + productId);
    }

    Cart cart = getOrCreateCart();

    CartItem existing = null;
    for (CartItem item : cart.getItems()) {
      if (item.getProductId() != null && item.getProductId().equals(productId)) {
        existing = item;
        break;
      }
    }

    if (existing != null) {
      existing.setQuantity(existing.getQuantity() + quantity);
      return cartRepository.save(cart);
    }

    CartItem item = new CartItem();
    item.setCart(cart);
    item.setProductId(product.getId());
    item.setProductName(product.getName());
    item.setUnitPrice(product.getPrice());
    item.setQuantity(quantity);
    cart.getItems().add(item);

    return cartRepository.save(cart);
  }

  @Transactional(readOnly = true)
  public Cart getMyCart() {
    String email = authenticatedUserService.getCurrentEmail();
    return cartRepository.findByUserEmail(email).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
  }

  @Transactional
  public Cart updateItem(Long itemId, CartItemUpdateRequest request) {
    if (request.getQuantity() < 1) {
      throw new IllegalArgumentException("quantity must be >= 1");
    }

    CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
    String email = authenticatedUserService.getCurrentEmail();
    if (item.getCart() == null || item.getCart().getUserEmail() == null || !item.getCart().getUserEmail().equals(email)) {
      throw new IllegalArgumentException("Cart item not owned by current user");
    }

    item.setQuantity(request.getQuantity());
    cartItemRepository.save(item);
    return getMyCart();
  }

  @Transactional
  public String removeItem(Long itemId) {
    CartItem item = cartItemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
    String email = authenticatedUserService.getCurrentEmail();
    if (item.getCart() == null || item.getCart().getUserEmail() == null || !item.getCart().getUserEmail().equals(email)) {
      throw new IllegalArgumentException("Cart item not owned by current user");
    }
    cartItemRepository.delete(item);
    return "Item removed";
  }

  @Transactional
  public Order startCheckout(CheckoutRequest request) {
    Cart cart = getMyCart();
    if (cart.getItems() == null || cart.getItems().isEmpty()) {
      throw new IllegalArgumentException("Cart is empty");
    }

    Order order = new Order();
    order.setUserEmail(cart.getUserEmail());
    order.setStatus(OrderStatus.CHECKOUT);
    order.setAddressLine(request.getAddressLine());
    order.setDeliveryOption(request.getDeliveryOption());
    order.setPaymentMethod(request.getPaymentMethod());

    for (CartItem ci : cart.getItems()) {
      OrderItem oi = new OrderItem();
      oi.setOrder(order);
      oi.setProductId(ci.getProductId());
      oi.setProductName(ci.getProductName());
      oi.setUnitPrice(ci.getUnitPrice());
      oi.setQuantity(ci.getQuantity());
      order.getItems().add(oi);
    }

    Order saved = orderRepository.save(order);

    // Publish event so catalog-service can decrement stock asynchronously.
    orderEventPublisher.publishOrderPlaced(saved);

    // Clear cart after order creation.
    cart.getItems().clear();
    cartRepository.save(cart);
    return saved;
  }

  @Transactional
  public Order processPayment(PaymentRequest request) {
    Order order = orderRepository.findById(request.getOrderId())
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.getOrderId()));

    String email = authenticatedUserService.getCurrentEmail();
    if (order.getUserEmail() == null || !order.getUserEmail().equals(email)) {
      throw new IllegalArgumentException("Order not owned by current user");
    }

    if (order.getStatus() != OrderStatus.CHECKOUT && order.getStatus() != OrderStatus.DRAFT) {
      throw new IllegalArgumentException("Order not ready for payment");
    }

    order.setPaymentMethod(request.getPaymentMethod());
    order.setStatus(OrderStatus.PAID);
    return orderRepository.save(order);
  }
}

