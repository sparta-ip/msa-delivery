package com.msa_delivery.delivery.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeliveryRouteRequest {
    // 배송이 생성되면, 출도착 허브, 예상 거리, 예상 소요시간 변경 불가
    // 배송 경로 상태는 배송에서 관리
    @JsonProperty("delivery_manager_id")
    private Long deliveryManagerId;
    private Integer distance;
    private Integer duration;

    @JsonProperty("delivery_status")
    private String deliveryStatus;

}
