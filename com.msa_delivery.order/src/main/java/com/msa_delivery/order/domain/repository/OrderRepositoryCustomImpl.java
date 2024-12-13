package com.msa_delivery.order.domain.repository;

import com.msa_delivery.order.domain.model.Order;
import com.msa_delivery.order.domain.model.QOrder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Order> findOrdersWithSearch(Pageable pageable, String search) {
        QOrder order = QOrder.order;

        BooleanExpression searchCondition = createSearchCondition(order, search);

        List<OrderSpecifier> orderSpecifiers = getOrderSpecifiers(pageable.getSort(), order);

        List<Order> orders = queryFactory.selectFrom(order)
            .where(searchCondition)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .fetch();

        long total = queryFactory.selectFrom(order)
            .where(searchCondition)
            .fetchCount();

        return new PageImpl<>(orders, pageable, total);
    }

    // Sort 객체를 OrderSpecifier로 변환하여 정렬 기준 적용
    private List<OrderSpecifier> getOrderSpecifiers(Sort sort, QOrder order) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        // 정렬 기준이 없다면 기본값 설정(created_at 기준으로 오름차순 정렬)
        if (sort.isEmpty()) {
            orderSpecifiers.add(order.created_at.asc());
            return orderSpecifiers;
        }

        for (Sort.Order o : sort) {
            OrderSpecifier orderSpecifier;

            if (o.getProperty().equals("receiver_id")) {
                orderSpecifier = o.getDirection().isAscending() ? order.receiver_id.asc() : order.receiver_id.desc();
            } else if (o.getProperty().equals("supplier_id")) {
                orderSpecifier = o.getDirection().isAscending() ? order.supplier_id.asc() : order.supplier_id.desc();
            } else if (o.getProperty().equals("created_at")) {
                orderSpecifier = o.getDirection().isAscending() ? order.created_at.asc() : order.created_at.desc();
            } else {
                // 기본 정렬은 created_at을 기준으로 설정
                orderSpecifier = order.created_at.asc();
            }

            orderSpecifiers.add(orderSpecifier);
        }

        return orderSpecifiers;
    }

    // 검색어 필터링
    private BooleanExpression createSearchCondition(QOrder order, String search) {
        if (search == null || search.isBlank()) {
            return null;
        }

        // UUID를 String으로 변환한 값을 사용하여 containsIgnoreCase
        return Expressions.stringTemplate("{0}", order.receiver_id)
            .containsIgnoreCase(search)
            .or(Expressions.stringTemplate("{0}", order.supplier_id)
                .containsIgnoreCase(search));
    }
}
