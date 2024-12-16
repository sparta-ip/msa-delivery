package com.msa_delivery.company.domain.repository;

import com.msa_delivery.company.domain.model.Company;
import com.msa_delivery.company.infrastructure.repository.JpaCompanyRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID>, JpaCompanyRepository {
    Optional<Company> findByIdAndIsDeleteFalse(UUID companyId);
}
