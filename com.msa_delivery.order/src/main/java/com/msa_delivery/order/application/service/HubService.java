package com.msa_delivery.order.application.service;

import com.msa_delivery.order.application.dto.HubDataDto;
import com.msa_delivery.order.application.dto.ResponseDto;
import com.msa_delivery.order.application.feign.HubClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubClient hubClient;

    // 허브 정보 조회
    public HubDataDto getHub(UUID hub_id) {
        ResponseDto<HubDataDto> response = hubClient.getHub(hub_id);
        if (response.getData() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 허브입니다.");
        }
        return response.getData();
    }

}
