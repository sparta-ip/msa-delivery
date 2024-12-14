package com.msa_delivery.company.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {
    @JsonProperty("company_id")
    private UUID companyId;
    private String name;
    private Integer price;
    private Integer quantity;
}
