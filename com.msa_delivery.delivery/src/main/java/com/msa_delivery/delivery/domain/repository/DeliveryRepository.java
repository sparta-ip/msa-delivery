package com.msa_delivery.delivery.domain.repository;

import com.msa_delivery.delivery.domain.model.Delivery;
import com.msa_delivery.delivery.infrastructure.repository.JpaDeliveryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID>, JpaDeliveryRepository {
    Optional<Delivery> findByIdAndIsDeleteFalse(UUID deliveryId);
}
