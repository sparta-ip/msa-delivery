package com.msa_delivery.delivery.application.service;

import com.msa_delivery.delivery.application.dto.DeliveryCreateDto;
import com.msa_delivery.delivery.domain.model.*;
import com.msa_delivery.delivery.domain.repository.DeliveryManagerRepository;
import com.msa_delivery.delivery.domain.repository.DeliveryRepository;
import com.msa_delivery.delivery.domain.repository.DeliveryRouteRepository;
import com.msa_delivery.delivery.infrastructure.client.HubRouteClient;
import com.msa_delivery.delivery.infrastructure.client.HubRouteDto;
import com.msa_delivery.delivery.presentation.request.DeliveryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryManagerService deliveryManagerService;
    private final HubRouteClient hubRouteClient;

    public DeliveryCreateDto createDelivery(DeliveryRequest request, String userId, String username, String role) {
        // TODO: 배송 권한 검증
        // 배송 생성
        Delivery delivery = createDeliveryEntity(request, username);

        // 배송 경로 생성
        DeliveryRoute deliveryRoute = createDeliveryRouteEntity(request, delivery, username);

        return DeliveryCreateDto.create(delivery, deliveryRoute);
    }

    // 배송 생성 로직 분리
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
                .findFirstByDeliveryManagerTypeAndOrderIdIsNullAndIsDeleteFalseOrderBySequenceAsc(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)
                .orElseThrow(() -> new IllegalArgumentException("지정 할 수 있는 배송 담당자가 없습니다."));
        deliveryManagerService.updateOrderId(companyDeliveryManager, orderId);

        // 배송 생성
        Delivery delivery = Delivery.create(orderId, companyDeliveryManager, receiverId, receiverSlackId, departureId, arrivalId, deliveryStatus, address);
        deliveryRepository.save(delivery);
        delivery.setCreatedBy(username);
        return delivery;
    }

    // 배송 경로 생성 로직 분리
    private DeliveryRoute createDeliveryRouteEntity(DeliveryRequest request, Delivery delivery, String username) {
        UUID departureId = request.getDepartureId();
        UUID arrivalId = request.getArrivalId();
        DeliveryStatus deliveryStatus = DeliveryStatus.WAITING_AT_HUB;

        // 허브 간 배송 담당자 순차 배정
        // 배송 담당자 타입이 허브 배송 담당자이고 orderId 가 null 인 값 중에 sequence 가 가장 작은 값 (isDeleteIsFalse)
        DeliveryManager hubDeliveryManager = deliveryManagerRepository.
                findFirstByDeliveryManagerTypeAndOrderIdIsNullAndIsDeleteFalseOrderBySequenceAsc(DeliveryManagerType.HUB_DELIVERY_MANAGER)
                .orElseThrow(() -> new IllegalArgumentException("지정 할 수 있는 배송 담당자가 없습니다."));
        deliveryManagerService.updateOrderId(hubDeliveryManager, delivery.getOrderId());

        // 예상 거리, 예상 소요시간은 HubRouteClient 호출
        HubRouteDto hubRouteDto = hubRouteClient.getHubRoute(departureId, arrivalId);
        Integer expectDistance = hubRouteDto.getDuration();
        Integer expectDuration = hubRouteDto.getDistance();

        // TODO: 시퀀스, 실제 거리, 실제 소요 시간
        // sequence: P2P 면 sequence 는 항상 1 (의미 없음)
        // 실제 거리, 실제 소요시간은 계산해야함 -> 배송 완료(COMPLETE) 되면 업데이트 되게 해야 할 듯 -> 처음엔 null?

        // 배송 경로 생성
        DeliveryRoute deliveryRoute = DeliveryRoute.create(delivery, hubDeliveryManager, 1, departureId, arrivalId, expectDistance, expectDuration, null, null, deliveryStatus);
        deliveryRouteRepository.save(deliveryRoute);
        deliveryRoute.setCreatedBy(username);
        return deliveryRoute;
    }
}
