package com.msa_delivery.hub.domain.service;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.presentation.response.HubRes;

import java.util.UUID;

public interface HubDomainService {

    void verifyDuplicatedHub(String name);

    HubRes createHubs(CreateHubReqDto reqDto);
    HubRes updateHubs(UUID hubId, CreateHubReqDto reqDto);
    void deleteHubs(UUID hubId, Long userId);
}
