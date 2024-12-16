package com.msa_delivery.order.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class HubDataDto {

//    @JsonProperty("hubId")
    private UUID hub_id;

//    @JsonProperty("hubManagerId")
    private Long hub_manager_id;

    private String name;
    private String address;

//    @JsonProperty("latitude")
    private Double lat;

//    @JsonProperty("longitude")
    private Double lng;

//    @JsonProperty("createdAt")
    private LocalDateTime created_at;

//    @JsonProperty("createdBy")
    private String created_by;

//    @JsonProperty("updatedAt")
    private LocalDateTime updated_at;

//    @JsonProperty("updatedBy")
    private String updated_by;
}
