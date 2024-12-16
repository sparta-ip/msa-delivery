package com.msa_delivery.hub.domain.service;

import com.msa_delivery.hub.domain.model.HubRoute;
import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.presentation.response.HubRouteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HubRouteDomainService {

     List<HubRouteResponse> createHubRoute(Long userId);
     void deleteRelatedRoutes(UUID hubId, Long userId);
     List<HubRoute> generateRoutes(Hubs newHub, UUID hubId, Long userId);
     List<HubRoute> updateRelatedRoutes(UUID hubId, Long userId);
     Page<HubRoute> searchHubRoute(UUID hubRoutId, UUID departureHubId, UUID arrivalHubId, Boolean isDeleted, Pageable pageable);
}
