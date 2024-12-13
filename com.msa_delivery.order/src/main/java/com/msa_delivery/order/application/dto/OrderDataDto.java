package com.msa_delivery.order.application.dto;

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

    private UUID order_id;
    private UUID delivery_id;
    private String status;
    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
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
