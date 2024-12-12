package com.msa_delivery.slack_msg.domain.repository;

import com.msa_delivery.slack_msg.domain.model.QSlackMsg;
import com.msa_delivery.slack_msg.domain.model.SlackMsg;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class SlackMsgRepositoryCustomImpl implements SlackMsgRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public SlackMsgRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<SlackMsg> findSlackMsgsWithSearch(Pageable pageable, String search) {

        QSlackMsg slackMsg = QSlackMsg.slackMsg;

        BooleanExpression searchCondition = createSearchCondition(slackMsg, search);

        List<OrderSpecifier> slackMsgSpecifiers = getSlackMsgSpecifiers(pageable.getSort(),
            slackMsg);

        List<SlackMsg> slackMsgs = queryFactory.selectFrom(slackMsg)
            .where(searchCondition)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(slackMsgSpecifiers.toArray(new OrderSpecifier[0]))
            .fetch();

        long total = queryFactory.selectFrom(slackMsg)
            .where(searchCondition)
            .fetchCount();

        return new PageImpl<>(slackMsgs, pageable, total);
    }

    private List<OrderSpecifier> getSlackMsgSpecifiers(Sort sort, QSlackMsg slackMsg) {
        List<OrderSpecifier> slackMsgSpecifiers = new ArrayList<>();

        if (sort.isEmpty()) {
            slackMsgSpecifiers.add(slackMsg.created_at.asc());
            return slackMsgSpecifiers;
        }

        for (Sort.Order s : sort) {
            OrderSpecifier slackMsgSpecifier;

            if (s.getProperty().equals("created_at")) {
                slackMsgSpecifier = s.getDirection().isAscending() ? slackMsg.created_at.asc()
                    : slackMsg.created_at.desc();
            } else {
                slackMsgSpecifier = slackMsg.created_at.asc();
            }

            slackMsgSpecifiers.add(slackMsgSpecifier);
        }

        return slackMsgSpecifiers;
    }

    private BooleanExpression createSearchCondition(QSlackMsg slackMsg, String search) {
        if (search == null || search.isBlank()) {
            return null;
        }

        return slackMsg.receiver_slack_id.containsIgnoreCase(search);
    }
}
