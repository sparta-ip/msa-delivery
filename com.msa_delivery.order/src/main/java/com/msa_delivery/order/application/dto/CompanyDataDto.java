package com.msa_delivery.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CompanyDataDto {

    private UUID company_id;
    private Long company_manager_id;
    private String slack_id;
    private UUID hub_id;
    private String name;
    private String address;
    private String type;
    private LocalDateTime created_at;
    private String created_by;
    private LocalDateTime updated_at;
    private String updated_by;
}
