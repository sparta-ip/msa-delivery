package com.msa_delivery.user.application.service;

import com.msa_delivery.user.application.dtos.ApiResponseDto;
import com.msa_delivery.user.infrastructure.dtos.GetUUIDDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface DeliveryService {
    ResponseEntity<ApiResponseDto<GetUUIDDto>> getDeliveryByUserId(Long userId, String headerUserId, String username, String role);

    ResponseEntity<ApiResponseDto<?>> softDeleteDelivery(UUID deliveryId, String headerUserId, String username, String role);

    ResponseEntity<ApiResponseDto<GetUUIDDto>> getDeliveryManagerByUserId(Long userId, String headerUserId, String username, String role);

    ResponseEntity<ApiResponseDto<?>> softDeleteDeliveryManager(Long deliveryId, String headerUserId, String username, String role);
}
