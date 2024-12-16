package com.msa_delivery.order.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class ProductDataDto {

    @JsonProperty("productId")
    private UUID product_id;

    @JsonProperty("companyId")
    private UUID company_id;

    @JsonProperty("hubId")
    private UUID hub_id;

    private String name;
    private Integer price;
    private Integer quantity;

    @JsonProperty("createdAt")
    private LocalDateTime created_at;

    @JsonProperty("createdBy")
    private String created_by;

    @JsonProperty("updatedAt")
    private LocalDateTime updated_at;

    @JsonProperty("updatedBy")
    private String updated_by;
}
