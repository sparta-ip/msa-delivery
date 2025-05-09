package com.msa_delivery.hub.application.dto.request;

import java.util.UUID;

public record HubSearch(
        UUID hubId,
        String name,
        String address,
        Long hubManagerId,
        Boolean isDeleted
) {
}
