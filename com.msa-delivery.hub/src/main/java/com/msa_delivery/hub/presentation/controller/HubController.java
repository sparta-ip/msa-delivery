package com.msa_delivery.hub.presentation.controller;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.application.dto.response.CreateHubResDto;
import com.msa_delivery.hub.application.service.HubApplicationService;
import com.msa_delivery.hub.presentation.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hubs")
public class HubController {

  private final HubApplicationService hubApplicationService;
    @PostMapping
    public ResponseEntity<ApiResponse<CreateHubResDto>> createHub(@RequestBody CreateHubReqDto createHubReqDto) {
        return hubApplicationService.createHub(createHubReqDto);
    }
}
