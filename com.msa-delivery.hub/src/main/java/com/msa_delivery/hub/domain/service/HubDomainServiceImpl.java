package com.msa_delivery.hub.domain.service;


import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.Location;
import com.msa_delivery.hub.domain.port.GeoCodingPort;
import com.msa_delivery.hub.domain.repository.HubWriteRepository;
import com.msa_delivery.hub.domain.repository.HubReadRepository;
import com.msa_delivery.hub.presentation.response.HubRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubDomainServiceImpl implements HubDomainService {

    private final HubReadRepository hubReadRepository;
    private final GeoCodingPort geoCodingPort;
    private final HubWriteRepository hubWriteRepository;

    @Transactional
    public HubRes createHubs(CreateHubReqDto reqDto) {
        verifyDuplicatedHub(reqDto.getHub().getName());
        Hubs hub = reqDto.toEntity("master", geoCodingPort.getGeocode(reqDto.getHub().getAddress()));
        Hubs savedHub = hubWriteRepository.save(hub);
        return HubRes.from(savedHub);
    }

    @Override
    public void verifyDuplicatedHub(String name) {
        if (hubReadRepository.existsByNameAndIsDeletedFalse(name)) {
            throw new IllegalArgumentException("이미 존재하는 허브 입니다.");
        }
    }
    @Transactional
    @Override
    public HubRes updateHubs(UUID hubId, CreateHubReqDto reqDto) {
        return hubReadRepository.findByHubId(hubId).map(hubs -> {
            if (!hubs.getAddress().equals(reqDto.getHub().getAddress())) {
                Location newLocation = geoCodingPort.getGeocode(reqDto.getHub().getAddress());
                hubs.updateHubData(reqDto, reqDto.getHub().getAddress(), newLocation);
                return HubRes.from(hubs);
            } else {
                throw new IllegalArgumentException("현재 주소와 동일한 주소 입니다.");
            }
        }).orElseThrow(() -> new IllegalArgumentException("해당 주소의 허브를 찾을 수 없습니다."));
    }
    @Transactional
    @Override
    public void deleteHubs(UUID hubId, Long userId) {
        Hubs hubs = hubReadRepository.findByHubId(hubId).orElseThrow(() -> new IllegalArgumentException("허브를 찾을 수 없습니다."));
        LocalDateTime localDateTime = LocalDateTime.now();
        hubWriteRepository.updateHubToDeleted(hubId, localDateTime, userId);
    }

}
