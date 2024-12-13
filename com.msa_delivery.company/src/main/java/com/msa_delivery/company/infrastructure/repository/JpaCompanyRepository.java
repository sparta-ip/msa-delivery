package com.msa_delivery.company.infrastructure.repository;

import com.msa_delivery.company.application.dto.CompanyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JpaCompanyRepository {
    Page<CompanyDto> searchCompanies(String type, String search, Long managerId, UUID hubId, Pageable pageable);
}

