package com.msa_delivery.hub.application.service;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.presentation.response.HubRes;

import java.util.UUID;

public interface HubApplicationService {

    HubRes createHub(CreateHubReqDto createHubReqDto);

    HubRes updateHub(UUID hubId, CreateHubReqDto createHubReqDto);
    void deleteHub(UUID id, Long UserId);
}
