package com.msa_delivery.delivery.infrastructure.repository;

import com.msa_delivery.delivery.application.dto.DeliveryManagerDto;
import com.msa_delivery.delivery.domain.model.DeliveryManager;
import com.msa_delivery.delivery.domain.model.DeliveryManagerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryManagerRepository {
    Optional<DeliveryManager> findAvailableDeliveryManager(DeliveryManagerType type, UUID hubId);

    Optional<DeliveryManager> findAvailableHubDeliveryManager();

    Page<DeliveryManagerDto> searchManagers(String search, String type, Long deliveryManagerId, UUID hubId,UUID orderId, Integer sequenceMin, Integer sequenceMax,
                                            String createdFrom, String createdTo, Pageable pageable);
}
