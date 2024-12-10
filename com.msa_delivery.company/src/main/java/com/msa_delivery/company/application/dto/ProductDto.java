package com.msa_delivery.company.application.dto;

import com.msa_delivery.company.domain.model.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductDto extends BaseDto {
    private UUID productId;
    private UUID companyId;
    private UUID hudId;
    private String name;
    private Integer price;
    private Integer quantity;

    public static ProductDto create(Product product) {
        ProductDto dto = ProductDto.builder()
                .productId(product.getId())
                .companyId(product.getCompany().getId())
                .hudId(product.getHudId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
        dto.initializeBaseFields(product);
        return dto;
    }
}
