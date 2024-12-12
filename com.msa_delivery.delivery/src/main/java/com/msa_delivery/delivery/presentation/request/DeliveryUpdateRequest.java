package com.msa_delivery.delivery.presentation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeliveryUpdateRequest {
    // 배송이 생성되면, 출도착 허브와 배송 주소 변경 불가
    // 슬랙 ID 만 변경 불가
    private Long receiverId;
    private String deliveryStatus;
}
