package com.msa_delivery.user.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUUIDDto {

    @JsonProperty("hub_id")
    private List<UUID> hubId;

    @JsonProperty("delivery_id")
    private List<UUID> deliveryId;

    @JsonProperty("company_id")
    private List<UUID> companyId;
}
