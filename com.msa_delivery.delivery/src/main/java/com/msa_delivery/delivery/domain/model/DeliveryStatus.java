package com.msa_delivery.delivery.domain.model;

public enum DeliveryStatus {
    WAITING_AT_HUB, // 허브 대기중
    IN_HUB_TRANSFER,    // 허브 이동중
    ARRIVED_AT_DESTINATION_HUB, // 목적지 허브 도착
    OUT_FOR_DELIVERY    // 배송중
}
