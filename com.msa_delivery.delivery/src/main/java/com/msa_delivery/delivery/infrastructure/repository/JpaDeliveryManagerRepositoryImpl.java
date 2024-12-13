package com.msa_delivery.delivery.infrastructure.repository;

import com.msa_delivery.delivery.application.dto.DeliveryManagerDto;
import com.msa_delivery.delivery.domain.model.DeliveryManager;
import com.msa_delivery.delivery.domain.model.DeliveryManagerType;
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

import static com.msa_delivery.delivery.domain.model.QDeliveryManager.deliveryManager;

@RequiredArgsConstructor
public class JpaDeliveryManagerRepositoryImpl implements JpaDeliveryManagerRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DeliveryManagerDto> searchManagers(String search, String type, UUID hubId, UUID orderId, Integer sequenceMin, Integer sequenceMax,
                                                   String createdFrom, String createdTo, Pageable pageable) {
        // 동적 정렬
        List<OrderSpecifier<?>> orderSpecifier = buildOrderSpecifier(pageable);

        // QueryDSL 실행
        QueryResults<DeliveryManager> results = queryFactory.selectFrom(deliveryManager)
                .where(nameContains(search),
                        eqType(type),
                        eqHubId(hubId),
                        eqOrderId(orderId),
                        sequenceBetween(sequenceMin, sequenceMax),
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
        List<DeliveryManagerDto> content = results.getResults().stream()
                .map(DeliveryManagerDto::create)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalCount);
    }

    // 삭제되지 않은 데이터 조건
    private BooleanExpression isDeleteFalse() {
        return deliveryManager.isDelete.isFalse();
    }

    // 타입 조건
    private BooleanExpression eqType(String type) {
        return type != null ? deliveryManager.type.eq(DeliveryManagerType.fromString(type)) : null;
    }

    // 허브 ID 조건
    private BooleanExpression eqHubId(UUID hubId) {
        return hubId != null ? deliveryManager.hubId.eq(hubId) : null;
    }

    // 허브 ID 조건
    private BooleanExpression eqOrderId(UUID orderId) {
        return orderId != null ? deliveryManager.orderId.eq(orderId) : null;
    }

    // 이름 포함 조건 (검색)
    private BooleanExpression nameContains(String search) {
        return search != null ? deliveryManager.slackId.containsIgnoreCase(search) : null;
    }

    // 시퀀스 범위 조건
    private BooleanExpression sequenceBetween(Integer sequenceMin, Integer sequenceMax) {
        if (sequenceMin != null && sequenceMax != null) {
            return deliveryManager.sequence.between(sequenceMin, sequenceMax);
        } else if (sequenceMin != null) {
            return deliveryManager.sequence.goe(sequenceMin);
        } else if (sequenceMax != null) {
            return deliveryManager.sequence.loe(sequenceMax);
        } else {
            return null;
        }
    }

    // 생성일 범위 조건
    private BooleanExpression createdDateBetween(String createdFrom, String createdTo) {
        LocalDateTime fromDate = createdFrom != null ? LocalDateTime.parse(createdFrom) : null;
        LocalDateTime toDate = createdTo != null ? LocalDateTime.parse(createdTo) : null;

        if (fromDate != null && toDate != null) {
            return deliveryManager.createdAt.between(fromDate, toDate);
        } else if (fromDate != null) {
            return deliveryManager.createdAt.goe(fromDate);
        } else if (toDate != null) {
            return deliveryManager.createdAt.loe(toDate);
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
                    case "sequence":
                        orders.add(new OrderSpecifier<>(direction, deliveryManager.sequence));
                        break;
                    case "type":
                        orders.add(new OrderSpecifier<>(direction, deliveryManager.type));
                        break;
                    case "createdAt":
                    default:
                        orders.add(new OrderSpecifier<>(direction, deliveryManager.createdAt));
                        break;
                }
            }
        }
        return orders;
    }
}
