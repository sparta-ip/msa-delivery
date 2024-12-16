package com.msa_delivery.hub.application.service;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.application.dto.request.HubSearch;
import com.msa_delivery.hub.presentation.response.HubRes;
import com.msa_delivery.hub.presentation.response.HubWithRoutesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubApplicationService {

    Page<HubRes> searchHubs(HubSearch hubSearch, Pageable pageable);

    HubWithRoutesResponse createHubWithRoutes(CreateHubReqDto createHubReqDto, Long userId);

    HubWithRoutesResponse updateHub(UUID hubId, CreateHubReqDto createHubReqDto, Long userId);

    void deleteHub(UUID id, Long UserId);
}
