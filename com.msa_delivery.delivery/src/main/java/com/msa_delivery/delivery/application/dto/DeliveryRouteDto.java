package com.msa_delivery.delivery.application.dto;

import com.msa_delivery.delivery.domain.model.DeliveryRoute;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryRouteDto extends BaseDto{
    private UUID deliveryRouteId;
    private UUID deliveryId;
    private Long deliveryManagerId;    // 허브 간 배송 담당자(HUB_DELIVERY_MANAGER)
    private Integer sequence;
    private UUID departureId;
    private UUID arrivalId;
    private Integer expectDistance;
    private Integer expectDuration;
    private Integer distance;
    private Integer duration;
    private String deliveryStatus;

    public static DeliveryRouteDto create(DeliveryRoute deliveryRoute) {
        DeliveryRouteDto dto = DeliveryRouteDto.builder()
                .deliveryRouteId(deliveryRoute.getId())
                .deliveryId(deliveryRoute.getDelivery().getId())
                .deliveryManagerId(deliveryRoute.getDeliveryManager().getId())
                .sequence(deliveryRoute.getSequence())
                .departureId(deliveryRoute.getDepartureId())
                .arrivalId(deliveryRoute.getArrivalId())
                .expectDistance(deliveryRoute.getExpectDistance())
                .expectDuration(deliveryRoute.getExpectDuration())
                .distance(deliveryRoute.getDistance())
                .duration(deliveryRoute.getDuration())
                .deliveryStatus(deliveryRoute.getDeliveryStatus().name())
                .build();
        dto.initializeBaseFields(deliveryRoute);
        return dto;
    }
}
