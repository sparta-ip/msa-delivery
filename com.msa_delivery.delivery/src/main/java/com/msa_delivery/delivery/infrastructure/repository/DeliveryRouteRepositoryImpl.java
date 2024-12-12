package com.msa_delivery.delivery.infrastructure.repository;

import com.msa_delivery.delivery.application.dto.DeliveryRouteDto;
import com.msa_delivery.delivery.domain.model.DeliveryRoute;
import com.msa_delivery.delivery.domain.model.DeliveryStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.msa_delivery.delivery.domain.model.QDeliveryRoute.deliveryRoute;

@RequiredArgsConstructor
public class DeliveryRouteRepositoryImpl implements JpaDeliveryRouteRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DeliveryRouteDto> searchRoutes(String deliveryStatus, UUID departureId, UUID arrivalId,
                                               Long deliveryManagerId, String createdFrom, String createdTo,
                                               Pageable pageable) {

        // 동적 정렬
        List<OrderSpecifier<?>> orderSpecifier = buildOrderSpecifier(pageable);

        // QueryDSL 실행
        QueryResults<DeliveryRoute> results = queryFactory.selectFrom(deliveryRoute)
                .where(eqDeliveryStatus(deliveryStatus),
                        eqDepartureId(departureId),
                        eqArrivalId(arrivalId),
                        eqDeliveryManagerId(deliveryManagerId),
                        createdDateBetween(createdFrom, createdTo),
                        isDeleteFalse()
                )
                .orderBy(orderSpecifier.toArray(new OrderSpecifier[0])) // 정렬 조건 추가
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        // 전체 카운트
        long totalCount = results.getTotal();

        // 검색 결과를 DTO로 변환
        List<DeliveryRouteDto> content = results.getResults().stream()
                .map(DeliveryRouteDto::create)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalCount);
    }

    // 삭제되지 않은 데이터 조건
    private BooleanExpression isDeleteFalse() {
        return deliveryRoute.isDelete.isFalse();
    }

    // 배송 상태 조건
    private BooleanExpression eqDeliveryStatus(String status) {
        return status != null ? deliveryRoute.deliveryStatus.eq(DeliveryStatus.valueOf(status)) : null;
    }

    // 출발지 조건
    private BooleanExpression eqDepartureId(UUID departureId) {
        return departureId != null ? deliveryRoute.departureId.eq(departureId) : null;
    }

    // 도착지 조건
    private BooleanExpression eqArrivalId(UUID arrivalId) {
        return arrivalId != null ? deliveryRoute.arrivalId.eq(arrivalId) : null;
    }

    // 배송 담당자 조건
    private BooleanExpression eqDeliveryManagerId(Long managerId) {
        return managerId != null ? deliveryRoute.deliveryManager.id.eq(managerId) : null;
    }

    // 생성일 범위 조건
    private BooleanExpression createdDateBetween(String createdFrom, String createdTo) {
        LocalDateTime fromDate = createdFrom != null ? LocalDateTime.parse(createdFrom) : null;
        LocalDateTime toDate = createdTo != null ? LocalDateTime.parse(createdTo) : null;

        if (fromDate != null && toDate != null) {
            return deliveryRoute.createdAt.between(fromDate, toDate);
        } else if (fromDate != null) {
            return deliveryRoute.createdAt.goe(fromDate);
        } else if (toDate != null) {
            return deliveryRoute.createdAt.loe(toDate);
        } else {
            return null;
        }
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
                    case "departureId":
                        orders.add(new OrderSpecifier<>(direction, deliveryRoute.departureId));
                        break;
                    case "arrivalId":
                        orders.add(new OrderSpecifier<>(direction, deliveryRoute.arrivalId));
                        break;
                    case "createdAt":
                    default:
                        orders.add(new OrderSpecifier<>(direction, deliveryRoute.createdAt));
                        break;
                }
            }
        }
        return orders;
    }
}
