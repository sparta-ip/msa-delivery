package com.msa_delivery.hub.application.service;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.application.dto.response.CreateHubResDto;
import com.msa_delivery.hub.presentation.common.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface HubApplicationService {

    ResponseEntity<ApiResponse<CreateHubResDto>> createHub(CreateHubReqDto createHubReqDto);
}
