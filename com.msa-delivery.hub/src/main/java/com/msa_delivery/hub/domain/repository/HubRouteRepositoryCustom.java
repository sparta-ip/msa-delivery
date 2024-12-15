package com.msa_delivery.hub.domain.repository;

import com.msa_delivery.hub.domain.model.HubRoute;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubRouteRepositoryCustom {


    Page<HubRoute> searchHubs(UUID routeId, UUID arrivalId, UUID departureId, Boolean isDeleted, Pageable pageable);
}
