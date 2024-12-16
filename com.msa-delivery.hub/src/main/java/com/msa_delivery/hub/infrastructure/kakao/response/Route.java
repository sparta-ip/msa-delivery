package com.msa_delivery.hub.infrastructure.kakao.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Route(
    @JsonProperty("result_code")
    int resultCode,
    
    @JsonProperty("result_msg")
    String resultMessage,
    
    @JsonProperty("summary")
    Summary summary


) {}
