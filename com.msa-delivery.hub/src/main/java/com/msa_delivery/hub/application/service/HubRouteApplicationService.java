package com.msa_delivery.hub.application.service;


import com.msa_delivery.hub.application.dto.request.HubRouteSearch;
import com.msa_delivery.hub.domain.model.HubRoute;
import com.msa_delivery.hub.presentation.response.HubRouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


public interface HubRouteApplicationService {

    List<HubRouteResponse> createHubRouteList(String userId);
    Page<HubRouteResponse> searchHubRouteList(HubRouteSearch hubRouteSearch, Pageable pageable);
    HubRouteResponse getHubRouteById(UUID hubRouteId);


    HubRoute findHubRouteByDepartureAndArrivalHubId(UUID departureHubId, UUID arrivalHubId);
}
