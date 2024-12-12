package com.msa_delivery.delivery.application.service;

import com.msa_delivery.delivery.domain.model.DeliveryRoute;
import com.msa_delivery.delivery.domain.model.DeliveryStatus;
import com.msa_delivery.delivery.domain.repository.DeliveryRouteRepository;
import com.msa_delivery.delivery.presentation.request.DeliveryUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    private final DeliveryRouteRepository deliveryRouteRepository;

    @Transactional
    public void updateRouteStatus(UUID deliveryId, DeliveryStatus deliveryStatus) {
        DeliveryRoute route = deliveryRouteRepository.findByDeliveryIdAndIsDeleteFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        route.updateStatus(deliveryStatus);
    }
}
