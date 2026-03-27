package com.lpu.shopsphere.order.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.lpu.shopsphere.order.dto.CatalogProductDto;

@Component
public class CatalogClient {
  private final RestTemplate restTemplate;

  public CatalogClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public CatalogProductDto getProduct(Long productId) {
    String url = "http://catalog-service/catalog/products/" + productId;
    return restTemplate.getForObject(url, CatalogProductDto.class);
  }
}

