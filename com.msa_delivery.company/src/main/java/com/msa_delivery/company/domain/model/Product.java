package com.msa_delivery.company.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_products" , schema = "company")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private UUID hubId;

    private String name;

    private Integer price;

    private Integer quantity;

    public static Product create(Company company, UUID hubId, String name, Integer price, Integer quantity) {
        return Product.builder()
                .company(company)
                .hubId(hubId)
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
    }

    public void update(Company company, UUID hubId, String name, Integer price, Integer quantity) {
        this.company = company;
        this.hubId = hubId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
