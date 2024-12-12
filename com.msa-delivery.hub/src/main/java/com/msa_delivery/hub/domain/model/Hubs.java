package com.msa_delivery.hub.domain.model;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "p_hubs", schema = "hub_service")
public class Hubs {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID hubId;
    
    @Column(name = "hub_manager_id")
    private Long hubManagerId;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;

    @Embedded
    private Location location;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "deleted_by")
    private String deletedBy;
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void updateHubData(CreateHubReqDto reqDto, String address, Location location) {
        this.name = reqDto.getHub().getName();
        this.address = address;
        this.location = location;
    }
}