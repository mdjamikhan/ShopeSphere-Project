package com.lpu.shopsphere.catalog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lpu.shopsphere.catalog.domain.Category;
import com.lpu.shopsphere.catalog.domain.Product;
import com.lpu.shopsphere.catalog.dto.AdminProductRequest;
import com.lpu.shopsphere.catalog.repo.CategoryRepository;
import com.lpu.shopsphere.catalog.repo.ProductRepository;

@Service
public class CatalogAdminService {
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public CatalogAdminService(ProductRepository productRepository, CategoryRepository categoryRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  public Product createProduct(AdminProductRequest request) {
    Product product = new Product();
    applyCommonFields(product, request);
    product.setCategory(resolveCategory(request));
    return productRepository.save(product);
  }

  @Transactional
  public Product updateProduct(Long id, AdminProductRequest request) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    applyCommonFields(product, request);
    product.setCategory(resolveCategory(request));
    return productRepository.save(product);
  }

  private void applyCommonFields(Product product, AdminProductRequest request) {
    product.setName(request.getName());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    product.setFeatured(request.isFeatured());
    product.setDescription(request.getDescription());
  }

  private Category resolveCategory(AdminProductRequest request) {
    // Priority:
    // 1) If categoryId is present -> load it and optionally validate it against categoryName.
    // 2) Else if categoryName is present -> find existing or auto-create.
    Long categoryId = request.getCategoryId();
    String categoryName = request.getCategoryName();

    if (categoryId != null) {
      Category category = categoryRepository.findById(categoryId)
          .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

      if (categoryName != null && !categoryName.isBlank()) {
        String normalized = categoryName.trim();
        if (category.getName() == null || !category.getName().equalsIgnoreCase(normalized)) {
          throw new IllegalArgumentException("categoryId does not match categoryName");
        }
      }
      return category;
    }

    if (categoryName != null && !categoryName.isBlank()) {
      String name = categoryName.trim();
      return categoryRepository.findByName(name).orElseGet(() -> {
        Category newCategory = new Category();
        newCategory.setName(name);
        return categoryRepository.save(newCategory);
      });
    }

    return null;
  }
}

