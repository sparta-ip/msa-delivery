package com.msa_delivery.company.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HubDto {
    @JsonProperty("hubId")
    private UUID hubId;
    @JsonProperty("hubManagerId")
    private Long hubManagerId;
}
