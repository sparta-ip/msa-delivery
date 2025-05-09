package com.msa_delivery.delivery.infrastructure.repository;

import com.msa_delivery.delivery.application.dto.DeliveryRouteDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JpaDeliveryRouteRepository {
    Page<DeliveryRouteDto> searchRoutes(String deliveryStatus, UUID departureId, UUID arrivalId, Long deliveryManagerId,
                                        String createdFrom, String createdTo, Pageable pageable);
}
