package com.msa_delivery.delivery.application.service;

import com.msa_delivery.delivery.application.dto.DeliveryManagerDto;
import com.msa_delivery.delivery.domain.model.DeliveryManager;
import com.msa_delivery.delivery.domain.model.DeliveryManagerType;
import com.msa_delivery.delivery.domain.repository.DeliveryManagerRepository;
import com.msa_delivery.delivery.infrastructure.client.HubClient;
import com.msa_delivery.delivery.infrastructure.client.HubDto;
import com.msa_delivery.delivery.infrastructure.client.UserClient;
import com.msa_delivery.delivery.infrastructure.client.UserDto;
import com.msa_delivery.delivery.presentation.request.DeliveryManagerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final UserClient userClient;
    private final HubClient hubClient;

    @Transactional
    public DeliveryManagerDto createManager(DeliveryManagerRequest request, String userId, String username, String role) {
        // 배송 담당자 확인
        UserDto user = userClient.getUserById(request.getUserId()).getBody().getData();
        Long id = user.getUserId();
        String slackId = user.getSlackId();

        // 배송 담당자 타입
        UUID hubId = request.getHubId();
        DeliveryManagerType type = DeliveryManagerType.fromString(request.getType());
        // 업체 배송 담당자는 허브 ID 필수
        if (type == DeliveryManagerType.COMPANY_DELIVERY_MANAGER) {
            if (hubId != null) {
                HubDto hub = hubClient.getHubById(request.getHubId()).getBody().getData();
                hubId = hub.getHubId();
            } else {
                throw new IllegalArgumentException("허브 ID 를 입력해주세요.");
            }
        }

        // 최대 시퀀스 값 가져오기
        Integer maxSequence = deliveryManagerRepository.findMaxSequence();
        Integer sequence = (maxSequence != null ? maxSequence : 0) + 1;

        DeliveryManager deliveryManager = DeliveryManager.create(id, null, slackId, hubId, type, sequence);
        deliveryManager.setCreatedBy(username);
        deliveryManagerRepository.save(deliveryManager);
        return DeliveryManagerDto.create(deliveryManager);
    }

    @Transactional
    public void updateOrderId(DeliveryManager deliveryManager, UUID orderId) {
        deliveryManager.updateOrderId(orderId);
    }
}
