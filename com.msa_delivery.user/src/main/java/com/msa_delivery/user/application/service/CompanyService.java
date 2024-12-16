package com.msa_delivery.user.application.service;

import com.msa_delivery.user.application.dtos.ApiResponseDto;
import com.msa_delivery.user.infrastructure.dtos.GetUUIDDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface CompanyService {
    ResponseEntity<ApiResponseDto<GetUUIDDto>> getCompanyByUserId(Long userId, String headerUserId, String username, String role);

    ResponseEntity<ApiResponseDto<?>> softDeleteCompany(UUID companyId);
}
