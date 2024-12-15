package com.msa_delivery.hub.application.service;


import com.msa_delivery.hub.application.dto.request.HubRouteSearch;
import com.msa_delivery.hub.presentation.response.HubRouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface HubRouteApplicationService {

    List<HubRouteResponse> createHubRouteList(Long userId);
    Page<HubRouteResponse> searchHubRouteList(HubRouteSearch hubRouteSearch, Pageable pageable);
}
