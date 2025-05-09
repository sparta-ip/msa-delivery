package com.msa_delivery.company.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.msa_delivery.company.domain.model.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public class BaseDto {

    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String deletedBy;
    private LocalDateTime deletedAt;

    public void initializeBaseFields(BaseEntity baseEntity) {
        this.createdBy = baseEntity.getCreatedBy();
        this.createdAt = baseEntity.getCreatedAt();
        this.updatedBy = baseEntity.getUpdatedBy();
        this.updatedAt = baseEntity.getUpdatedAt();
        this.deletedBy = baseEntity.getDeletedBy();
        this.deletedAt = baseEntity.getDeletedAt();
    }
}
