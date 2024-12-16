package com.msa_delivery.hub.domain.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_hub_routes", schema = "hub_service")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hub_route_id")
    private UUID hubRouteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_id")
    private Hubs departureHub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_id")
    private Hubs arrivalHub;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void updateRouteInfo(Integer distance, Integer duration, Long updatedBy) {
        this.distance = distance;
        this.duration = duration;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }
}
