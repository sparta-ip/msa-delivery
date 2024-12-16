package com.msa_delivery.order.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.msa_delivery.order.domain.model.Order;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDataDto {

    @JsonProperty("orderId")
    private UUID order_id;

    @JsonProperty("deliveryId")
    private UUID delivery_id;

    private String status;

    @JsonProperty("createdAt")
    private LocalDateTime created_at;

    @JsonProperty("createdBy")
    private String created_by;

    @JsonProperty("updatedAt")
    private LocalDateTime updated_at;

    @JsonProperty("updatedBy")
    private String updated_by;

    public OrderDataDto(Order order) {
        this.order_id = order.getOrder_id();
        this.delivery_id = order.getDelivery_id();
        this.status = order.getStatus();
        this.created_at = order.getCreated_at();
        this.created_by = order.getCreated_by();
        this.updated_at = order.getUpdated_at() != null ? order.getUpdated_at() : null;
        this.updated_by = order.getUpdated_by();
    }
}
