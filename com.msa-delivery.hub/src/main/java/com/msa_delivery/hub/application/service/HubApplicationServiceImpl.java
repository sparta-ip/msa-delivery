package com.msa_delivery.hub.application.service;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.presentation.response.HubRes;
import com.msa_delivery.hub.domain.service.HubDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubApplicationServiceImpl implements HubApplicationService {


    private final HubDomainService domainService;

    @Override
    public HubRes createHub(CreateHubReqDto createHubReqDto) {
        return domainService.createHubs(createHubReqDto);
    }

    @Override
    public HubRes updateHub(UUID hubId , CreateHubReqDto createHubReqDto) {
        return domainService.updateHubs(hubId,createHubReqDto);
    }

    @Override
    public void deleteHub(UUID hubId, Long userId) {
        domainService.deleteHubs(hubId, userId);
    }
}
