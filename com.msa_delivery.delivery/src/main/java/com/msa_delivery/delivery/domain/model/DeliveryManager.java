package com.msa_delivery.delivery.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_delivery_managers" , schema = "delivery")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryManager extends BaseEntity{
    @Id
    @Column(name = "delivery_manager_id")
    private Long id;

    private UUID orderId;
    private String slackId;
    private UUID hubId;

    @Enumerated(EnumType.STRING)
    private DeliveryManagerType type;

    private Integer sequence;

    @Builder.Default
    @OneToMany(mappedBy = "deliveryManager", fetch = FetchType.LAZY)
    private List<DeliveryRoute> deliveryRoutes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "deliveryManager", fetch = FetchType.LAZY)
    private List<Delivery> deliveries = new ArrayList<>();

    public static DeliveryManager create(Long id, UUID orderId, String slackId, UUID hubId, DeliveryManagerType type, Integer sequence) {
        return DeliveryManager.builder()
                .id(id)
                .orderId(orderId)
                .slackId(slackId)
                .hubId(hubId)
                .type(type)
                .sequence(sequence)
                .build();
    }

    public void update(DeliveryManagerType type, UUID hubId, Integer sequence) {
        this.type = type;
        this.hubId = hubId;
        this.sequence = sequence;
    }

    public void updateOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void updateSequence(int sequence) {
        this.sequence = sequence;
    }
}
