package com.msa_delivery.company.application.dto;

import com.msa_delivery.company.domain.model.Company;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyDto extends BaseDto {
    private UUID companyId;
    private Long managerId;
    private String slackId;
    private UUID hubId;
    private String name;
    private String address;
    private String type;

    public static CompanyDto create(Company company) {
        CompanyDto dto = CompanyDto.builder()
                .companyId(company.getId())
                .managerId(company.getManagerId())
                .slackId(company.getSlackId())
                .hubId(company.getHubId())
                .name(company.getName())
                .address(company.getAddress())
                .type(company.getType().name())
                .build();
        dto.initializeBaseFields(company);
        return dto;
    }
}
