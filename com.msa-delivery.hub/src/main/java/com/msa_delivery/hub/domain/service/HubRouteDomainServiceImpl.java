package com.msa_delivery.hub.domain.service;

import com.msa_delivery.hub.domain.model.HubRoute;
import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.RouteInfo;
import com.msa_delivery.hub.domain.port.NavigationPort;
import com.msa_delivery.hub.domain.repository.HubRouteRepository;
import com.msa_delivery.hub.domain.repository.HubRouteRepositoryCustom;
import com.msa_delivery.hub.presentation.response.HubRouteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubRouteDomainServiceImpl implements HubRouteDomainService {

    private final HubDomainService hubDomainService;
    private final NavigationPort navigationPort;
    private final HubRouteRepository hubRouteRepository;
    private final HubRouteRepositoryCustom hubRouteRepositoryCustom;

    @Override
    public List<HubRouteResponse> createHubRoute(String userId) {
        List<Hubs> hubsList = hubDomainService.getHubAll();
        List<HubRoute> routes = new ArrayList<>();

        for (int i = 0; i < hubsList.size(); i++) {
            Hubs originHub = hubsList.get(i);
            for (int j = 1+i; j < hubsList.size(); j++) {
                Hubs destHub = hubsList.get(j);

                RouteInfo routeInfo = navigationPort.calculateRouteInfo(originHub.getLocation(), destHub.getLocation());

                HubRoute route = HubRoute.builder()
                        .arrivalHub(originHub)
                        .departureHub(destHub)
                        .distance(routeInfo.distance())
                        .duration(routeInfo.duration())
                        .createdBy(userId)
                        .build();
                routes.add(route);
            }
        }
        hubRouteRepository.saveAll(routes);


        return routes.stream().map(HubRouteResponse::from).toList();
    }

    @Transactional
    @Override
    public List<HubRoute> generateRoutes(Hubs newHub, UUID hubId, String userId) {

        List<Hubs> existingHubs = hubDomainService.getHubByIsDeletedFalse(hubId);

        List<HubRoute> newRoutes = existingHubs.stream()
                .map(existingHub -> {
                    RouteInfo routeInfo = navigationPort.calculateRouteInfo(
                            newHub.getLocation(),
                            existingHub.getLocation()
                    );

                    return HubRoute.builder()
                            .departureHub(newHub)
                            .arrivalHub(existingHub)
                            .distance(routeInfo.distance())
                            .duration(routeInfo.duration())
                            .createdBy(userId)
                            .createdAt(LocalDateTime.now())
                            .build();
                })
                .toList();

        return hubRouteRepository.saveAll(newRoutes);
    }


    @Transactional
    @Override
    public List<HubRoute> updateRelatedRoutes(UUID hubId, String username) {
        List<HubRoute> relatedRoutes = hubRouteRepository
                .findAllByArrivalHubIdOrDepartureHubId(hubId, hubId);

        for (HubRoute route : relatedRoutes) {
            RouteInfo newRouteInfo = navigationPort.calculateRouteInfo(
                    route.getArrivalHub().getLocation(),
                    route.getDepartureHub().getLocation()
            );
            route.updateRouteInfo(
                    newRouteInfo.distance(),
                    newRouteInfo.duration(),
                    username
            );
        }
        return relatedRoutes;
    }
    public void deleteRelatedRoutes(UUID hubId, String userId) {
        LocalDateTime now = LocalDateTime.now();
        hubRouteRepository.softDeleteByHubId(hubId, userId, now);
    }
    @Override
    public HubRoute findHubRouteByDepartureAndArrivalHubId(UUID departureHubId, UUID arrivalHubId) {
        return hubRouteRepositoryCustom.findHubRouteByDepartureAndArrivalHubId(departureHubId, arrivalHubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 허브 경로를 찾을 수 없습니다."));
    }

    public Page<HubRoute> searchHubRoute(UUID hubRoutId, UUID departureHubId, UUID arrivalHubId, Boolean isDeleted, Pageable pageable) {

        return hubRouteRepositoryCustom.searchHubs(hubRoutId, arrivalHubId, departureHubId, isDeleted, pageable);

    }


    @Cacheable(value = "hubRoute", key = "#hubRouteId")
    @Override
    public HubRoute getHubRouteById(UUID hubRouteId) {
        return hubRouteRepository.findById(hubRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 허브 경로를 찾을 수 없습니다."));
    }

}
