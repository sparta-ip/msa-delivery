package com.msa_delivery.hub.domain.service;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.domain.model.HubRoute;
import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.presentation.response.HubRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HubDomainService {

    Page<Hubs> searchHubs(UUID hubId, String name, String address, Long hubManagerId, Boolean isDeleted, Pageable pageable);
    void verifyDuplicatedHub(String name);
    Hubs createHubs(String name, String address, String username);
    Hubs updateHub(UUID hubId, String name, String address, String userId);
    void deleteHubs(UUID hubId, String userId);
    List<Hubs> getHubAll();
    List<Hubs> getHubByIsDeletedFalse(UUID hubId);
    Hubs getHub(UUID hubId);


}
