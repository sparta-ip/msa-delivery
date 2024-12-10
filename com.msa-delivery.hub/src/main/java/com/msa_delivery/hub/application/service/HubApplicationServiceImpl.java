package com.msa_delivery.hub.application.service;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.application.dto.response.CreateHubResDto;
import com.msa_delivery.hub.domain.model.Hubs;
import com.msa_delivery.hub.domain.model.LocationVO;
import com.msa_delivery.hub.domain.repository.HubCRUDRepository;
import com.msa_delivery.hub.domain.service.HubDomainService;
import com.msa_delivery.hub.infrastrcture.kakao.KaKaoMapClient;
import com.msa_delivery.hub.infrastrcture.kakao.dto.KaKaoGeoResponse;
import com.msa_delivery.hub.presentation.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubApplicationServiceImpl implements HubApplicationService {

    private final KaKaoMapClient kaKaoMapClient;
    private final HubCRUDRepository hubRepository;
    private final HubDomainService domainService;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<CreateHubResDto>> createHub(CreateHubReqDto createHubReqDto) {
        domainService.verifyDuplicatedHub(createHubReqDto.getHub().getName());
        KaKaoGeoResponse geoResponse = kaKaoMapClient.convertAddressToGeocode(createHubReqDto.getHub().getAddress());
        if (geoResponse.getDocuments().isEmpty()) {
            throw new IllegalArgumentException("주소를 찾을 수 없습니다.");
        }
        LocationVO location = geoResponse.getDocuments().get(0).toLocation();
        Hubs hub = createHubReqDto.toEntity("master", location);
        Hubs savedHub = hubRepository.save(hub);
        return ResponseEntity.ok(ApiResponse.<CreateHubResDto>builder()
                .status(200)
                .message("허브 생성이 완료되었습니다.")
                .data(CreateHubResDto.of(savedHub))
                .build());

    }
}
