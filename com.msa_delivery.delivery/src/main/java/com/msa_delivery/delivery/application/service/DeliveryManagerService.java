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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final UserClient userClient;
    private final HubClient hubClient;

    @Transactional
    public DeliveryManagerDto createManager(DeliveryManagerRequest request, String userId, String username, String role) {
        log.info("Service createManager :: ");
        // 배송 담당자 확인
        UserDto user = userClient.getUserById(request.getUserId(), userId, username, role).getBody().getData();
        Long id = user.getUserId();
        String slackId = user.getSlackId();

        // 배송 담당자 타입
        UUID hubId = request.getHubId();
        DeliveryManagerType type = DeliveryManagerType.fromString(request.getType());
        // 업체 배송 담당자는 허브 ID 필수
        if (type == DeliveryManagerType.COMPANY_DELIVERY_MANAGER) {
            if (hubId != null) {
                HubDto hub = hubClient.getHubById(request.getHubId(), userId, username, role).getBody().getData();
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

    @Transactional
    public DeliveryManagerDto updateManager(Long deliveryManagerId, DeliveryManagerUpdateRequest request, String userId, String username, String role) {
        // 배송 담당자 조회
        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeleteFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 담당자를 찾을 수 없습니다."));

        // 기존 타입 확인
        DeliveryManagerType type = manager.getType();
        UUID hubId = manager.getHubId(); // 기존 허브 ID 유지

        // 타입 및 허브 ID 변경 처리
        if (request.getType() != null) {
            DeliveryManagerType newType = DeliveryManagerType.fromString(request.getType());

            // 타입 변경 시 허브 ID 유효성 검증
            if (newType.equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {
                if (request.getHubId() != null) {
                    HubDto hub = hubClient.getHubById(request.getHubId(), userId, username, role).getBody().getData();
                    hubId = hub.getHubId(); // 유효한 허브 ID로 설정
                } else {
                    throw new IllegalArgumentException("허브 ID를 입력해주세요.");
                }
            } else if (newType.equals(DeliveryManagerType.HUB_DELIVERY_MANAGER)) {
                // 허브 배송 담당자는 허브 ID를 null 로 설정
                hubId = null;
            }

            type = newType; // 타입 변경 적용
        } else if (request.getHubId() != null) {
            // 타입 변경 없이 허브 ID만 전달된 경우
            if (type.equals(DeliveryManagerType.HUB_DELIVERY_MANAGER)) {
                throw new IllegalArgumentException("허브 배송 담당자는 허브 ID를 설정할 수 없습니다.");
            } else if (type.equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {
                HubDto hub = hubClient.getHubById(request.getHubId(), userId, username, role).getBody().getData();
                hubId = hub.getHubId();
            }
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
    public void deleteManager(Long deliveryManagerId, String userId, String username, String role) {
        // 배송 담당자 조회
        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeleteFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 담당자를 찾을 수 없습니다."));

        manager.delete(username);
    }


    @Transactional(readOnly = true)
    public DeliveryManagerDto getManagerById(Long deliveryManagerId, String userId, String role) {
        // 배송 담당자 조회
        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeleteFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 담당자를 찾을 수 없습니다."));

        return DeliveryManagerDto.create(manager);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryManagerDto> getManagers(String search, String type, UUID hubId, Integer sequenceMin, Integer sequenceMax,
                                                String createdFrom, String createdTo, String userId, String role, Pageable pageable) {
        return deliveryManagerRepository.searchManagers(search, type, hubId, sequenceMin, sequenceMax, createdFrom, createdTo, pageable);
    }

    @Transactional
    public void updateOrderId(DeliveryManager deliveryManager, UUID orderId) {
        deliveryManager.updateOrderId(orderId);
    }
}
