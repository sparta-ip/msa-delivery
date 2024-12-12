package com.msa_delivery.hub.presentation.controller;

import com.msa_delivery.hub.application.dto.request.CreateHubReqDto;
import com.msa_delivery.hub.application.service.HubApplicationService;
import com.msa_delivery.hub.presentation.common.ApiResponse;
import com.msa_delivery.hub.presentation.response.HubRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hubs")
public class HubController {

    private final HubApplicationService hubApplicationService;


    @PostMapping
    public ApiResponse<HubRes> createHub(@RequestBody CreateHubReqDto createHubReqDto) {
        return ApiResponse.success(hubApplicationService.createHub(createHubReqDto));

//        return ResponseEntity.ok(ApiResponse.<HubRes>builder()
//                .status(200)
//                .message("허브 생성이 완료되었습니다.")
//                .data(hubApplicationService.createHub(createHubReqDto))
//                .build());
    }

    @PatchMapping("/{hubId}")
    public ApiResponse<HubRes> updateHub(@PathVariable UUID hubId, @RequestBody CreateHubReqDto createHubReqDto) {
        return ApiResponse.success(hubApplicationService.updateHub(hubId ,createHubReqDto));
    }

    @DeleteMapping("/{hubId}")
    public ApiResponse<String> deleteHub(@PathVariable UUID hubId , @RequestHeader(value = "X-userId", required = true) Long userId) {
        hubApplicationService.deleteHub(hubId, userId);
        return ApiResponse.success(userId + "허브 필드가 비활성화 되었습니다.");
    }
}
