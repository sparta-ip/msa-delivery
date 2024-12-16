package com.msa_delivery.delivery.application.service;

import com.msa_delivery.delivery.application.dto.DeliveryManagerDto;
import com.msa_delivery.delivery.application.util.CheckRole;
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

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final UserClient userClient;
    private final HubClient hubClient;
    private final CheckRole checkRole;

    @Transactional
    public DeliveryManagerDto createManager(DeliveryManagerRequest request, String userId, String username, String role) throws AccessDeniedException {
        log.info("Service createManager :: ");
        log.info("Request :: " + request.getUserId());
        // 배송 담당자 타입
        DeliveryManagerType type = DeliveryManagerType.fromString(request.getType());
        UUID hubId = request.getHubId();
        // 권한 검증
        checkRole.validateManagerRole(userId, username, role, hubId, null, "생성", type);

        // 배송 담당자 확인 (해당 유저가 있는지)
        UserDto user = userClient.getUserById(request.getUserId(), userId, username, role).getBody().getData();
        Long id = user.getUserId();
        String slackId = user.getSlackId();

        // 최대 시퀀스 값 가져오기
        Integer maxSequence = deliveryManagerRepository.findMaxSequenceByType(type);
        Integer sequence = (maxSequence != null ? maxSequence : 0) + 1;

        DeliveryManager deliveryManager = DeliveryManager.create(id, null, slackId, hubId, type, sequence);
        deliveryManager.setCreatedBy(username);
        deliveryManagerRepository.save(deliveryManager);
        return DeliveryManagerDto.create(deliveryManager);
    }

    @Transactional
    public DeliveryManagerDto updateManager(Long deliveryManagerId, DeliveryManagerUpdateRequest request, String userId, String username, String role) throws AccessDeniedException {
        // 배송 담당자 조회
        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeleteFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 담당자를 찾을 수 없습니다."));

        // 타입 및 허브 ID 변경 처리
        DeliveryManagerType type = request.getType() != null
                ? DeliveryManagerType.fromString(request.getType())
                : manager.getType();
        UUID hubId = request.getHubId() != null ? request.getHubId() : manager.getHubId();

        // 권한 검증
        checkRole.validateManagerRole(role, userId, username, hubId, deliveryManagerId, "수정", type);

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
    public void deleteManager(Long deliveryManagerId, String userId, String username, String role) throws AccessDeniedException {
        // 배송 담당자 조회
        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeleteFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 담당자를 찾을 수 없습니다."));
        // 권한 검증
        checkRole.validateManagerRole(userId, username, role, manager.getHubId(), deliveryManagerId, "삭제", manager.getType());

        manager.delete(username);
    }


    @Transactional(readOnly = true)
    public DeliveryManagerDto getManagerById(Long deliveryManagerId, String userId, String username, String role) throws AccessDeniedException {
        // 배송 담당자 조회
        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeleteFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 담당자를 찾을 수 없습니다."));
        // 권한 검증
        checkRole.validateManagerRole(userId, username, role, manager.getHubId(), deliveryManagerId, "조회", manager.getType());
        return DeliveryManagerDto.create(manager);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryManagerDto> getManagers(String search, String type, Long deliveryManagerId, UUID hubId, UUID orderId, Integer sequenceMin, Integer sequenceMax,
                                                String createdFrom, String createdTo, String userId, String role, Pageable pageable) throws AccessDeniedException {

        // 역할별 검색 조건 설정
        switch (role) {
            case "MASTER":
                break;
            case "HUB_MANAGER":
                if (hubId != null) {
                    // 허브 ID 유효성 검사
                    HubDto hub = hubClient.getHubById(hubId, userId, userId, role).getBody().getData();
                    Long hubManagerId = hub.getHubManagerId();
                    if (!Long.valueOf(userId).equals(hubManagerId)) {
                        throw new AccessDeniedException("허브 관리자는 자신의 허브에만 접근할 수 있습니다.");
                    }
                }
                break;
            case "DELIVERY_MANAGER":
                // 배송 담당자는 본인 ID로 강제 필터링
                deliveryManagerId = Long.valueOf(userId);
                break;
            case "COMPANY_MANAGER":
                // 업체 담당자는 모든 작업 불가
                throw new AccessDeniedException("업체 담당자는 배송 담당자를 검색할 권한이 없습니다.");

            default:
                throw new IllegalArgumentException("유효하지 않은 역할입니다.");
        }

        return deliveryManagerRepository.searchManagers(search, type, deliveryManagerId, hubId, orderId, sequenceMin, sequenceMax, createdFrom, createdTo, pageable);
    }

    @Transactional
    public void updateOrderId(DeliveryManager deliveryManager, UUID orderId) {
        deliveryManager.updateOrderId(orderId);
    }
}
