package com.msa_delivery.company.infrastructure.repository;

import com.msa_delivery.company.domain.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaCompanyRepository {
    Page<Company> searchCompanies(String type, String search, String sortBy, String direction, Pageable pageable);
}
