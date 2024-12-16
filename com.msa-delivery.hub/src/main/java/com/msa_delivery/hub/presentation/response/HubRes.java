package com.msa_delivery.hub.presentation.response;

import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Builder
public record HubRes(UUID hubId, String hubName, String address, Location location) {


    public static HubRes from(Hubs hubs) {
        return HubRes.builder()
                .hubId(hubs.getHubId())
                .hubName(hubs.getName())
                .address(hubs.getAddress())
                .location(hubs.getLocation())
                .build();
    }
}
