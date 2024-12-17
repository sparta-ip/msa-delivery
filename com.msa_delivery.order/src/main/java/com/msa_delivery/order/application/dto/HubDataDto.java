package com.msa_delivery.order.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class HubDataDto {

    private UUID hub_id;
    private Long hub_manager_id;
    private String hub_name;
    private String address;

    @JsonProperty("latitude")
    private Double lat;

    @JsonProperty("longitude")
    private Double lng;

    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;
}
