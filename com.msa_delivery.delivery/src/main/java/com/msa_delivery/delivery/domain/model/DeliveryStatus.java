package com.msa_delivery.delivery.domain.model;

import java.util.Arrays;

public enum DeliveryStatus {
    WAITING_AT_HUB, // 허브 대기중
    IN_HUB_TRANSFER,    // 목적지 허브로 이동중
    ARRIVED_AT_DESTINATION_HUB, // 목적지 허브 도착 = 허브 간 배송 완료
    OUT_FOR_DELIVERY,    // 업체로 배송중
    COMPLETE;    // 배송 완료

    public static DeliveryStatus fromString(String type) {
        return Arrays.stream(DeliveryStatus.values())
                .filter(enumValue -> enumValue.name().equalsIgnoreCase(type))   // 대소문자 구분 X
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 상태가 존재하지 않습니다.: " + type));
    }

    // 상태 순차적으로 전환
    public boolean nextStatus(DeliveryStatus newStatus) {
        switch (this) {
            case WAITING_AT_HUB:
                return newStatus == IN_HUB_TRANSFER;
            case IN_HUB_TRANSFER:
                return newStatus == ARRIVED_AT_DESTINATION_HUB;
            case ARRIVED_AT_DESTINATION_HUB:
                return newStatus == OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return newStatus == COMPLETE;
            default:
                return false;
        }
    }
}
