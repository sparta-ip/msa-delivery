package com.msa_delivery.hub.infrastructure.kakao.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Summary(
    @JsonProperty("distance")
    int distance,
    
    @JsonProperty("duration")
    int duration
) {}