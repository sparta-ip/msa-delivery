package com.msa_delivery.delivery.infrastructure.repository;

import com.msa_delivery.delivery.application.dto.DeliveryManagerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JpaDeliveryManagerRepository {
    Page<DeliveryManagerDto> searchManagers(String search, String type, UUID hubId,UUID orderId, Integer sequenceMin, Integer sequenceMax,
                                            String createdFrom, String createdTo, Pageable pageable);
}
