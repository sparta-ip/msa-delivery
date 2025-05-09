package com.msa_delivery.company.domain.repository;

import com.msa_delivery.company.domain.model.Product;
import com.msa_delivery.company.infrastructure.repository.JpaProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaProductRepository {
    Optional<Product> findByIdAndIsDeleteFalse(UUID productId);
}
