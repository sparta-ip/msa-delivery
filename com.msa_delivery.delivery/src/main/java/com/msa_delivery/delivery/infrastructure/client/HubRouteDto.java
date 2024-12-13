package com.msa_delivery.delivery.infrastructure.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HubRouteDto {
    private UUID hubRouteId;
    private Integer duration;
    private Integer distance;
}
