package com.msa_delivery.hub.application.service;


import com.msa_delivery.hub.application.dto.request.HubRouteSearch;
import com.msa_delivery.hub.domain.model.HubRoute;
import com.msa_delivery.hub.domain.service.HubRouteDomainService;
import com.msa_delivery.hub.presentation.response.HubRouteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class HubRouteApplicationServiceImpl implements HubRouteApplicationService{

    private final HubRouteDomainService hubRouteDomainService;

    @Override
    public List<HubRouteResponse> createHubRouteList(String userId) {
        return hubRouteDomainService.createHubRoute(userId);
    }

    @Override
    public Page<HubRouteResponse> searchHubRouteList(HubRouteSearch hubRouteSearch, Pageable pageable) {
        Page<HubRoute> hubRoutePage = hubRouteDomainService.searchHubRoute(
                hubRouteSearch.hubRouteId(),
                hubRouteSearch.departureHubId(),
                hubRouteSearch.arrivalHubId(),
                hubRouteSearch.isDeleted(),
                pageable
        );
        return hubRoutePage.map(HubRouteResponse::from);
    }


    @Override
    public HubRouteResponse getHubRouteById(UUID hubRouteId) {
        return HubRouteResponse.from(hubRouteDomainService.getHubRouteById(hubRouteId));
    }

}
