package com.msa_delivery.company.presentation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {
    private UUID companyId;
    private String name;
    private Integer price;
    private Integer quantity;
}
