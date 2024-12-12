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
import com.msa_delivery.delivery.presentation.request.DeliveryManagerUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Integer maxSequence = deliveryManagerRepository.findMaxSequenceByType(type);
        Integer sequence = (maxSequence != null ? maxSequence : 0) + 1;

        DeliveryManager deliveryManager = DeliveryManager.create(id, null, slackId, hubId, type, sequence);
        deliveryManager.setCreatedBy(username);
        deliveryManagerRepository.save(deliveryManager);
        return DeliveryManagerDto.create(deliveryManager);
    }

    public DeliveryManagerDto updateManager(Long deliveryManagerId, DeliveryManagerUpdateRequest request, String userId, String username, String role) {
        // 배송 담당자 조회
        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeleteFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 담당자를 찾을 수 없습니다."));

        // Hub ID 수정
        UUID hubId = request.getHubId() != null ? request.getHubId() : manager.getHubId();

        // 타입 수정
        DeliveryManagerType type = request.getType() != null ? DeliveryManagerType.fromString(request.getType()) : manager.getType();
        // 배송 담당자 타입이 업체 배송 담당자인 경우
        if (DeliveryManagerType.fromString(request.getType()).equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {
            if (request.getHubId() != null) {
                HubDto hub = hubClient.getHubById(request.getHubId()).getBody().getData();
                hubId = hub.getHubId();
            } else {
                throw new IllegalArgumentException("허브 ID 를 입력해주세요.");
            }
        } else if (type.equals(DeliveryManagerType.HUB_DELIVERY_MANAGER)) {
            // 허브 배송 담당자는 특정 허브에 종속되지 않을 수 있음 (optional)
            hubId = null;
        }

        // Sequence 수정 및 관계 고려
        Integer sequence = request.getSequence() != null ? request.getSequence() : manager.getSequence();
        if (request.getSequence() != null) {
            if (sequence <= 0) {
                throw new IllegalArgumentException("시퀀스 값은 1 이상이어야 합니다.");
            }
            Integer maxSequence = deliveryManagerRepository.findMaxSequenceByType(type);
            if (sequence > maxSequence + 1) {
                throw new IllegalArgumentException("입력된 시퀀스 값(" + sequence + ")이 최대값(" + maxSequence + ")보다 클 수 없습니다.");
            }

            // 시퀀스 중복 처리
            deliveryManagerRepository.incrementSequence(type, sequence);
        }


        manager.update(type, hubId, sequence);
        manager.setUpdatedBy(username);
        return DeliveryManagerDto.create(manager);
    }

    @Transactional
    public void updateOrderId(DeliveryManager deliveryManager, UUID orderId) {
        deliveryManager.updateOrderId(orderId);
    }
}
