package com.msa_delivery.delivery.domain.repository;

import com.msa_delivery.delivery.domain.model.DeliveryRoute;
import com.msa_delivery.delivery.infrastructure.repository.JpaDeliveryRouteRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID>, JpaDeliveryRouteRepository {
    Optional<DeliveryRoute> findByIdAndIsDeleteFalse(UUID deliveryRouteId);

    Optional<DeliveryRoute> findByDeliveryIdAndIsDeleteFalse(UUID deliveryId);
}
