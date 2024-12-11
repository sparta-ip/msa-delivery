package com.msa_delivery.delivery.application.dto;

import com.msa_delivery.delivery.domain.model.Delivery;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryDto extends BaseDto{
    private UUID deliveryId;
    private UUID orderId;
    private Long deliveryManagerId;    // 업체 배송 담당자 (COMPANY_DELIVERY_MANAGER)
    private Long receiverId;    // 수령인 (수령 업체의 업체 담당자)
    private String receiverSlackId; // 수령인 슬랙 ID (수령 업체의 업체 담당자 슬랙 ID)
    private UUID departureId;
    private UUID arrivalId;
    private String deliveryStatus;
    private String address;

    public static DeliveryDto create(Delivery delivery) {
        DeliveryDto dto = DeliveryDto.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .deliveryManagerId(delivery.getDeliveryManager().getId())
                .receiverId(delivery.getReceiverId())
                .receiverSlackId(delivery.getReceiverSlackId())
                .departureId(delivery.getDepartureId())
                .arrivalId(delivery.getArrivalId())
                .deliveryStatus(delivery.getDeliveryStatus().name())
                .address(delivery.getAddress())
                .build();
        dto.initializeBaseFields(delivery);
        return dto;
    }
}
