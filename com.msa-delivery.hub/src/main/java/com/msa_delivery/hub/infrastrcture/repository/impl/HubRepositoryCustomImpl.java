package com.msa_delivery.hub.infrastrcture.repository.impl;

import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.QHubs;
import com.msa_delivery.hub.domain.repository.HubRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

import static com.msa_delivery.hub.domain.model.QHubs.hubs;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class HubRepositoryCustomImpl extends QuerydslRepositorySupport implements HubRepositoryCustom {


    public HubRepositoryCustomImpl() {
        super(Hubs.class);
    }

    @Override
    public Page<Hubs> searchHubs(UUID hubId, String name, String address, Long hubManagerId, Boolean isDeleted, Pageable pageable) {
        JPQLQuery<Hubs> query = from(hubs)
                .where(
                        hubIdEq(hubId),
                        nameContains(name),
                        addressContains(address),
                        hubManagerIdEq(hubManagerId),
                        isDeletedEq(isDeleted)
                );

        List<Hubs> content = getQuerydsl()
                .applyPagination(pageable, query)
                .fetch();

        return new PageImpl<>(content, pageable, query.fetchCount());
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? hubs.hubId.eq(hubId) : null;
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? hubs.name.contains(name) : null;
    }

    private BooleanExpression addressContains(String address) {
        return hasText(address) ? hubs.address.contains(address) : null;
    }

    private BooleanExpression hubManagerIdEq(Long hubManagerId) {
        return hubManagerId != null ? hubs.hubManagerId.eq(hubManagerId) : null;
    }

    private BooleanExpression isDeletedEq(Boolean isDeleted) {
        return isDeleted != null ? hubs.isDeleted.eq(isDeleted) : null;
    }
}
