package com.msa_delivery.hub.presentation.response;

import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
public record HubRes(UUID hubId, Long hubMangerId, String hubName, String address, Location location, LocalDateTime createdAt, String createdBy, LocalDateTime updatedAt, String updatedBy) {


    public static HubRes from(Hubs hubs) {
        return HubRes.builder()
                .hubId(hubs.getHubId())
                .hubMangerId(hubs.getHubManagerId())
                .hubName(hubs.getName())
                .address(hubs.getAddress())
                .location(hubs.getLocation())
                .createdAt(hubs.getCreatedAt())
                .createdBy(hubs.getCreatedBy())
                .updatedAt(hubs.getUpdatedAt())
                .updatedBy(hubs.getUpdatedBy())
                .build();
    }
}
