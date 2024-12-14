package com.msa_delivery.delivery.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HubRouteDto {
    @JsonProperty("hubRouteId")
    private UUID hubRouteId;
    @JsonProperty("duration")
    private Integer duration;
    @JsonProperty("distance")
    private Integer distance;
}
