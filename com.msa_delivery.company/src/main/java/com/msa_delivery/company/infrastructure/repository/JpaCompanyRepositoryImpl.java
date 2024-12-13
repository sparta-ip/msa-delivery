package com.msa_delivery.company.infrastructure.repository;

import com.msa_delivery.company.application.dto.CompanyDto;
import com.msa_delivery.company.domain.model.Company;
import com.msa_delivery.company.domain.model.CompanyType;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.msa_delivery.company.domain.model.QCompany.company;

@RequiredArgsConstructor
public class JpaCompanyRepositoryImpl implements JpaCompanyRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CompanyDto> searchCompanies(String type, String search, Long managerId, UUID hubId, Pageable pageable) {
        // QueryDSL 실행
        QueryResults<Company> results = queryFactory.selectFrom(company)
                .where(
                        eqType(type),           // 타입 조건
                        nameOrAddressContains(search), // 검색 조건
                        eqManagerId(managerId), // 회사 담당자 조건
                        eqHubId(hubId),         // 허브 ID 조건
                        isDeleteFalse()         // 삭제되지 않은 데이터만
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        // 전체 카운트
        long totalCount = results.getTotal();

        // 검색 결과를 DTO로 변환
        List<CompanyDto> content = results.getResults().stream()
                .map(CompanyDto::create)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalCount);
    }

    // 타입 조건
    private BooleanExpression eqType(String type) {
        return type != null ? company.type.eq(CompanyType.fromString(type)) : null;
    }

    // 이름 또는 주소 조건
    private BooleanExpression nameOrAddressContains(String search) {
        if (search != null) {
            return company.name.containsIgnoreCase(search)
                    .or(company.address.containsIgnoreCase(search));
        }
        return null;
    }

    // 회사 담당자 조건
    private BooleanExpression eqManagerId(Long managerId) {
        return managerId != null ? company.managerId.eq(managerId) : null;
    }

    // 허브 ID 조건
    private BooleanExpression eqHubId(UUID hubId) {
        return hubId != null ? company.hubId.eq(hubId) : null;
    }

    // 삭제되지 않은 데이터 조건
    private BooleanExpression isDeleteFalse() {
        return company.isDelete.isFalse();
    }
}
