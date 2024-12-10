package com.msa_delivery.company.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_companies" , schema = "company")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID id;

    @Column(name = "company_manager_id")
    private Long managerId;

    @Column(nullable = false)
    private String slackId;

    @Column(nullable = false)
    private UUID hubId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompanyType type;

    public static Company create(Long managerId, String slackId, UUID hubId, String name,
                                 String address, CompanyType type) {
        return Company.builder()
                .managerId(managerId)
                .slackId(slackId)
                .hubId(hubId)
                .name(name)
                .address(address)
                .type(type)
                .build();
    }
}
