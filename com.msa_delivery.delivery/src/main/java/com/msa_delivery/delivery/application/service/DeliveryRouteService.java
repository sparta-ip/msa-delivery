package com.msa_delivery.delivery.application.service;

import com.msa_delivery.delivery.application.dto.DeliveryRouteDto;
import com.msa_delivery.delivery.application.util.CheckRole;
import com.msa_delivery.delivery.domain.model.DeliveryManager;
import com.msa_delivery.delivery.domain.model.DeliveryRoute;
import com.msa_delivery.delivery.domain.model.DeliveryStatus;
import com.msa_delivery.delivery.domain.repository.DeliveryManagerRepository;
import com.msa_delivery.delivery.domain.repository.DeliveryRouteRepository;
import com.msa_delivery.delivery.presentation.request.DeliveryRouteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryManagerService deliveryManagerService;
    private final CheckRole checkRole;

    @Transactional
    public DeliveryRouteDto updateRoute(UUID deliveryRouteId, DeliveryRouteRequest request, String userId, String username, String role) throws AccessDeniedException {
        // 기존 데이터 조회
        DeliveryRoute route = deliveryRouteRepository.findByIdAndIsDeleteFalse(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Long deliveryManagerId = route.getDeliveryManager().getId();
        UUID departureId = route.getDepartureId();
        UUID arrivalId = route.getArrivalId();

        // 권한 확인
        checkRole.validateRole(role, userId, username, departureId, arrivalId, deliveryManagerId, "수정");

        // 실제 이동 거리 및 소요 시간
        Integer distance = route.getDistance();
        Integer duration = route.getDuration();

        // 자동 계산된 값이 없을 경우에만 직접 업데이트 허용
        if ((request.getDistance() != null || request.getDuration() != null)) {
            if (distance != null || duration != null) {
                throw new IllegalArgumentException("자동 계산된 값이 이미 존재합니다.");
            }
            distance = request.getDistance() != null ? request.getDistance() : distance;
            duration = request.getDuration() != null ? request.getDuration() : duration;
        }

        // 배송 상태 업데이트
        DeliveryStatus deliveryStatus = request.getDeliveryStatus() != null ?
                DeliveryStatus.fromString(request.getDeliveryStatus()) : route.getDeliveryStatus();
        if (request.getDeliveryStatus() != null) {
            if (!route.getDeliveryStatus().nextStatus(deliveryStatus)) {
                throw new IllegalArgumentException("잘못된 상태 전환입니다.");
            }
            // 배송 상태가 'IN_HUB_TRANSFER'이면 거리와 소요 시간 업데이트 불가
            if (deliveryStatus == DeliveryStatus.IN_HUB_TRANSFER &&
                    (request.getDistance() != null || request.getDuration() != null)) {
                throw new IllegalArgumentException("배송 상태가 이동 중일 때는 거리와 소요 시간을 업데이트할 수 없습니다.");
            }
            // 배송 상태가 목적지 허브 도착일 경우 거리 및 소요 시간 계산
            if (deliveryStatus == DeliveryStatus.ARRIVED_AT_DESTINATION_HUB) {
                if (route.getStartTime() == null) {
                    throw new IllegalArgumentException("배송 시작 시간이 기록되지 않았습니다.");
                }
                if (request.getDuration() == null && duration == null) {
                    route.updateStatus(deliveryStatus); // 자동 계산
                }
            }
        }

        // 배송 담당자 업데이트
        DeliveryManager currentManager = route.getDeliveryManager();
        if (currentManager != null) {
            // 기존 담당자의 orderId 초기화
            deliveryManagerService.updateOrderId(currentManager, null);
        }
        // 새로운 배송 담당자 조회
        // 배송 담당자는 한 번에 하나의 배송(주문) 만 처리
        DeliveryManager newManager = deliveryManagerRepository.findByIdAndIsDeleteFalse(request.getDeliveryManagerId())
                .orElseThrow(() -> new IllegalArgumentException("지정된 새로운 배송 담당자를 찾을 수 없습니다."));
        if (newManager.getOrderId() != null) {
            throw new IllegalArgumentException("이미 배송이 지정되어 있는 담당자입니다.");
        }
        // 새로운 담당자에게 orderId 배정
        deliveryManagerService.updateOrderId(newManager, route.getDelivery().getOrderId());

        route.updateStatus(deliveryStatus);
        route.update(newManager, distance, duration);
        route.setUpdatedBy(username);
        return DeliveryRouteDto.create(route);
    }

    @Transactional
    public void deleteRoute(UUID deliveryRouteId, String userId, String username, String role) throws AccessDeniedException {
        DeliveryRoute route = deliveryRouteRepository.findByIdAndIsDeleteFalse(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));
        Long deliveryManagerId = route.getDeliveryManager().getId();
        UUID departureId = route.getDepartureId();
        UUID arrivalId = route.getArrivalId();

        // 권한 확인
        checkRole.validateRole(role, userId, username, departureId, arrivalId, deliveryManagerId, "삭제");
        route.delete(username);
    }

    @Transactional(readOnly = true)
    public DeliveryRouteDto getRouteById(UUID deliveryRouteId, String userId, String username, String role) throws AccessDeniedException {
        DeliveryRoute route = deliveryRouteRepository.findByIdAndIsDeleteFalse(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Long deliveryManagerId = route.getDeliveryManager().getId();
        UUID departureId = route.getDepartureId();
        UUID arrivalId = route.getArrivalId();

        // 권한 확인
        checkRole.validateRole(role, userId, username, departureId, arrivalId, deliveryManagerId, "조회");

        return DeliveryRouteDto.create(route);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryRouteDto> getRoutes(String deliveryStatus, UUID departureId, UUID arrivalId, Long deliveryManagerId,
                                            String createdFrom, String createdTo, String userId, String username, String role, Pageable pageable) throws AccessDeniedException {
        // 역할별 검색 조건 설정
        switch (role) {
            case "DELIVERY_MANAGER":
                // 배송 담당자는 본인 ID로 강제 필터링
                deliveryManagerId = Long.valueOf(userId);
                break;

            case "HUB_MANAGER":
                // 허브 관리자는 출발/도착 허브 ID 검증 및 필터링
                if (departureId == null && arrivalId == null) {
                    throw new AccessDeniedException("출발/도착 허브 ID 가 필요합니다.");
                }
                if (!checkRole.isHubManager(userId, username, role, departureId) &&
                        !checkRole.isHubManager(userId, username, role, arrivalId)) {
                    throw new AccessDeniedException("해당 허브의 배송 정보를 조회할 권한이 없습니다.");
                }
                break;

            case "MASTER":
            case "COMPANY_MANAGER":
                // MASTER 는 제한 없음, 업체 담당자는 추가 조건 없이 검색 가능
                break;

            default:
                throw new IllegalArgumentException("유효하지 않은 역할입니다.");
        }

        return deliveryRouteRepository.searchRoutes(deliveryStatus, departureId, arrivalId, deliveryManagerId,
                createdFrom, createdTo, pageable);
    }


    // 배송 경로의 배송 상태 업데이트
    @Transactional
    public void updateRouteStatus(UUID deliveryId, DeliveryStatus deliveryStatus) {
        DeliveryRoute route = deliveryRouteRepository.findByDeliveryIdAndIsDeleteFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        route.updateStatus(deliveryStatus);
    }
}
