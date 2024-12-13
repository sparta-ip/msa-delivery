package com.msa_delivery.company.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompanyUpdateRequest {
    @JsonProperty("manager_id")
    private Long managerId;
    private String name;
    private String address;
    private String type;
}
