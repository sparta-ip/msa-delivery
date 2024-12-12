package com.msa_delivery.delivery.infrastructure.repository;

import com.msa_delivery.delivery.application.dto.DeliveryDto;
import com.msa_delivery.delivery.domain.model.Delivery;
import com.msa_delivery.delivery.domain.model.DeliveryStatus;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.msa_delivery.delivery.domain.model.QDelivery.delivery;

@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements JpaDeliveryRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DeliveryDto> searchDeliveries(String search, String deliveryStatus, UUID departureId, UUID arrivalId,
                                              Long deliveryManagerId, Long receiverId, String createdFrom, String createdTo, Pageable pageable) {

        // 동적 정렬
        List<OrderSpecifier<?>> orderSpecifier = buildOrderSpecifier(pageable);

        // QueryDSL 실행
        QueryResults<Delivery> results = queryFactory.selectFrom(delivery)
                .where(nameContains(search),
                        eqDeliveryStatus(deliveryStatus),
                        eqDepartureId(departureId),
                        eqArrivalId(arrivalId),
                        eqDeliveryManagerId(deliveryManagerId),
                        eqReceiverId(receiverId),
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
        List<DeliveryDto> content = results.getResults().stream()
                .map(DeliveryDto::create)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, totalCount);
    }

    // 삭제되지 않은 데이터 조건
    private BooleanExpression isDeleteFalse() {
        return delivery.isDelete.isFalse();
    }

    // 배송 상태 조건
    private BooleanExpression eqDeliveryStatus(String deliveryStatus) {
        return deliveryStatus != null ? delivery.deliveryStatus.eq(DeliveryStatus.valueOf(deliveryStatus)) : null;
    }

    // 출발지 조건
    private BooleanExpression eqDepartureId(UUID departureId) {
        return departureId != null ? delivery.departureId.eq(departureId) : null;
    }

    // 도착지 조건
    private BooleanExpression eqArrivalId(UUID arrivalId) {
        return arrivalId != null ? delivery.arrivalId.eq(arrivalId) : null;
    }

    // 배송 담당자 조건
    private BooleanExpression eqDeliveryManagerId(Long deliveryManagerId) {
        return deliveryManagerId != null ? delivery.deliveryManager.id.eq(deliveryManagerId) : null;
    }

    // 수령인 조건
    private BooleanExpression eqReceiverId(Long receiverId) {
        return receiverId != null ? delivery.receiverId.eq(receiverId) : null;
    }

    // 생성일 범위 조건
    private BooleanExpression createdDateBetween(String createdFrom, String createdTo) {
        LocalDateTime fromDate = createdFrom != null ? LocalDateTime.parse(createdFrom) : null;
        LocalDateTime toDate = createdTo != null ? LocalDateTime.parse(createdTo) : null;

        if (fromDate != null && toDate != null) {
            return delivery.createdAt.between(fromDate, toDate);
        } else if (fromDate != null) {
            return delivery.createdAt.goe(fromDate);
        } else if (toDate != null) {
            return delivery.createdAt.loe(toDate);
        } else {
            return null;
        }
    }

    // 이름 포함 조건 (검색)
    private BooleanExpression nameContains(String search) {
        return search != null ? delivery.address.containsIgnoreCase(search) : null;
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
                    case "address":
                        orders.add(new OrderSpecifier<>(direction, delivery.address));
                        break;
                    case "createdAt":
                    default:
                        orders.add(new OrderSpecifier<>(direction, delivery.createdAt));
                        break;
                }
            }
        }
        return orders;
    }
}
