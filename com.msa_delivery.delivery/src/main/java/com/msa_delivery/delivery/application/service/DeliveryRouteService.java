package com.msa_delivery.delivery.application.service;

import com.msa_delivery.delivery.application.dto.DeliveryRouteDto;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryManagerService deliveryManagerService;

    @Transactional
    public DeliveryRouteDto updateRoute(UUID deliveryRouteId, DeliveryRouteRequest request, String userId, String username, String role) {
        // TODO: 권한 검증
        // 기존 데이터 조회
        DeliveryRoute route = deliveryRouteRepository.findByIdAndIsDeleteFalse(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

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
    public void deleteRoute(UUID deliveryRouteId, String userId, String username, String role) {
        DeliveryRoute route = deliveryRouteRepository.findByIdAndIsDeleteFalse(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        route.delete(username);
    }

    @Transactional(readOnly = true)
    public DeliveryRouteDto getRouteById(UUID deliveryRouteId, String userId, String role) {
        DeliveryRoute route = deliveryRouteRepository.findByIdAndIsDeleteFalse(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        return DeliveryRouteDto.create(route);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryRouteDto> getRoutes(String deliveryStatus, UUID departureId, UUID arrivalId, Long deliveryManagerId,
                                            String createdFrom, String createdTo, String userId, String role, Pageable pageable) {
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
