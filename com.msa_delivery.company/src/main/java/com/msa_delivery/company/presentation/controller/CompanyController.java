package com.msa_delivery.company.presentation.controller;

import com.msa_delivery.company.application.dto.CommonResponse;
import com.msa_delivery.company.application.dto.CompanyDto;
import com.msa_delivery.company.application.service.CompanyService;
import com.msa_delivery.company.presentation.request.CompanyRequest;
import com.msa_delivery.company.presentation.request.CompanyUpdateRequest;
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
                                           @RequestHeader("X-User_Id") Long userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        try {
            CompanyDto company = companyService.createCompany(request, userId, username, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    CommonResponse.success(HttpStatus.CREATED, "업체 생성이 완료되었습니다.", company)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "업체 생성 중 오류가 발생했습니다.")
            );
        }
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<?> updateCompany(@PathVariable UUID companyId,
                                           @RequestBody CompanyUpdateRequest request,
                                           @RequestHeader("X-User_Id") Long userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        try {
            CompanyDto company = companyService.updateCompany(companyId, request, userId, username, role);
            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "업체 수정이 완료되었습니다.", company)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "업체 수정 중 오류가 발생했습니다.")
            );
        }
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<?> deleteCompany(@PathVariable UUID companyId,
                                           @RequestHeader("X-User_Id") Long userId,
                                           @RequestHeader("X-Username") String username,
                                           @RequestHeader("X-Role") String role) {
        try {
            companyService.deleteCompany(companyId, userId, username, role);
            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "업체 삭제가 완료되었습니다.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    CommonResponse.error(HttpStatus.FORBIDDEN, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "업체 삭제 중 오류가 발생했습니다.")
            );
        }
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<?> getCompanyById(@PathVariable UUID companyId) {
        try {
            CompanyDto company = companyService.getCompanyById(companyId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    CommonResponse.success(HttpStatus.OK, "업체 상세 조회가 완료되었습니다.", company)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    CommonResponse.error(HttpStatus.NOT_FOUND, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "업체 조회 중 오류가 발생했습니다.")
            );
        }
    }

}
