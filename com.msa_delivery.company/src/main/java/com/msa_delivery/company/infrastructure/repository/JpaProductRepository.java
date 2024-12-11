package com.msa_delivery.company.infrastructure.repository;

import com.msa_delivery.company.application.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JpaProductRepository {
    Page<ProductDto> searchProducts(String search, Integer minPrice, Integer maxPrice, Integer minQuantity, Integer maxQuantity, Pageable pageable, UUID hubId);
}
