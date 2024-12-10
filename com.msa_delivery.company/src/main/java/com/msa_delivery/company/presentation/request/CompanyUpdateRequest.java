package com.msa_delivery.company.presentation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyUpdateRequest {
    private Long managerId;
    private String name;
    private String address;
    private String type;
}
