package com.msa_delivery.hub.infrastrcture.repository.impl;

import com.msa_delivery.hub.domain.model.HubRoute;
import com.msa_delivery.hub.domain.repository.HubRouteRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.msa_delivery.hub.domain.model.QHubRoute.hubRoute;

@Repository
public class HubRouteRepositoryCustomImpl extends QuerydslRepositorySupport implements HubRouteRepositoryCustom {


    public HubRouteRepositoryCustomImpl() {
        super(HubRoute.class);
    }

    @Override
    public Page<HubRoute> searchHubs(UUID hubRouteId, UUID arrivalId, UUID departureId, Boolean isDeleted, Pageable pageable) {
        JPQLQuery<HubRoute> query = from(hubRoute)
                .where(
                    routeIdEq(hubRouteId),
                        arrivalIdEq(arrivalId),
                        departureIdEq(departureId),
                        isDeletedEq(isDeleted)
                );

        List<HubRoute> content = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(content, pageable,query.fetchCount());
    }

    public BooleanExpression routeIdEq(UUID hubRouteId) {
        return hubRouteId != null ? hubRoute.hubRouteId.eq(hubRouteId) : null;
    }
    public BooleanExpression arrivalIdEq(UUID arrivalId) {
        return arrivalId != null ? hubRoute.arrivalHub.hubId.eq(arrivalId) : null;
    }
    public BooleanExpression departureIdEq(UUID departureId) {
        return departureId != null ? hubRoute.departureHub.hubId.eq(departureId) : null;
    }
    public BooleanExpression isDeletedEq(Boolean isDeleted) {
        return isDeleted != null ? hubRoute.isDeleted.eq(isDeleted) : null;
    }
}
