package com.msa_delivery.delivery.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class DeliveryManagerUpdateRequest {
    // 슬랙 ID, 주문 ID 직접 변경 불가
    private String type;

    @JsonProperty("hub_id")
    private UUID hubId;

    private Integer sequence;
}
