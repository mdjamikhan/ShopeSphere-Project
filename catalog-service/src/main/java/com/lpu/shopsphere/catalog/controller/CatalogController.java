package com.lpu.shopsphere.catalog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lpu.shopsphere.catalog.dto.AdminProductRequest;
import com.lpu.shopsphere.catalog.domain.Product;
import com.lpu.shopsphere.catalog.repo.ProductRepository;
import com.lpu.shopsphere.catalog.service.CatalogAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/catalog")
@Validated
public class CatalogController {
  private final CatalogAdminService catalogAdminService;
  private final ProductRepository productRepository;

  public CatalogController(CatalogAdminService catalogAdminService, ProductRepository productRepository) {
    this.catalogAdminService = catalogAdminService;
    this.productRepository = productRepository;
  }

  @GetMapping("/products")
  public List<Product> products() {
    return productRepository.findAll();
  }

  @GetMapping("/products/featured")
  public List<Product> featuredProducts() {
    return productRepository.findByFeaturedTrue();
  }

  @GetMapping("/products/{id}")
  public Product productById(@PathVariable Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
  }

  @PostMapping("/products")
  public ResponseEntity<Product> createProduct(@Valid @RequestBody AdminProductRequest request) {
    return ResponseEntity.ok(catalogAdminService.createProduct(request));
  }

  @PutMapping("/products/{id}")
  public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody AdminProductRequest request) {
    return ResponseEntity.ok(catalogAdminService.updateProduct(id, request));
  }

  @DeleteMapping("/products/{id}")
  public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
    productRepository.deleteById(id);
    return ResponseEntity.ok("Product deleted");
  }
}

