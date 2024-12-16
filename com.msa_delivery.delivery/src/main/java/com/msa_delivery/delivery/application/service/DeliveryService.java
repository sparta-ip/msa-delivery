package com.msa_delivery.delivery.application.service;

import com.msa_delivery.delivery.application.dto.DeliveryCreateDto;
import com.msa_delivery.delivery.application.dto.DeliveryDto;
import com.msa_delivery.delivery.application.util.CheckRole;
import com.msa_delivery.delivery.domain.model.*;
import com.msa_delivery.delivery.domain.repository.DeliveryManagerRepository;
import com.msa_delivery.delivery.domain.repository.DeliveryRepository;
import com.msa_delivery.delivery.domain.repository.DeliveryRouteRepository;
import com.msa_delivery.delivery.infrastructure.client.*;
import com.msa_delivery.delivery.presentation.request.DeliveryRequest;
import com.msa_delivery.delivery.presentation.request.DeliveryUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryRouteService deliveryRouteService;
    private final DeliveryManagerService deliveryManagerService;
    private final HubClient hubClient;
    private final UserClient userClient;
    private final CheckRole checkRole;

    @Transactional
    public DeliveryCreateDto createDelivery(DeliveryRequest request, String userId, String username, String role) {
        // TODO: 배송 권한 검증
        /*
          배송 생성
          이 메서드는 주문 완료 시 자동으로 호출되며, 권한 검증이 필요하지 않습니다.
          생성된 배송은 이후 수정, 삭제, 조회 시에만 권한 검증을 받습니다.
         */
        // 배송 생성
        Delivery delivery = createDeliveryEntity(request, username);

        // 배송 경로 생성
        DeliveryRoute deliveryRoute = createDeliveryRouteEntity(request, delivery, userId, username, role);

        return DeliveryCreateDto.create(delivery, deliveryRoute);
    }

    // 배송 생성 로직 분리
    @Transactional
    private Delivery createDeliveryEntity(DeliveryRequest request, String username) {
        UUID orderId = request.getOrderId();
        Long receiverId = request.getReceiverId();
        String receiverSlackId = request.getReceiverSlackId();
        UUID departureId = request.getDepartureId();
        UUID arrivalId = request.getArrivalId();
        String address = request.getAddress();
        DeliveryStatus deliveryStatus = DeliveryStatus.WAITING_AT_HUB;

        // 업체 배송 담당자 순차 배정
        // 배송 담당자 타입이 업체 배송 담당자이고 orderId 가 null 인 값 중에 sequence 가 가장 작은 값 (isDeleteIsFalse)
        DeliveryManager companyDeliveryManager = deliveryManagerRepository
                .findAvailableDeliveryManager(DeliveryManagerType.COMPANY_DELIVERY_MANAGER, arrivalId)
                .orElseThrow(() -> new IllegalArgumentException("지정 할 수 있는 업체 배송 담당자가 없습니다."));
        deliveryManagerService.updateOrderId(companyDeliveryManager, orderId);

        // 배송 생성
        Delivery delivery = Delivery.create(orderId, companyDeliveryManager, receiverId, receiverSlackId, departureId, arrivalId, deliveryStatus, address);
        delivery.setCreatedBy(username);
        deliveryRepository.save(delivery);
        return delivery;
    }

    // 배송 경로 생성 로직 분리
    @Transactional
    private DeliveryRoute createDeliveryRouteEntity(DeliveryRequest request, Delivery delivery, String userId, String username, String role) {
        UUID departureId = request.getDepartureId();
        UUID arrivalId = request.getArrivalId();
        DeliveryStatus deliveryStatus = DeliveryStatus.WAITING_AT_HUB;

        // 허브 간 배송 담당자 순차 배정
        // 배송 담당자 타입이 허브 배송 담당자이고 orderId 가 null 인 값 중에 sequence 가 가장 작은 값 (isDeleteIsFalse)
        DeliveryManager hubDeliveryManager = deliveryManagerRepository
                .findAvailableHubDeliveryManager()
                .orElseThrow(() -> new IllegalArgumentException("지정 할 수 있는 허브 배송 담당자가 없습니다."));
        deliveryManagerService.updateOrderId(hubDeliveryManager, delivery.getOrderId());

        // 예상 거리, 예상 소요시간은 HubRouteClient 호출
        HubRouteDto hubRouteDto = hubClient.getHubRoute(departureId, arrivalId, userId, username, role).getBody().getData();
        Integer expectDistance = hubRouteDto.getDistance();
        Integer expectDuration = hubRouteDto.getDuration();

        // TODO: 시퀀스, 실제 거리, 실제 소요 시간
        // sequence: P2P 면 sequence 는 항상 1 (의미 없음)
        // 실제 거리, 실제 소요시간은 계산해야함 -> 배송 완료(COMPLETE) 되면 업데이트 되게 해야 할 듯 -> 처음엔 null?

        // 배송 경로 생성
        DeliveryRoute deliveryRoute = DeliveryRoute.create(delivery, hubDeliveryManager, 1, departureId, arrivalId, expectDistance, expectDuration, null, null, deliveryStatus);
        deliveryRoute.setCreatedBy(username);
        deliveryRouteRepository.save(deliveryRoute);
        return deliveryRoute;
    }

    @Transactional
    public DeliveryDto updateDelivery(UUID deliveryId, DeliveryUpdateRequest request, String userId, String username, String role) throws AccessDeniedException {
        Delivery delivery = deliveryRepository.findByIdAndIsDeleteFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송를 찾을 수 없습니다."));

        Long deliveryManagerId = delivery.getDeliveryManager().getId();
        UUID departureId = delivery.getDepartureId();
        UUID arrivalId = delivery.getArrivalId();

        // 권한 확인
        checkRole.validateRole(role, userId, username, departureId, arrivalId, deliveryManagerId, "수정");

        // 수령 업체 담당자 업데이트
        Long receiverId = request.getReceiverId() != null ? request.getReceiverId() : delivery.getReceiverId();
        String receiverSlackId = delivery.getReceiverSlackId();
        if (request.getReceiverId() != null) {
            UserDto user = userClient.getUserById(receiverId, userId, username, role).getBody().getData();
            receiverId = user.getUserId();
            receiverSlackId = user.getSlackId();
        }

        // 배송 상태
        DeliveryStatus deliveryStatus = request.getDeliveryStatus() != null ? DeliveryStatus.fromString(request.getDeliveryStatus()) : delivery.getDeliveryStatus();
        if (request.getDeliveryStatus() != null) {
            if (!delivery.getDeliveryStatus().nextStatus(deliveryStatus)) {
                throw new IllegalArgumentException("잘못된 상태 전환입니다.");
            }
        }

        // 배송 경로 상태 업데이트
        if (deliveryStatus == DeliveryStatus.IN_HUB_TRANSFER || deliveryStatus == DeliveryStatus.ARRIVED_AT_DESTINATION_HUB) {
            deliveryRouteService.updateRouteStatus(deliveryId, deliveryStatus);
        }

        // 배송 담당자 업데이트
        DeliveryManager currentManager = delivery.getDeliveryManager();
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
        deliveryManagerService.updateOrderId(newManager, delivery.getOrderId());

        // 배송 정보 업데이트
        delivery.update(receiverId, receiverSlackId, deliveryStatus);
        delivery.setUpdatedBy(username);
        return DeliveryDto.create(delivery);
    }

    @Transactional
    public void deleteDelivery(UUID deliveryId, String userId, String username, String role) throws AccessDeniedException {
        // 기존 데이터 조회
        Delivery delivery = deliveryRepository.findByIdAndIsDeleteFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송을 찾을 수 없습니다."));
        UUID departureId = delivery.getDepartureId();
        UUID arrivalId = delivery.getArrivalId();


        // 권한 확인
        checkRole.validateRole(role, userId, username, departureId, arrivalId, null, "삭제");

        delivery.delete(username);
    }

    @Transactional(readOnly = true)
    public DeliveryDto getDeliveryById(UUID deliveryId, String userId, String username, String role) throws AccessDeniedException {
        // 기존 데이터 조회
        Delivery delivery = deliveryRepository.findByIdAndIsDeleteFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송을 찾을 수 없습니다."));
        Long deliveryManagerId = delivery.getDeliveryManager().getId();
        UUID departureId = delivery.getDepartureId();
        UUID arrivalId = delivery.getArrivalId();

        // 권한 확인
        checkRole.validateRole(role, userId, username, departureId, arrivalId, deliveryManagerId, "조회");

        return DeliveryDto.create(delivery);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryDto> getDeliveries(String search, String deliveryStatus, UUID departureId, UUID arrivalId,
                                           Long deliveryManagerId, Long receiverId, String createdFrom, String createdTo,
                                           String userId, String username, String role, Pageable pageable) throws AccessDeniedException {

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

        return deliveryRepository.searchDeliveries(search, deliveryStatus, departureId, arrivalId, deliveryManagerId,
                receiverId, createdFrom, createdTo, pageable);
    }

}
