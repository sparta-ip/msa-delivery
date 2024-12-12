package com.msa_delivery.delivery.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_routes" , schema = "delivery")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRoute extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_route_id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "delivery_manager_id")
    private DeliveryManager deliveryManager;    // 허브 간 배송 담당자(HUB_DELIVERY_MANAGER)

    private Integer sequence;

    private UUID departureId;
    private UUID arrivalId;

    private Integer expectDistance;
    private Integer expectDuration;
    private Integer distance;
    private Integer duration;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    public static DeliveryRoute create(Delivery delivery, DeliveryManager deliveryManager, Integer sequence, UUID departureId, UUID arrivalId, Integer expectDistance, Integer expectDuration, Integer distance, Integer duration, DeliveryStatus deliveryStatus) {
        return DeliveryRoute.builder()
                .delivery(delivery)
                .deliveryManager(deliveryManager)
                .sequence(sequence)
                .departureId(departureId)
                .arrivalId(arrivalId)
                .expectDistance(expectDistance)
                .expectDuration(expectDuration)
                .distance(distance)
                .duration(duration)
                .deliveryStatus(deliveryStatus)
                .build();
    }

    public void updateStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
