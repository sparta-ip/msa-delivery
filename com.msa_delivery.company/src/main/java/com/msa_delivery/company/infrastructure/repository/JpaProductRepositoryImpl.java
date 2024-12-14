package com.msa_delivery.company.infrastructure.repository;

import com.msa_delivery.company.application.dto.ProductDto;
import com.msa_delivery.company.domain.model.Product;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.msa_delivery.company.domain.model.QProduct.product;

@RequiredArgsConstructor
public class JpaProductRepositoryImpl implements JpaProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductDto> searchProducts(String search, Integer minPrice, Integer maxPrice, Integer minQuantity, Integer maxQuantity,
                                           Pageable pageable, UUID hubId) {

        // 동적 정렬
        List<OrderSpecifier<?>> orderSpecifier = buildOrderSpecifier(pageable);

        // QueryDSL 실행
        QueryResults<Product> results = queryFactory.selectFrom(product)
                .where(nameContains(search),    // 이름 포함 조건
                        eqHubId(hubId), // 허브 ID 확인
                        priceBetween(minPrice, maxPrice), // 가격 범위 조건
                        quantityBetween(minQuantity, maxQuantity), // 수량 범위 조건
                        isDeleteFalse()
                )
                .orderBy(orderSpecifier.toArray(new OrderSpecifier[0])) // 정렬 조건 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        // 전체 카운트
        long totalCount = results.getTotal();

        // 검색 결과를 DTO로 변환
        List<ProductDto> content = results.getResults().stream()
                .map(ProductDto::create)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalCount);

    }
    // 허브 ID 확인
    private BooleanExpression eqHubId(UUID hubId) {
        return hubId != null ? product.hubId.eq(hubId) : null;
    }

    // 이름 포함 조건
    private BooleanExpression nameContains(String name) {
        return name != null ? product.name.containsIgnoreCase(name) : null;
    }

    // 가격 범위 조건
    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return product.price.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return product.price.goe(minPrice);
        } else if (maxPrice != null) {
            return product.price.loe(maxPrice);
        } else {
            return null;
        }
    }

    // 수량 범위 조건
    private BooleanExpression quantityBetween(Integer minQuantity, Integer maxQuantity) {
        if (minQuantity != null && maxQuantity != null) {
            return product.quantity.between(minQuantity, maxQuantity);
        } else if (minQuantity != null) {
            return product.quantity.goe(minQuantity);
        } else if (maxQuantity != null) {
            return product.quantity.loe(maxQuantity);
        } else {
            return null;
        }
    }

    // 삭제되지 않은 데이터 조건
    private BooleanExpression isDeleteFalse() {
        return product.isDelete.isFalse();
    }

    // 정렬 조건 생성
    private List<OrderSpecifier<?>> buildOrderSpecifier(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        // Pageable 의 정렬 조건을 QueryDSL 의 OrderSpecifier 로 변환
        if (pageable.getSort() != null) {
            for (Sort.Order sortOrder : pageable.getSort()) {
                // 정렬 방향 결정 (ASC/DESC)
                com.querydsl.core.types.Order direction = sortOrder.isAscending()
                        ? com.querydsl.core.types.Order.ASC
                        : com.querydsl.core.types.Order.DESC;

                // 정렬 필드에 따른 OrderSpecifier 추가
                switch (sortOrder.getProperty()) {
                    case "name":
                        orders.add(new OrderSpecifier<>(direction, product.name));
                        break;
                    case "price":
                        orders.add(new OrderSpecifier<>(direction, product.price));
                        break;
                    case "quantity":
                        orders.add(new OrderSpecifier<>(direction, product.quantity));
                        break;
                    case "createdAt":
                    default:
                        orders.add(new OrderSpecifier<>(direction, product.createdAt));
                        break;
                }
            }
        }
        return orders;
    }
}
