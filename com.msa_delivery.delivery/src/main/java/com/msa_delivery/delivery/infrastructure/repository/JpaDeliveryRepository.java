package com.msa_delivery.delivery.infrastructure.repository;

import com.msa_delivery.delivery.application.dto.DeliveryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JpaDeliveryRepository {
    Page<DeliveryDto> searchDeliveries(String search, String deliveryStatus, UUID departureId, UUID arrivalId, Long deliveryManagerId, Long receiverId, String createdFrom, String createdTo, Pageable pageable);
}
