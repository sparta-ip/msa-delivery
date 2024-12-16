package com.msa_delivery.hub.infrastructure.kakao.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoDirectionsResponse(
    @JsonProperty("routes")
    List<Route> routes
){

}