package com.msa_delivery.company.infrastructure.repository;

import com.msa_delivery.company.application.dto.CompanyDto;
import com.msa_delivery.company.domain.model.Company;
import com.msa_delivery.company.domain.model.CompanyType;
import com.msa_delivery.company.domain.model.QCompany;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CompanyRepositoryImpl implements JpaCompanyRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CompanyDto> searchCompanies(String type, String search, String sortBy, String direction, Pageable pageable) {
        QCompany company = QCompany.company;

        // 동적 검색 조건
        BooleanExpression condition = buildCondition(type, search);

        // 동적 정렬
        OrderSpecifier<?> orderSpecifier = buildOrderSpecifier(sortBy, direction, company);

        // QueryDSL 실행
        List<Company> results = queryFactory.selectFrom(company)
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트
        long totalCount = queryFactory.selectFrom(company)
                .where(condition)
                .fetchCount();

        // 검색 결과를 DTO로 변환
        List<CompanyDto> content = results.stream()
                .map(CompanyDto::create)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression buildCondition(String type, String search) {
        QCompany company = QCompany.company;

        BooleanExpression condition = company.isDelete.isFalse(); // 삭제되지 않은 엔티티만 검색

        if (type != null) {
            condition = condition.and(company.type.eq(CompanyType.valueOf(type)));
        }

        if (search != null) {
            condition = condition.and(
                    company.name.containsIgnoreCase(search) // 이름 검색
                            .or(company.managerId.stringValue().containsIgnoreCase(search)) // 담당자 ID 검색
                            .or(Expressions.stringTemplate("cast({0} as string)", company.hubId).containsIgnoreCase(search))    // 허브 ID 검색
                            .or(company.address.containsIgnoreCase(search)) // 주소 검색
            );
        }

        return condition;
    }

    private OrderSpecifier<?> buildOrderSpecifier(String sortBy, String direction, QCompany company) {
        // 기본 정렬 필드와 방향
        String defaultSortBy = "createdAt";
        Order defaultOrder = Order.ASC;

        if (sortBy == null) {
            sortBy = defaultSortBy;
        }

        Order order = "desc".equalsIgnoreCase(direction) ? Order.DESC : defaultOrder;

        switch (sortBy) {
            case "name":
                return new OrderSpecifier<>(order, company.name);
            case "managerId":
                return new OrderSpecifier<>(order, company.managerId);
            case "hubId":
                return new OrderSpecifier<>(order, company.hubId);
            case "address":
                return new OrderSpecifier<>(order, company.address);
            case "createdAt":
            default:
                return new OrderSpecifier<>(order, company.createdAt);
        }
    }
}
