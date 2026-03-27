package com.lpu.shopsphere.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

  @GetMapping("/products/{productId}")
  Object getProduct(
      @PathVariable Long productId,
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );

  @PostMapping("/admin/products")
  Object createProduct(
      @RequestBody Object productRequest,
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );

  @PutMapping("/admin/products/{productId}")
  Object updateProduct(
      @PathVariable Long productId,
      @RequestBody Object productRequest,
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );

  @DeleteMapping("/admin/products/{productId}")
  void deleteProduct(
      @PathVariable Long productId,
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );

  @GetMapping("/admin/products")
  Object getAllProducts(
      @RequestHeader("X-User-Email") String email,
      @RequestHeader("X-User-Role") String role
  );
}
