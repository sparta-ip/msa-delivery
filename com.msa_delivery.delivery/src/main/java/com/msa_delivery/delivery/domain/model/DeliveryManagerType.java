package com.msa_delivery.delivery.domain.model;

import java.util.Arrays;

public enum DeliveryManagerType {
    HUB_DELIVERY_MANAGER,   // 허브 배송 담당자
    COMPANY_DELIVERY_MANAGER;    // 업체 배송 담당자

    public static DeliveryManagerType fromString(String type) {
        return Arrays.stream(DeliveryManagerType.values())
                .filter(enumValue -> enumValue.name().equalsIgnoreCase(type))   // 대소문자 구분 X
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유형이 존재하지 않습니다.: " + type));
    }
}
