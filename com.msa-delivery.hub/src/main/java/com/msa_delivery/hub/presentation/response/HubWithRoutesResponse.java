package com.msa_delivery.hub.presentation.response;

import java.util.List;

public record HubWithRoutesResponse(
    HubRes hub,
    List<HubRouteResponse> updatedRoutes
) {
    public static HubWithRoutesResponse of(HubRes hub, List<HubRouteResponse> routes) {
        return new HubWithRoutesResponse(hub, routes);
    }
}