package com.msa_delivery.hub.application.dto.request;

import java.util.UUID;

public record HubRouteSearch(
        UUID hubRouteId,
        UUID arrivalHubId,
        UUID departureHubId,
        Boolean isDeleted
) {
}
