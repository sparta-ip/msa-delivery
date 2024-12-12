package com.msa_delivery.delivery.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_deliveries" , schema = "delivery")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    private UUID id;

    private UUID orderId;

    @ManyToOne
    @JoinColumn(name = "delivery_manager_id")
    private DeliveryManager deliveryManager;    // 업체 배송 담당자 (COMPANY_DELIVERY_MANAGER)

    private Long receiverId;    // 수령인 (수령 업체의 업체 담당자)
    private String receiverSlackId; // 수령인 슬랙 ID (수령 업체의 업체 담당자 슬랙 ID)

    private UUID departureId;
    private UUID arrivalId;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    private String address;

    @OneToOne(mappedBy = "delivery")
    private DeliveryRoute deliveryRoute;

    public static Delivery create(UUID orderId, DeliveryManager deliveryManager, Long receiverId, String receiverSlackId, UUID departureId, UUID arrivalId, DeliveryStatus deliveryStatus, String address) {
        return Delivery.builder()
                .orderId(orderId)
                .deliveryManager(deliveryManager)
                .receiverId(receiverId)
                .receiverSlackId(receiverSlackId)
                .departureId(departureId)
                .arrivalId(arrivalId)
                .deliveryStatus(deliveryStatus)
                .address(address)
                .build();
    }
}
