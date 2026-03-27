package com.lpu.shopsphere.catalog.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lpu.shopsphere.catalog.config.RabbitMQConfig;
import com.lpu.shopsphere.catalog.domain.Product;
import com.lpu.shopsphere.catalog.repo.ProductRepository;

/**
 * Listens to the {@code order.placed.queue} and decrements product stock
 * for each item in the order.  Runs inside a transaction so all updates
 * succeed or roll back together.
 */
@Component
public class OrderPlacedEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacedEventListener.class);

    private final ProductRepository productRepository;

    public OrderPlacedEventListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_PLACED_QUEUE)
    @Transactional
    public void onOrderPlaced(OrderPlacedEvent event) {
        if (event == null || event.getItems() == null) {
            log.warn("[RabbitMQ] Received null or empty OrderPlacedEvent — skipping.");
            return;
        }

        log.info("[RabbitMQ] Received OrderPlacedEvent: orderId={}, items={}",
                event.getOrderId(), event.getItems().size());

        for (OrderPlacedEvent.OrderItemDto item : event.getItems()) {
            productRepository.findById(item.getProductId()).ifPresentOrElse(
                product -> decrementStock(product, item.getQuantity(), event.getOrderId()),
                () -> log.warn("[RabbitMQ] Product not found for id={} in orderId={}",
                        item.getProductId(), event.getOrderId())
            );
        }
    }

    private void decrementStock(Product product, int quantity, Long orderId) {
        int currentStock = product.getStock();
        int newStock = currentStock - quantity;

        if (newStock < 0) {
            log.warn("[RabbitMQ] Stock went negative for productId={} in orderId={}. " +
                     "currentStock={}, requested={}. Clamping to 0.",
                    product.getId(), orderId, currentStock, quantity);
            newStock = 0;
        }

        product.setStock(newStock);
        productRepository.save(product);

        log.info("[RabbitMQ] Stock decremented: productId={}, {} -> {} (orderId={})",
                product.getId(), currentStock, newStock, orderId);
    }
}
