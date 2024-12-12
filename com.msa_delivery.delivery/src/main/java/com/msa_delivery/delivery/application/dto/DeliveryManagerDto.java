package com.msa_delivery.delivery.application.dto;

import com.msa_delivery.delivery.domain.model.DeliveryManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryManagerDto extends BaseDto{
    private Long deliveryMangerId;
    private UUID orderId;
    private String slackId;
    private UUID hubId;
    private String type;
    private Integer sequence;

    public static DeliveryManagerDto create(DeliveryManager deliveryManager) {
        DeliveryManagerDto dto = DeliveryManagerDto.builder()
                .deliveryMangerId(deliveryManager.getId())
                .orderId(deliveryManager.getOrderId())
                .slackId(deliveryManager.getSlackId())
                .hubId(deliveryManager.getHubId())
                .type(deliveryManager.getType().name())
                .sequence(deliveryManager.getSequence())
                .build();
        dto.initializeBaseFields(deliveryManager);
        return dto;
    }
}
