package com.msa_delivery.hub.application.service;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.application.dto.request.HubSearch;
import com.msa_delivery.hub.domain.model.HubRoute;
import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.service.HubRouteDomainService;
import com.msa_delivery.hub.presentation.response.HubRes;
import com.msa_delivery.hub.domain.service.HubDomainService;
import com.msa_delivery.hub.presentation.response.HubRouteResponse;
import com.msa_delivery.hub.presentation.response.HubWithRoutesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubApplicationServiceImpl implements HubApplicationService {


    private final HubDomainService hubDomainService;
    private final HubRouteDomainService hubRouteDomainService;


    @Transactional
    @Override
    public HubWithRoutesResponse createHubWithRoutes(CreateHubReqDto createHubReqDto, Long userId) {
        Hubs createdHub = hubDomainService.createHubs(
                createHubReqDto.getHub().getName(),
                createHubReqDto.getHub().getAddress(),
                userId
        );
        List<HubRoute> newRoutes = hubRouteDomainService.generateRoutes(createdHub, createdHub.getHubId(), userId);

        return new HubWithRoutesResponse(
                HubRes.from(createdHub),
                newRoutes.stream()
                        .map(HubRouteResponse::from)
                        .toList()
        );
    }

    @Transactional
    @Override
    public HubWithRoutesResponse updateHub(UUID hubId , CreateHubReqDto createHubReqDto, Long userId) {

        Hubs updatedHub = hubDomainService.updateHub(
                hubId,
                createHubReqDto.getHub().getName(),
                createHubReqDto.getHub().getAddress(),
                userId);
        List<HubRoute> updatedRoutes = hubRouteDomainService.updateRelatedRoutes(hubId, userId);

        return new HubWithRoutesResponse(HubRes.from(updatedHub), updatedRoutes.stream().map(HubRouteResponse::from).toList());
    }

    @Transactional
    @Override
    public void deleteHub(UUID hubId, Long userId) {
        hubDomainService.deleteHubs(hubId, userId);
        hubRouteDomainService.deleteRelatedRoutes(hubId, userId);
    }

    public Page<HubRes> searchHubs(HubSearch hubSearch, Pageable pageable) {
        Page<Hubs> hubsPage = hubDomainService.searchHubs(
                hubSearch.hubId(),
                hubSearch.name(),
                hubSearch.address(),
                hubSearch.hubManagerId(),
                hubSearch.isDeleted(),
                pageable
        );

        return hubsPage.map(HubRes::from);
    }


}
