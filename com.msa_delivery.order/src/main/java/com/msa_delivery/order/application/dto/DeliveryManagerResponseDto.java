package com.msa_delivery.order.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class DeliveryManagerResponseDto {

    @JsonProperty("deliveryManagerId")
    private Long delivery_manager_id;

    @JsonProperty("orderId")
    private UUID order_id;

    @JsonProperty("slackId")
    private String slack_id;

    @JsonProperty("hubId")
    private UUID hub_id;

    private String type;
    private Integer sequence;

    @JsonProperty("createdAt")
    private LocalDateTime created_at;

    @JsonProperty("createdBy")
    private String created_by;

    @JsonProperty("updatedAt")
    private LocalDateTime updated_at;

    @JsonProperty("updatedBy")
    private String updated_by;
}
