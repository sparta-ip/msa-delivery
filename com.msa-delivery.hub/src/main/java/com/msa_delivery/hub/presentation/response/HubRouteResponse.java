package com.msa_delivery.hub.presentation.response;


import com.msa_delivery.hub.domain.model.HubRoute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
public record HubRouteResponse(
        UUID hubRouteId,
        UUID departureHubId,
        UUID arrivalHubId,
        double distance,
        double duration
) {
    public static HubRouteResponse from(HubRoute hubRoute) {
        return HubRouteResponse.builder()
                .hubRouteId(hubRoute.getHubRouteId())
                .departureHubId(hubRoute.getDepartureHub().getHubId())
                .arrivalHubId(hubRoute.getArrivalHub().getHubId())
                .distance(hubRoute.getDistance())
                .duration(hubRoute.getDuration())
                .build();
    }
}