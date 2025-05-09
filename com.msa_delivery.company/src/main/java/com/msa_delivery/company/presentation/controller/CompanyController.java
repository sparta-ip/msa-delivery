package com.msa_delivery.company.presentation.controller;

import com.msa_delivery.company.application.dto.CommonResponse;
import com.msa_delivery.company.application.dto.CompanyDto;
import com.msa_delivery.company.application.service.CompanyService;
import com.msa_delivery.company.presentation.request.CompanyRequest;
import com.msa_delivery.company.presentation.request.CompanyUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<?> createCompany(@Valid @RequestBody CompanyRequest request,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) throws AccessDeniedException {
        CompanyDto company = companyService.createCompany(request, userId, username, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(HttpStatus.CREATED.value(), "업체 생성이 완료되었습니다.", company)
        );
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<?> updateCompany(@PathVariable UUID companyId,
                                           @RequestBody CompanyUpdateRequest request,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) throws AccessDeniedException {
        CompanyDto company = companyService.updateCompany(companyId, request, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "업체 수정이 완료되었습니다.", company)
        );
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<?> deleteCompany(@PathVariable UUID companyId,
                                           @RequestHeader("X-User_Id") String userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) throws AccessDeniedException {
        companyService.deleteCompany(companyId, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "업체 삭제가 완료되었습니다.", null)
        );
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<?> getCompanyById(@PathVariable UUID companyId,
                                            @RequestHeader("X-User_Id") String userId,
                                            @RequestHeader("X-Username") String username,
                                            @RequestHeader("X-Role") String role) {
        CompanyDto company = companyService.getCompanyById(companyId, userId, username, role);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "업체 상세 조회가 완료되었습니다.", company)
        );
    }

    @GetMapping
    public ResponseEntity<?> getCompanies(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "company_manager_id", required = false) Long managerId,
            @RequestParam(value = "hub_id", required = false) UUID hubId,
            @RequestHeader("X-User_Id") String userId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Role") String role,
            Pageable pageable) {
        Page<CompanyDto> companies = companyService.getCompanies(type, search, managerId, hubId, userId, username, role, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonResponse.success(HttpStatus.OK.value(), "검색 조회가 완료되었습니다.", companies)
        );
    }

}
