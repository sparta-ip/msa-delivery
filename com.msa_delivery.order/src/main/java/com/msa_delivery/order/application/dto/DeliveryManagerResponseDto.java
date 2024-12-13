package com.msa_delivery.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class DeliveryManagerResponseDto {

    private Long delivery_manager_id;
    private UUID order_id;
    private String slack_id;
    private UUID hub_id;
    private String type;
    private Integer sequence;
    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;
}
