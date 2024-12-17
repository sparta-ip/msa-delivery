package com.msa_delivery.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class ProductDataDto {

    private UUID product_id;
    private UUID company_id;
    private UUID hub_id;
    private String name;
    private Integer price;
    private Integer quantity;
    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;
}
