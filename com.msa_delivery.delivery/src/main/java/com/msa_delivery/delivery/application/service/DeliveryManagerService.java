package com.msa_delivery.delivery.application.service;

import com.msa_delivery.delivery.domain.model.DeliveryManager;
import com.msa_delivery.delivery.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;

    @Transactional
    public void updateOrderId(DeliveryManager deliveryManager, UUID orderId) {
        deliveryManager.updateOrderId(orderId);
    }
}
