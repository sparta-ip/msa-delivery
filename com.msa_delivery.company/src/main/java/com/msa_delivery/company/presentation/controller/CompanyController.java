package com.msa_delivery.company.presentation.controller;

import com.msa_delivery.company.application.dto.CommonResponse;
import com.msa_delivery.company.application.dto.CompanyDto;
import com.msa_delivery.company.application.service.CompanyService;
import com.msa_delivery.company.presentation.request.CompanyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<?> createCompany(@Valid @RequestBody CompanyRequest request,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role,
                                           @RequestHeader("X-Hub-Id") UUID hubId) {
        try {
            CompanyDto company = companyService.createCompany(request, username, role, hubId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "업체 생성이 완료되었습니다.", company)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, "권한이 없습니다.")
            );
        }
    }


}
