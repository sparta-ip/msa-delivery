package com.msa_delivery.user.infrastructure.client;

import com.msa_delivery.user.application.dtos.ApiResponseDto;
import com.msa_delivery.user.application.service.CompanyService;
import com.msa_delivery.user.infrastructure.dtos.GetUUIDDto;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient extends CompanyService {
    @GetMapping("/api/companies")
    ResponseEntity<ApiResponseDto<GetUUIDDto>> getCompanyByUserId(@RequestParam(name = "company_manager_id") Long userId,
                                                                  @RequestHeader(value = "X-User_Id", required = true) @NotBlank String headerUserId,
                                                                  @RequestHeader(value = "X-Username", required = true) @NotBlank String username,
                                                                  @RequestHeader(value = "X-Role", required = true) @NotBlank String role);

    @DeleteMapping("/api/companies/{company_id}")
    ResponseEntity<ApiResponseDto<?>> softDeleteCompany(@PathVariable(name = "company_id") UUID companyId);
}
